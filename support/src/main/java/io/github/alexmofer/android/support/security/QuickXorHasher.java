package io.github.alexmofer.android.support.security;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Java implementation of the Microsoft Proprietary QuickXorHash algorithm.
 * <p>
 * A quick, simple non-cryptographic hash algorithm that works by XORing the bytes in a circular-shifting fashion.
 * A high level description of the algorithm without the introduction of the length is as follows:
 * <p>
 * Let's say a "block" is a 160 bit block of bits (e.g. byte[20]).
 *
 * <pre>
 *   method block zero():
 *     returns a block with all zero bits.
 *   method block extend8(byte b):
 *     returns a block with all zero bits except for the lower 8 bits
 *         which come from b.
 *   method block extend64(int64 i):
 *     returns a block of all zero bits except for the lower 64 bits
 *         which come from i.
 *   method block rotate(block bl, int n):
 *     returns bl rotated left by n bits.
 *   method block xor(block bl1, block bl2):
 *     returns a bitwise xor of bl1 with bl2
 *   method block XorHash0(byte rgb[], int cb):
 *     block ret = zero()
 *     for (int i = 0; i &lt; cb; i ++)
 *     ret = xor(ret, rotate(extend8(rgb[i]), i * 11))
 *     returns ret
 *   entrypoint block XorHash(byte rgb[], int cb):
 *     returns xor(extend64(cb), XorHash0(rgb, cb))
 * </pre>
 * <p>
 * The final hash should XOR the length of the data with the least significant bits of the resulting block.
 *
 * @see <a href="https://docs.microsoft.com/en-us/onedrive/developer/code-snippets/quickxorhash?view=odsp-graph-online">Code Snippets: QuickXorHash Algorithm</a>
 */
@SuppressWarnings("ALL")
public final class QuickXorHasher extends MessageDigest implements Cloneable {
    private final static int BITS_IN_LAST_CELL = 32;
    private final static byte SHIFT = 11;
    private final static int WIDTH_IN_BITS = 160;

    private long[] data;
    private long lengthSoFar;
    private int shiftSoFar;

    /**
     * NOTE: This QuickXorHash is derived from Microsoft's implementation.
     * However, it has not been properly scrutinized via an official security audit.
     * Use with caution as NO claim to reliability, accuracy, or security is being stated or implied.
     */
    public QuickXorHasher() throws NoSuchAlgorithmException {
        super("QuickXorHash");
        engineReset();
    }

    /**
     * 获取文件 Hash
     *
     * @param file 文件
     * @return 文件 Hash
     * @throws Exception 错误信息
     */
    public static String getHash(File file) throws Exception {
        final QuickXorHasher hasher = new QuickXorHasher();
        final byte[] buffer = new byte[1024];
        try (final InputStream input = Files.newInputStream(file.toPath())) {
            while (true) {
                final int len = input.read(buffer);
                if (len < 0) break;  // EOF
                hasher.update(buffer, 0, len);
            }
        }
        return hasher.digestToString();
    }

    /**
     * Converted from Microsoft's QuickXORHash snippet's HashFinal method
     * <p>
     * Converts the data into a byte array, and then XORs the file length with the least significant bits.
     *
     * @return The data converted into a byte array after XOR transformation.
     */
    @Override
    protected byte[] engineDigest() {
        // How many bytes are in a long.
        final int longByteLength = Long.SIZE / Byte.SIZE;

        // Create a ByteBuffer and allocate enough room to hold our data.
        ByteBuffer buffer = ByteBuffer.allocate((WIDTH_IN_BITS - 1) / longByteLength + 1);

        // We want our buffer to work in little endian order so that the longs do not have to be reversed.
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Block copy all of our bit vectors into this ByteBuffer.
        for (int i = 0; i < this.data.length; i++) {
            if (i != this.data.length - 1) {
                buffer.putLong(i * longByteLength, this.data[i]);
            } else {
                // We only want four bytes of the last data part.
                // NOTE: This may be different if the WIDTH_IN_BITS was to be changed.
                buffer.putInt(i * longByteLength, (int) this.data[i]);
            }
        }

        // XOR the file length with the least significant bits.
        final int baseIndex = (WIDTH_IN_BITS / longByteLength) - longByteLength;
        buffer.putLong(baseIndex, buffer.getLong(baseIndex) ^ lengthSoFar);

        return buffer.array();
    }

    /**
     * Converted from Microsoft's QuickXORHash snippet's Initialize method.
     */
    @Override
    protected void engineReset() {
        this.data = new long[(WIDTH_IN_BITS - 1) / 64 + 1];
        this.shiftSoFar = 0;
        this.lengthSoFar = 0;
    }

    /**
     * Updates the digest using the specified byte.
     *
     * @param singleByte The byte to use for the update.
     */
    @Override
    protected void engineUpdate(byte singleByte) {
        byte[] array = new byte[1];
        array[0] = singleByte;
        this.engineUpdate(array, 0, 1);
    }

    /**
     * Converted from Microsoft's QuickXORHash snippet's HashCore method.
     * <p>
     * Note: the C# implementation allows for unchecked operation and Java
     * does not provide an automatic overflow or loss of info checking.
     *
     * @param array   The input to compute the hash code for.
     * @param ibStart The offset into the byte array from which to begin using data.
     * @param cbSize  The number of bytes in the byte array to use as data.
     */
    @Override
    protected void engineUpdate(byte[] array, int ibStart, int cbSize) {
        int currentShift = this.shiftSoFar;

        // The bit vector where we'll start XORing.
        int vectorArrayIndex = currentShift / 64;

        // The position within the bit vector at which we begin XORing.
        int vectorOffset = currentShift % 64;
        int iterations = Math.min(cbSize, WIDTH_IN_BITS);

        for (int i = 0; i < iterations; i++) {
            boolean isLastCell = vectorArrayIndex == this.data.length - 1;
            int bitsInVectorCell = isLastCell ? BITS_IN_LAST_CELL : 64;

            // There's at least 2 bit vectors before we reach the end of the array.
            if (vectorOffset <= bitsInVectorCell - 8) {
                for (int j = ibStart + i; j < cbSize + ibStart; j += WIDTH_IN_BITS) {
                    this.data[vectorArrayIndex] ^= ((long) array[j] & 0xff) << vectorOffset;
                }
            } else {
                int index1 = vectorArrayIndex;
                int index2 = isLastCell ? 0 : (vectorArrayIndex + 1);
                byte low = (byte) (bitsInVectorCell - vectorOffset);

                long xoredByte = 0;
                for (int j = ibStart + i; j < cbSize + ibStart; j += WIDTH_IN_BITS) {
                    xoredByte ^= ((long) array[j] & 0xff);
                }
                this.data[index1] ^= xoredByte << vectorOffset;
                this.data[index2] ^= xoredByte >> low;

            }
            vectorOffset += SHIFT;
            while (vectorOffset >= bitsInVectorCell) {
                vectorArrayIndex = isLastCell ? 0 : vectorArrayIndex + 1;
                vectorOffset -= bitsInVectorCell;
            }
        }

        // Update the starting position in a circular shift pattern.
        this.shiftSoFar = (this.shiftSoFar + SHIFT * (cbSize % WIDTH_IN_BITS)) % WIDTH_IN_BITS;

        this.lengthSoFar += cbSize;
    }

    @NonNull
    @Override
    public QuickXorHasher clone() throws CloneNotSupportedException {
        return (QuickXorHasher) super.clone();
    }

    /**
     * 输出字符串摘要
     * @return 字符串摘要
     */
    public String digestToString() {
        return Base64.getEncoder().encodeToString(engineDigest());
    }
}

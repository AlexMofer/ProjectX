package io.github.alexmofer.android.support.security;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Computes a hash using the same algorithm that the Dropbox API uses for the
 * the "content_hash" metadata field.
 *
 * <p>
 * The {@link #digest()} method returns a raw binary representation of the hash.
 * The "content_hash" field in the Dropbox API is a hexadecimal-encoded version
 * of the digest.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * MessageDigest hasher = new DropboxContentHasher();
 * byte[] buf = new byte[1024];
 * InputStream in = new FileInputStream("some-file");
 * try {
 *     while (true) {
 *         int n = in.read(buf);
 *         if (n &lt; 0) break;  // EOF
 *         hasher.update(buf, 0, n);
 *     }
 * }
 * finally {
 *     in.close();
 * }
 *
 * byte[] rawHash = hasher.digest();
 * System.out.println(hex(rawHash));
 *     // Assuming 'hex' is a method that converts a byte[] to
 *     // a hexadecimal-encoded String
 * </pre>
 *
 * <p>
 * If you need to hash something as it passes through a stream, you can use the
 * {@link java.security.DigestInputStream} or {@code java.security.DigestOutputStream} helpers.
 * </p>
 *
 * <pre>
 * MessageDigest hasher = new DropboxContentHasher();
 * InputStream in = new FileInputStream("some-file");
 * UploadResponse r;
 * try {
 *     r = someApiClient.upload(new DigestInputStream(in, hasher)));
 * }
 * finally {
 *     in.close();
 * }
 *
 * String locallyComputed = hex(hasher.digest());
 * assert r.contentHash.equals(locallyComputed);
 * </pre>
 */
public final class DropboxContentHasher extends MessageDigest implements Cloneable {
    public static final int BLOCK_SIZE = 4 * 1024 * 1024;
    private MessageDigest overallHasher;
    private MessageDigest blockHasher;
    private int blockPos = 0;

    public DropboxContentHasher() throws NoSuchAlgorithmException {
        this(MessageDigest.getInstance("SHA-256"), MessageDigest.getInstance("SHA-256"), 0);
    }

    private DropboxContentHasher(MessageDigest overallHasher, MessageDigest blockHasher, int blockPos) {
        super("Dropbox-Content-Hash");
        this.overallHasher = overallHasher;
        this.blockHasher = blockHasher;
        this.blockPos = blockPos;
    }

    /**
     * 获取文件 Hash
     *
     * @param file 文件
     * @return 文件 Hash
     * @throws Exception 错误信息
     */
    public static String getHash(File file) throws Exception {
        final DropboxContentHasher hasher = new DropboxContentHasher();
        final byte[] buffer = new byte[1024];
        //noinspection IOStreamConstructor
        try (final InputStream input = new FileInputStream(file)) {
            while (true) {
                final int len = input.read(buffer);
                if (len < 0) break;  // EOF
                hasher.update(buffer, 0, len);
            }
        }
        return hasher.digestToString();
    }

    @Override
    protected void engineUpdate(byte input) {
        finishBlockIfFull();

        blockHasher.update(input);
        blockPos += 1;
    }

    @Override
    protected int engineGetDigestLength() {
        return overallHasher.getDigestLength();
    }

    @Override
    protected void engineUpdate(byte[] input, int offset, int len) {
        int inputEnd = offset + len;
        while (offset < inputEnd) {
            finishBlockIfFull();

            int spaceInBlock = BLOCK_SIZE - this.blockPos;
            int inputPartEnd = Math.min(inputEnd, offset + spaceInBlock);
            int inputPartLength = inputPartEnd - offset;
            blockHasher.update(input, offset, inputPartLength);

            blockPos += inputPartLength;
            offset += inputPartLength;
        }
    }

    @Override
    protected void engineUpdate(ByteBuffer input) {
        int inputEnd = input.limit();
        while (input.position() < inputEnd) {
            finishBlockIfFull();

            int spaceInBlock = BLOCK_SIZE - this.blockPos;
            int inputPartEnd = Math.min(inputEnd, input.position() + spaceInBlock);
            int inputPartLength = inputPartEnd - input.position();
            input.limit(inputPartEnd);
            blockHasher.update(input);

            blockPos += inputPartLength;
            input.position(inputPartEnd);
        }
    }

    @Override
    protected byte[] engineDigest() {
        finishBlockIfNonEmpty();
        return overallHasher.digest();
    }

    @Override
    protected int engineDigest(byte[] buf, int offset, int len)
            throws DigestException {
        finishBlockIfNonEmpty();
        return overallHasher.digest(buf, offset, len);
    }

    @Override
    protected void engineReset() {
        this.overallHasher.reset();
        this.blockHasher.reset();
        this.blockPos = 0;
    }

    @NonNull
    @Override
    public DropboxContentHasher clone()
            throws CloneNotSupportedException {
        DropboxContentHasher clone = (DropboxContentHasher) super.clone();
        clone.overallHasher = (MessageDigest) clone.overallHasher.clone();
        clone.blockHasher = (MessageDigest) clone.blockHasher.clone();
        return clone;
    }

    /**
     * 输出字符串摘要
     * @return 字符串摘要
     */
    public String digestToString() {
        return MessageDigestUtils.toHexString(digest(), 0);
    }

    private void finishBlock() {
        overallHasher.update(blockHasher.digest());
        blockPos = 0;
    }

    private void finishBlockIfFull() {
        if (blockPos == BLOCK_SIZE) {
            finishBlock();
        }
    }

    private void finishBlockIfNonEmpty() {
        if (blockPos > 0) {
            finishBlock();
        }
    }
}

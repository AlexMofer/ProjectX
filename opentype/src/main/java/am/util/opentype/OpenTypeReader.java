/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.util.opentype;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads a OpenType font file.
 * Created by Alex on 2018/9/5.
 */
@SuppressWarnings("all")
public interface OpenTypeReader extends Closeable {

    /**
     * Sets the file-pointer offset, measured from the beginning of this
     * file, at which the next read or write occurs.  The offset may be
     * set beyond the end of the file. Setting the offset beyond the end
     * of the file does not change the file length.  The file length will
     * change only by writing after the offset has been set beyond the end
     * of the file.
     *
     * @param pos the offset position, measured in bytes from the
     *            beginning of the file, at which to set the file
     *            pointer.
     * @throws IOException if {@code pos} is less than
     *                     {@code 0} or if an I/O error occurs.
     */
    void seek(long pos) throws IOException;

    /**
     * Attempts to skip over {@code n} bytes of input discarding the
     * skipped bytes.
     *
     * This method may skip over some smaller number of bytes, possibly zero.
     * This may result from any of a number of conditions; reaching end of
     * file before {@code n} bytes have been skipped is only one
     * possibility. This method never throws an {@code EOFException}.
     * The actual number of bytes skipped is returned.  If {@code n}
     * is negative, no bytes are skipped.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws IOException if an I/O error occurs.
     */
    long skip(long n) throws IOException;

    /**
     * Returns the current offset in this file.
     *
     * @return the offset from the beginning of the file, in bytes,
     * at which the next read or write occurs.
     * @throws IOException if an I/O error occurs.
     */
    long getPointer() throws IOException;

    /**
     * Returns the length of this file.
     *
     * @return the length of this file, measured in bytes.
     * @throws IOException if an I/O error occurs.
     */
    long length() throws IOException;

    /**
     * Reads a byte of data from this file. The byte is returned as an
     * integer in the range 0 to 255 ({@code 0x00-0x0ff}). This
     * method blocks if no input is yet available.
     * <p>
     * Although {@code RandomAccessFile} is not a subclass of
     * {@code InputStream}, this method behaves in exactly the same
     * way as the {@link InputStream#read()} method of
     * {@code InputStream}.
     *
     * @return the next byte of data, or {@code -1} if the end of the
     * file has been reached.
     * @throws IOException if an I/O error occurs. Not thrown if
     *                     end-of-file has been reached.
     */
    int read() throws IOException;

    /**
     * Reads up to {@code len} bytes of data from this file into an
     * array of bytes. This method blocks until at least one byte of input
     * is available.
     * <p>
     * Although {@code RandomAccessFile} is not a subclass of
     * {@code InputStream}, this method behaves in exactly the
     * same way as the {@link InputStream#read(byte[], int, int)} method of
     * {@code InputStream}.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset in array {@code b}
     *            at which the data is written.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     * {@code -1} if there is no more data because the end of
     * the file has been reached.
     * @throws IOException               If the first byte cannot be read for any reason
     *                                   other than end of file, or if the random access file has been closed, or if
     *                                   some other I/O error occurs.
     * @throws NullPointerException      If {@code b} is {@code null}.
     * @throws IndexOutOfBoundsException If {@code off} is negative,
     *                                   {@code len} is negative, or {@code len} is greater than
     *                                   {@code b.length - off}
     */
    int read(byte b[], int off, int len) throws IOException;

    /**
     * Reads an unsigned 8-bit number from this file. This method reads
     * a byte from this file, starting at the current file pointer,
     * and returns that byte.
     * <p>
     * This method blocks until the byte is read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return the next byte of this file, interpreted as an unsigned
     * eight-bit number.
     * @throws EOFException if this file has reached the end.
     * @throws IOException  if an I/O error occurs.
     */
    int readUnsignedByte() throws IOException;

    /**
     * Reads a signed 16-bit number from this file. The method reads two
     * bytes from this file, starting at the current file pointer.
     * If the two bytes read, in order, are
     * {@code b1} and {@code b2}, where each of the two values is
     * between {@code 0} and {@code 255}, inclusive, then the
     * result is equal to:
     * <blockquote><pre>
     *     (short)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as a signed
     * 16-bit number.
     * @throws EOFException if this file reaches the end before reading
     *                      two bytes.
     * @throws IOException  if an I/O error occurs.
     */
    int readShort() throws IOException;

    /**
     * Reads an unsigned 16-bit number from this file. This method reads
     * two bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 8) | b2
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as an unsigned
     * 16-bit integer.
     * @throws EOFException if this file reaches the end before reading
     *                      two bytes.
     * @throws IOException  if an I/O error occurs.
     */
    int readUnsignedShort() throws IOException;

    /**
     * Reads an unsigned 24-bit integer from this file. This method reads 3
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are {@code b1},
     * {@code b2}, and {@code b3}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 16) | (b2 &lt;&lt; 8) + b3
     * </pre></blockquote>
     * <p>
     * This method blocks until the three bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next three bytes of this file, interpreted as an
     * {@code int}.
     * @throws EOFException if this file reaches the end before reading
     *                      three bytes.
     * @throws IOException  if an I/O error occurs.
     */
    int readUnsignedInt24() throws IOException;

    /**
     * Reads a signed 32-bit integer from this file. This method reads 4
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are {@code b1},
     * {@code b2}, {@code b3}, and {@code b4}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </pre></blockquote>
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as an
     * {@code int}.
     * @throws EOFException if this file reaches the end before reading
     *                      four bytes.
     * @throws IOException  if an I/O error occurs.
     */
    int readInt() throws IOException;

    /**
     * Reads an unsigned 32-bit integer from this file. This method reads 4
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are {@code b1},
     * {@code b2}, {@code b3}, and {@code b4}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </pre></blockquote>
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as an
     * {@code int}.
     * @throws EOFException if this file reaches the end before reading
     *                      four bytes.
     * @throws IOException  if an I/O error occurs.
     */
    int readUnsignedInt() throws IOException;

    /**
     * Reads a {@code float} from this file. This method reads an
     * {@code int} value, starting at the current file pointer,
     * as if by the {@code readInt} method
     * and then converts that {@code int} to a {@code float}
     * using the {@code intBitsToFloat} method in class
     * {@code Float}.
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as a
     * {@code float}.
     * @throws EOFException if this file reaches the end before reading
     *                      four bytes.
     * @throws IOException  if an I/O error occurs.
     * @see java.io.RandomAccessFile#readInt()
     * @see Float#intBitsToFloat(int)
     */
    float readFixed() throws IOException;

    /**
     * Reads a {@code float} from this file. This method reads
     * two bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2&nbsp;&lt;=&nbsp;255</code>,
     * then converts {@code b1} and {@code b2} to a {@code float}
     * using F2DOT14 format. The F2DOT14 format consists of a signed,
     * 2's complement integer and an unsigned fraction. To compute the
     * actual value, take the integer and add the fraction.
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as a
     * {@code float}.
     * @throws EOFException if this file reaches the end before reading
     *                      two bytes.
     * @throws IOException  if an I/O error occurs.
     */
    float readFixed2Dot14() throws IOException;

    /**
     * Reads a signed 64-bit integer from this file. This method reads eight
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1}, {@code b2}, {@code b3},
     * {@code b4}, {@code b5}, {@code b6},
     * {@code b7}, and {@code b8,} where:
     * <blockquote><pre>
     *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
     * </pre></blockquote>
     * <p>
     * then the result is equal to:
     * <blockquote><pre>
     *     ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48)
     *     + ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32)
     *     + ((long)b5 &lt;&lt; 24) + ((long)b6 &lt;&lt; 16)
     *     + ((long)b7 &lt;&lt; 8) + b8
     * </pre></blockquote>
     * <p>
     * This method blocks until the eight bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next eight bytes of this file, interpreted as a
     * {@code long}.
     * @throws EOFException if this file reaches the end before reading
     *                      eight bytes.
     * @throws IOException  if an I/O error occurs.
     */
    long readLong() throws IOException;

    /**
     * Reads a string from this file. This method reads {@code length}
     * bytes from the file, starting at the current file pointer.
     * <p>
     * This method blocks until the {@code length} bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @param length      The length of the string to read
     * @param charsetName The name of a supported {@linkplain java.nio.charset.Charset
     *                    charset}
     * @return the next {@code length} bytes of this file, interpreted as a
     * {@code java.lang.String}.
     * @throws EOFException if this file reaches the end before reading
     *                      eight bytes.
     * @throws IOException  if an I/O error occurs.
     */
    String readString(long length, String charsetName) throws IOException;
}

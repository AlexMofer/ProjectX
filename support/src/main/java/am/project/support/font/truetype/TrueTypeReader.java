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
package am.project.support.font.truetype;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads a TrueType font file.
 * Created by Alex on 2018/9/5.
 */
@SuppressWarnings("all")
public interface TrueTypeReader extends Closeable {

    String CHARSET_UTF_16BE = "UTF-16BE";
    String CHARSET_ISO_8859_1 = "ISO-8859-1";
    String TAG_TTCF = "ttcf";
    String TAG_OTTO = "OTTO";
    String TAG_TTF = "\u0000\u0001\u0000\u0000";

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
     * <p>
     * <p>
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
     * Reads an unsigned eight-bit number from this file. This method reads
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
    short readShort() throws IOException;

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
     * Reads the {@code len} bytes of text from this file.
     *
     * @param len         The length of the string to read
     * @param charsetName The name of a supported {@linkplain java.nio.charset.Charset
     *                    charset}
     * @return the next {@code len} bytes of text from this file.
     * @throws EOFException if this file reaches the end before reading
     *                      four bytes.
     * @throws IOException  if an I/O error occurs.
     */
    String readString(int len, String charsetName) throws IOException;

    /**
     * Reads the {@code len} bytes of text from this file.
     *
     * @param len The length of the string to read
     * @return the next {@code len} bytes of text from this file.
     * @throws EOFException if this file reaches the end before reading
     *                      four bytes.
     * @throws IOException  if an I/O error occurs.
     */
    String readString(int len) throws IOException;
}

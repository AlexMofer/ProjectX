/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.fop.fonts.truetype;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;


/**
 * Reads a TrueType font file into a RandomAccessFile.
 */
@SuppressWarnings("unused")
public class FontFileReader implements Closeable {

    public static final String CHARSET_UTF_16BE = "UTF-16BE";
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";
    private final RandomAccessFile mFile;

    @SuppressWarnings("all")
    public FontFileReader(File font) throws IOException {
        mFile = new RandomAccessFile(font, "r");
    }

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
    public void seek(long pos) throws IOException {
        mFile.seek(pos);
    }

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
    @SuppressWarnings("all")
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        final long pos = mFile.getFilePointer();
        final long len = mFile.length();
        long newpos = pos + n;
        if (newpos > len) {
            newpos = len;
        }
        mFile.seek(newpos);
        /* return the actual number of bytes skipped */
        return newpos - pos;
    }

    /**
     * Returns the current offset in this file.
     *
     * @return the offset from the beginning of the file, in bytes,
     * at which the next read or write occurs.
     * @throws IOException if an I/O error occurs.
     */
    public long getPointer() throws IOException {
        return mFile.getFilePointer();
    }

    /**
     * Returns the length of this file.
     *
     * @return the length of this file, measured in bytes.
     * @throws IOException if an I/O error occurs.
     */
    public long length() throws IOException {
        return mFile.length();
    }

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
    public int read() throws IOException {
        return mFile.read();
    }

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
    public final int readUnsignedByte() throws IOException {
        return mFile.readUnsignedByte();
    }

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
    public final short readShort() throws IOException {
        return mFile.readShort();
    }

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
    public final int readUnsignedShort() throws IOException {
        return mFile.readUnsignedShort();
    }

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
    public final int readInt() throws IOException {
        return mFile.readInt();
    }

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
    public final String readString(int len, String charsetName) throws IOException {
        if ((len + mFile.getFilePointer()) > mFile.length())
            throw new EOFException();
        final byte[] tmp = new byte[len];
        mFile.read(tmp);
        return new String(tmp, charsetName);
    }

    /**
     * Reads the {@code len} bytes of text from this file.
     *
     * @param len The length of the string to read
     * @return the next {@code len} bytes of text from this file.
     * @throws EOFException if this file reaches the end before reading
     *                      four bytes.
     * @throws IOException  if an I/O error occurs.
     */
    public final String readString(int len) throws IOException {
        if ((len + mFile.getFilePointer()) > mFile.length())
            throw new EOFException();
        final byte[] tmp = new byte[len];
        mFile.read(tmp);
        final String charsetName;
        if ((tmp.length > 0) && (tmp[0] == 0)) {
            charsetName = CHARSET_UTF_16BE;
        } else {
            charsetName = CHARSET_ISO_8859_1;
        }
        return new String(tmp, charsetName);
    }

    /**
     * Closes this random access file stream and releases any system
     * resources associated with the stream. A closed random access
     * file cannot perform input or output operations and cannot be
     * reopened.
     * <p>
     * <p> If this file has an associated channel then the channel is closed
     * as well.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        mFile.close();
    }
}
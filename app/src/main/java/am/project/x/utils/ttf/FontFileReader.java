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

/* $Id: FontFileReader.java 1357883 2012-07-05 20:29:53Z gadams $ */
package am.project.x.utils.ttf;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads a TrueType font file into a byte array and provides file like functions for array access.
 */
@SuppressWarnings("all")
public class FontFileReader {

    private int fsize; // file size
    private int current; // current position in file
    private byte[] file;

    /**
     * Constructor
     *
     * @param in InputStream to read from
     * @throws IOException In case of an I/O problem
     */
    FontFileReader(InputStream in) throws IOException {
        init(in);
    }

    /**
     * Constructor
     *
     * @param fileName filename to read
     * @throws IOException In case of an I/O problem
     */
    FontFileReader(String fileName) throws IOException {
        File f = new File(fileName);
        InputStream in = new FileInputStream(f);
        try {
            init(in);
        } finally {
            in.close();
        }
    }

    /**
     * Read a font file
     *
     * @param path absolute path to the font file.
     * @return
     * @throws IOException if an error occurred while reading the font.
     */
    public static TTFFile readTTF(String path) throws IOException {
        TTFFile ttfFile = new TTFFile();
        ttfFile.readFont(new FontFileReader(path));
        return ttfFile;
    }

    /**
     * Read a font file
     *
     * @param inputStream InputStream to read from
     * @return
     * @throws IOException if an error occurred while reading the font.
     */
    public static TTFFile readTTF(InputStream inputStream) throws IOException {
        TTFFile ttfFile = new TTFFile();
        ttfFile.readFont(new FontFileReader(inputStream));
        return ttfFile;
    }

    /**
     * Returns the full byte array representation of the file.
     *
     * @return byte array.
     */
    public byte[] getAllBytes() {
        return file;
    }

    /**
     * Returns current file position.
     *
     * @return int The current position.
     */
    public int getCurrentPos() {
        return current;
    }

    /**
     * Returns the size of the file.
     *
     * @return int The filesize
     */
    public int getFileSize() {
        return fsize;
    }

    /**
     * Initializes class and reads stream. Init does not close stream.
     *
     * @param in InputStream to read from new array with size + inc
     * @throws IOException In case of an I/O problem
     */
    private void init(InputStream in) throws java.io.IOException {
        file = IOUtils.toByteArray(in);
        fsize = file.length;
        current = 0;
    }

    /**
     * Read 1 byte.
     *
     * @return One byte
     * @throws IOException If EOF is reached
     */
    private byte read() throws IOException {
        if (current >= fsize) {
            throw new EOFException("Reached EOF, file size=" + fsize);
        }

        byte ret = file[current++];
        return ret;
    }

    /**
     * Read 1 signed byte.
     *
     * @return One byte
     * @throws IOException If EOF is reached
     */
    public byte readTTFByte() throws IOException {
        return read();
    }

    /**
     * Read 4 bytes.
     *
     * @return One signed integer
     * @throws IOException If EOF is reached
     */
    public int readTTFLong() throws IOException {
        long ret = readTTFUByte(); // << 8;
        ret = (ret << 8) + readTTFUByte();
        ret = (ret << 8) + readTTFUByte();
        ret = (ret << 8) + readTTFUByte();

        return (int) ret;
    }

    /**
     * Read an ISO-8859-1 string of len bytes.
     *
     * @param len The bytesToUpload of the string to read
     * @return A String
     * @throws IOException If EOF is reached
     */
    public String readTTFString(int len) throws IOException {
        if ((len + current) > fsize) {
            throw new EOFException("Reached EOF, file size=" + fsize);
        }

        byte[] tmp = new byte[len];
        System.arraycopy(file, current, tmp, 0, len);
        current += len;
        String encoding;
        if ((tmp.length > 0) && (tmp[0] == 0)) {
            encoding = "UTF-16BE";
        } else {
            encoding = "ISO-8859-1";
        }
        return new String(tmp, encoding);
    }

    /**
     * Read an ISO-8859-1 string of len bytes.
     *
     * @param len        The bytesToUpload of the string to read
     * @param encodingID the string encoding id (presently ignored; always uses UTF-16BE)
     * @return A String
     * @throws IOException If EOF is reached
     */
    public String readTTFString(int len, int encodingID) throws IOException {
        if ((len + current) > fsize) {
            throw new java.io.EOFException("Reached EOF, file size=" + fsize);
        }

        byte[] tmp = new byte[len];
        System.arraycopy(file, current, tmp, 0, len);
        current += len;
        String encoding;
        encoding = "UTF-16BE"; // Use this for all known encoding IDs for now
        return new String(tmp, encoding);
    }

    /**
     * Read 1 unsigned byte.
     *
     * @return One unsigned byte
     * @throws IOException If EOF is reached
     */
    public int readTTFUByte() throws IOException {
        byte buf = read();

        if (buf < 0) {
            return 256 + buf;
        } else {
            return buf;
        }
    }

    /**
     * Read 4 bytes.
     *
     * @return One unsigned integer
     * @throws IOException If EOF is reached
     */
    public long readTTFULong() throws IOException {
        long ret = readTTFUByte();
        ret = (ret << 8) + readTTFUByte();
        ret = (ret << 8) + readTTFUByte();
        ret = (ret << 8) + readTTFUByte();

        return ret;
    }

    /**
     * Read 2 bytes unsigned.
     *
     * @return One unsigned short
     * @throws IOException If EOF is reached
     */
    public int readTTFUShort() throws IOException {
        int ret = (readTTFUByte() << 8) + readTTFUByte();
        return ret;
    }

    /**
     * Set current file position to offset
     *
     * @param offset The new offset to set
     * @throws IOException In case of an I/O problem
     */
    public void seekSet(long offset) throws IOException {
        if (offset > fsize || offset < 0) {
            throw new EOFException("Reached EOF, file size=" + fsize + " offset=" + offset);
        }
        current = (int) offset;
    }

    /**
     * Skip a given number of bytes.
     *
     * @param add The number of bytes to advance
     * @throws IOException In case of an I/O problem
     */
    public void skip(long add) throws IOException {
        seekSet(current + add);
    }

}

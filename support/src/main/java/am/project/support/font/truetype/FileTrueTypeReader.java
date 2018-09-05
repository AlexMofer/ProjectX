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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Reads a TrueType font file into a RandomAccessFile.
 * Created by Alex on 2018/9/5.
 */
public class FileTrueTypeReader implements TrueTypeReader {

    private final RandomAccessFile mFile;

    @SuppressWarnings("all")
    public FileTrueTypeReader(File font) throws IOException {
        mFile = new RandomAccessFile(font, "r");
    }

    @Override
    public void seek(long pos) throws IOException {
        mFile.seek(pos);
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        final long pos = mFile.getFilePointer();
        final long len = mFile.length();
        long np = pos + n;
        if (np > len) {
            np = len;
        }
        mFile.seek(np);
        /* return the actual number of bytes skipped */
        return np - pos;
    }

    @Override
    public long getPointer() throws IOException {
        return mFile.getFilePointer();
    }

    @Override
    public long length() throws IOException {
        return mFile.length();
    }

    @Override
    public int read() throws IOException {
        return mFile.read();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return mFile.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return mFile.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return mFile.readUnsignedShort();
    }

    @Override
    public int readInt() throws IOException {
        return mFile.readInt();
    }

    @Override
    public String readString(int len, String charsetName) throws IOException {
        if ((len + mFile.getFilePointer()) > mFile.length())
            throw new EOFException();
        final byte[] tmp = new byte[len];
        mFile.read(tmp);
        return new String(tmp, charsetName);
    }

    @Override
    public String readString(int len) throws IOException {
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

    @Override
    public void close() throws IOException {
        mFile.close();
    }
}

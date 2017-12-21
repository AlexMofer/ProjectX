/*
 * Copyright (C) 2017 AlexMofer
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

package am.util.ftpserver.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * 随机访问文件流
 * Created by Alex on 2017/12/20.
 */
@SuppressWarnings("all")
public class RandomAccessFileOutputStream extends OutputStream {

    private final RandomAccessFile mRandomAccessFile;

    public RandomAccessFileOutputStream(String name, String mode) throws FileNotFoundException {
        mRandomAccessFile = new RandomAccessFile(name, mode);
    }

    public RandomAccessFileOutputStream(File file, String mode) throws FileNotFoundException {
        mRandomAccessFile = new RandomAccessFile(file, mode);
    }

    @Override
    public void write(int b) throws IOException {
        mRandomAccessFile.write(b);
    }

    @Override
    public void write(byte b[]) throws IOException {
        mRandomAccessFile.write(b);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        mRandomAccessFile.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        super.close();
        mRandomAccessFile.close();
    }

    public final FileDescriptor getFD() throws IOException {
        return mRandomAccessFile.getFD();
    }

    public FileChannel getChannel() {
        return mRandomAccessFile.getChannel();
    }

    public long getFilePointer() throws IOException {
        return mRandomAccessFile.getFilePointer();
    }

    public void seek(long offset) throws IOException {
        mRandomAccessFile.seek(offset);
    }

    public long length() throws IOException {
        return mRandomAccessFile.length();
    }

    public void setLength(long newLength) throws IOException {
        mRandomAccessFile.setLength(newLength);
    }

    public final void writeBoolean(boolean v) throws IOException {
        mRandomAccessFile.writeBoolean(v);
    }

    public final void writeByte(int v) throws IOException {
        mRandomAccessFile.writeByte(v);
    }

    public final void writeShort(int v) throws IOException {
        mRandomAccessFile.writeShort(v);
    }

    public final void writeChar(int v) throws IOException {
        mRandomAccessFile.writeChar(v);
    }

    public final void writeInt(int v) throws IOException {
        mRandomAccessFile.writeInt(v);
    }

    public final void writeLong(long v) throws IOException {
        mRandomAccessFile.writeLong(v);
    }

    public final void writeFloat(float v) throws IOException {
        mRandomAccessFile.writeFloat(v);
    }

    public final void writeDouble(double v) throws IOException {
        mRandomAccessFile.writeDouble(v);
    }

    public final void writeBytes(String s) throws IOException {
        mRandomAccessFile.writeBytes(s);
    }

    public final void writeChars(String s) throws IOException {
        mRandomAccessFile.writeChars(s);
    }

    public final void writeUTF(String str) throws IOException {
        mRandomAccessFile.writeUTF(str);
    }
}
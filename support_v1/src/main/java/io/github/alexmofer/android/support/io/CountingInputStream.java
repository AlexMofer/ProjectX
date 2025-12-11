/*
 * Copyright (C) 2025 AlexMofer
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
package io.github.alexmofer.android.support.io;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.LongConsumer;

/**
 * InputStream 读取计数.
 * Created by Alex on 2025/7/7.
 *
 * @since 1.12.0
 */
public final class CountingInputStream extends FilterInputStream {

    private final LongConsumer progress;
    private long count;
    private long mark = -1;

    public CountingInputStream(@NonNull InputStream in, @Nullable LongConsumer progress) {
        super(in);
        this.progress = progress;
    }

    /**
     * 获取总数
     * @return 总数
     */
    public long getCount() {
        return count;
    }

    @Override
    public int read() throws IOException {
        int result = in.read();
        if (result != -1) {
            count++;
        }
        notifyProgress();
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result = in.read(b, off, len);
        if (result != -1) {
            count += result;
        }
        notifyProgress();
        return result;
    }

    @Override
    public long skip(long n) throws IOException {
        long result = in.skip(n);
        count += result;
        notifyProgress();
        return result;
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
        mark = count;
    }

    @Override
    public synchronized void reset() throws IOException {
        if (!in.markSupported()) {
            throw new IOException("Mark not supported");
        }
        if (mark == -1) {
            throw new IOException("Mark not set");
        }
        in.reset();
        count = mark;
        notifyProgress();
    }

    private void notifyProgress() {
        if (progress != null) {
            progress.accept(count);
        }
    }
}

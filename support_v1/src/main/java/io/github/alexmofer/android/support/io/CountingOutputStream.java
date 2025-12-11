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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.LongConsumer;

/**
 * OutputStream 写入计数.
 * Created by Alex on 2025/7/7.
 *
 * @since 1.12.0
 */
public final class CountingOutputStream extends FilterOutputStream {

    private final LongConsumer progress;
    private long count;

    public CountingOutputStream(@NonNull OutputStream out, @Nullable LongConsumer progress) {
        super(out);
        this.progress = progress;
    }

    /**
     * 获取总数
     *
     * @return 总数
     */
    public long getCount() {
        return count;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        count += len;
        notifyProgress();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        count++;
        notifyProgress();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    private void notifyProgress() {
        if (progress != null) {
            progress.accept(count);
        }
    }
}

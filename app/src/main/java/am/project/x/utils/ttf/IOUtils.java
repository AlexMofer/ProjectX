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
package am.project.x.utils.ttf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("all")
class IOUtils {

    private static int DEFAULT_BUFFER = 4096; // 4kb

    private static int EOF = -1; // end of file

    private IOUtils() {

    }

    /**
     * Get the contents of an {@code InputStream} as a {@code byte[]}.
     * <p/>
     * This method buffers the input internally, so there is no need to use a {@code
     * BufferedInputStream}.
     *
     * @param input the {@code InputStream} to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * Copy bytes from an {@code InputStream} to an {@code OutputStream}.</p>
     * <p/>
     * This method buffers the input internally, so there is no need to use a {@code
     * BufferedInputStream}.</p>
     * <p/>
     * Large streams (over 2GB) will return a bytes copied value of {@code -1} after the copy has
     * completed since the correct number of bytes cannot be returned as an int. For large streams
     * use the {@code copyLarge(InputStream, OutputStream)} method.</p>
     *
     * @param input  the {@code InputStream} to read from
     * @param output the {@code OutputStream} to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) {@code InputStream} to an {@code OutputStream}.</p>
     * <p/>
     * This method buffers the input internally, so there is no need to use a {@code
     * BufferedInputStream}.</p>
     * <p/>
     * The buffer size is given by {@link #DEFAULT_BUFFER}.</p>
     *
     * @param input  the {@code InputStream} to read from
     * @param output the {@code OutputStream} to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     */
    public static long copyLarge(InputStream input, OutputStream output) throws
            IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER]);
    }

    /**
     * Copy bytes from a large (over 2GB) {@code InputStream} to an {@code OutputStream}.</p>
     * <p/>
     * This method uses the provided buffer, so there is no need to use a {@code
     * BufferedInputStream}.</p>
     *
     * @param input  the {@code InputStream} to read from
     * @param output the {@code OutputStream} to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     */
    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}

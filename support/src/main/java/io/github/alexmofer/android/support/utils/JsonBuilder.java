/*
 * Copyright (C) 2022 AlexMofer
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
package io.github.alexmofer.android.support.utils;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Json构建器
 * Created by Alex on 2022/11/12.
 */
public class JsonBuilder implements Closeable {

    private final StringWriter mWriter = new StringWriter();
    private final JsonWriter mJson = new JsonWriter(mWriter);

    /**
     * Sets the indentation string to be repeated for each level of indentation
     * in the encoded document. If {@code indent.isEmpty()} the encoded document
     * will be compact. Otherwise the encoded document will be more
     * human-readable.
     *
     * @param indent a string containing only whitespace.
     */
    public void setIndent(String indent) {
        mJson.setIndent(indent);
    }

    /**
     * Returns true if this builder has relaxed syntax rules.
     */
    public boolean isLenient() {
        return mJson.isLenient();
    }

    /**
     * Configure this builder to relax its syntax rules. By default, this builder
     * only emits well-formed JSON as specified by <a
     * href="http://www.ietf.org/rfc/rfc4627.txt">RFC 4627</a>. Setting the builder
     * to lenient permits the following:
     * <ul>
     *   <li>Top-level values of any type. With strict writing, the top-level
     *       value must be an object or an array.
     *   <li>Numbers may be {@link Double#isNaN() NaNs} or {@link
     *       Double#isInfinite() infinities}.
     * </ul>
     */
    public void setLenient(boolean lenient) {
        mJson.setLenient(lenient);
    }

    /**
     * Begins encoding a new array. Each call to this method must be paired with
     * a call to {@link #endArray}.
     *
     * @return this builder.
     */
    public JsonBuilder beginArray() {
        try {
            mJson.beginArray();
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Ends encoding the current array.
     *
     * @return this builder.
     */
    public JsonBuilder endArray() {
        try {
            mJson.endArray();
        } catch (IOException e) {
            //ignore
        }
        return this;
    }

    /**
     * Begins encoding a new object. Each call to this method must be paired
     * with a call to {@link #endObject}.
     *
     * @return this builder.
     */
    public JsonBuilder beginObject() {
        try {
            mJson.beginObject();
        } catch (IOException e) {
            //ignore
        }
        return this;
    }

    /**
     * Ends encoding the current object.
     *
     * @return this builder.
     */
    public JsonBuilder endObject() {
        try {
            mJson.endObject();
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Encodes the property name.
     *
     * @param name the name of the forthcoming value. May not be null.
     * @return this builder.
     */
    public JsonBuilder name(String name) {
        try {
            mJson.name(name);
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @param value the literal string value, or null to encode a null literal.
     * @return this builder.
     */
    public JsonBuilder value(String value) {
        try {
            mJson.value(value);
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Encodes {@code null}.
     *
     * @return this builder.
     */
    public JsonBuilder nullValue() {
        try {
            mJson.nullValue();
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @return this builder.
     */
    public JsonBuilder value(boolean value) {
        try {
            mJson.value(value);
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @param value a finite value. May not be {@link Double#isNaN() NaNs} or
     *              {@link Double#isInfinite() infinities} unless this builder is lenient.
     * @return this builder.
     */
    public JsonBuilder value(double value) {
        try {
            mJson.value(value);
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @return this builder.
     */
    public JsonBuilder value(long value) {
        try {
            mJson.value(value);
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @param value a finite value. May not be {@link Double#isNaN() NaNs} or
     *              {@link Double#isInfinite() infinities} unless this builder is lenient.
     * @return this builder.
     */
    public JsonBuilder value(Number value) {
        try {
            mJson.value(value);
        } catch (IOException e) {
            // ignore
        }
        return this;
    }

    /**
     * Ensures all buffered data is written to the underlying {@link JsonWriter}
     * and flushes that builder.
     */
    public void flush() {
        try {
            mJson.flush();
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * Flushes and closes this builder and the underlying {@link JsonWriter}.
     */
    public void close() {
        try {
            mJson.close();
        } catch (IOException e) {
            // ignore
        }
    }

    @NonNull
    @Override
    public String toString() {
        flush();
        return mWriter.toString();
    }

    /**
     * To String and close.
     *
     * @return Json
     */
    public String build() {
        final String result = toString();
        close();
        return result;
    }
}

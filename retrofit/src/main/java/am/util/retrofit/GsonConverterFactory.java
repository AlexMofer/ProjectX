/*
 * Copyright (C) 2020 AlexMofer
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
package am.util.retrofit;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Gson转换工厂
 * Created by Alex on 2020/6/2.
 */
public class GsonConverterFactory extends Converter.Factory {

    private final Gson mGson;

    protected GsonConverterFactory(Gson gson) {
        mGson = gson;
    }

    @SuppressWarnings("unused")
    public static GsonConverterFactory newInstance() {
        return newInstance(new Gson());
    }

    public static GsonConverterFactory newInstance(Gson gson) {
        return new GsonConverterFactory(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new ResponseBodyConverter<>(mGson, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        return new RequestBodyConverter<>(mGson, type);
    }

    @SuppressWarnings("NullableProblems")
    private static class RequestBodyConverter<T> implements Converter<T, RequestBody> {
        private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");
        @SuppressWarnings("CharsetObjectCanBeUsed")
        private static final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson mGson;
        private final Type mType;

        RequestBodyConverter(Gson gson, Type type) {
            mGson = gson;
            mType = type;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            final Buffer buffer = new Buffer();
            final JsonWriter writer =
                    mGson.newJsonWriter(new OutputStreamWriter(buffer.outputStream(), UTF_8));
            mGson.toJson(value, mType, writer);
            writer.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    private static class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson mGson;
        private final Type mType;

        ResponseBodyConverter(Gson gson, Type type) {
            mGson = gson;
            mType = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            try {
                final JsonReader reader = mGson.newJsonReader(value.charStream());
                T result = mGson.fromJson(reader, mType);
                if (reader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonIOException("JSON document was not fully consumed.");
                }
                return result;
            } finally {
                value.close();
            }
        }
    }
}
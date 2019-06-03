package am.project.x.business.others.retrofithelper.gson;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gson辅助器
 * Created by Alex on 2017/8/17.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class GsonHelper {

    private static Gson mGson;

    private static Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .setDateFormat(DateFormat.LONG)
                    .create();
        }
        return mGson;
    }

    /**
     * 实体对象序列化
     *
     * @param src 需要序列化的实体对象
     * @return 格式化的字符串
     */
    public static String toJson(Object src) {
        if (src instanceof List<?>)
            return toJson(src, new TypeToken<List<?>>() {
            }.getType());
        if (src instanceof Set<?>)
            return toJson(src, new TypeToken<Set<?>>() {
            }.getType());
        if (src instanceof Map<?, ?>)
            return toJson(src, new TypeToken<Map<?, ?>>() {
            }.getType());

        return getGson().toJson(src);
    }

    /**
     * 范型对象序列化
     *
     * @param src       需要序列化的实体对象
     * @param typeOfSrc 范型类型
     * @return 格式化的字符串
     */
    public static String toJson(Object src, Type typeOfSrc) {
        return getGson().toJson(src, typeOfSrc);
    }

    /**
     * 反序列实体对象
     *
     * @param json     字符串
     * @param classOfT 实体类型
     * @param <T>      目标类型
     * @return 实体对象
     * @throws JsonSyntaxException 错误
     */
    public static <T> T fromJsonOrThrow(String json, Class<T> classOfT)
            throws JsonSyntaxException {
        return getGson().fromJson(json, classOfT);
    }

    /**
     * 反序列范型对象
     *
     * @param json    字符串
     * @param typeOfT 范型类型
     * @param <T>     目标类型
     * @return 实体对象
     * @throws JsonSyntaxException 错误
     */
    public static <T> T fromJsonOrThrow(String json, Type typeOfT) throws JsonSyntaxException {
        return getGson().fromJson(json, typeOfT);
    }

    /**
     * 反序列范型对象
     *
     * @param json         字符串
     * @param typeTokenOfT 范型类型Token
     * @param <T>          目标类型
     * @return 实体对象
     * @throws JsonSyntaxException 错误
     */
    public static <T> T fromJsonOrThrow(String json, TypeToken<T> typeTokenOfT)
            throws JsonSyntaxException {
        return fromJsonOrThrow(json, typeTokenOfT.getType());
    }

    /**
     * 反序列实体对象
     *
     * @param json     字符串
     * @param classOfT 实体类型
     * @param <T>      目标类型
     * @return 实体对象
     */
    @Nullable
    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json))
            return null;
        try {
            return fromJsonOrThrow(json, classOfT);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * 反序列范型对象
     *
     * @param json    字符串
     * @param typeOfT 范型类型
     * @param <T>     目标类型
     * @return 实体对象
     */
    @Nullable
    public static <T> T fromJson(String json, Type typeOfT) {
        if (TextUtils.isEmpty(json))
            return null;
        try {
            return fromJsonOrThrow(json, typeOfT);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * 反序列范型对象
     *
     * @param json         字符串
     * @param typeTokenOfT 范型类型Token
     * @param <T>          目标类型
     * @return 实体对象
     */
    @Nullable
    public static <T> T fromJson(String json, TypeToken<T> typeTokenOfT) {
        if (TextUtils.isEmpty(json))
            return null;
        try {
            return fromJsonOrThrow(json, typeTokenOfT);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * 日期序列化
     */
    private static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    }
}


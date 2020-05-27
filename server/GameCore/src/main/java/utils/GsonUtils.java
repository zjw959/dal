package utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonUtils {
    private static Gson gson = new Gson();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return (T) gson.fromJson(json, clazz);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static JsonElement toJsonTree(Object obj) {
        return gson.toJsonTree(obj);
    }

    public static JsonObject toJsonObject(String str) {
        return new JsonParser().parse(str).getAsJsonObject();
    }

    public static <T> T fromJson(String json, Type type) {
        return (T) gson.fromJson(json, type);
    }
}

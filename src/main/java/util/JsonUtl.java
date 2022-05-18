package util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by homer on 16-9-18.
 */
public class JsonUtl {
    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Type target) {
        Gson gson = new Gson();
        return gson.fromJson(json, target);
    }
}

package org.apache.nifi.bsc.utils;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

/**
 * Created by SonCD on 11/05/2020
 */
public class JSONValidUtil {

    private static Gson gson = new Gson();
    public static boolean isJSONObjectValid(String json) {
        try {
            gson.fromJson(json, JsonObject.class);
        }catch (JsonIOException ex) {
            return false;
        }
        return true;
    }

    public static boolean isJSONArrayValid(String json) {
        try {
            gson.fromJson(json, JsonArray.class);
        }catch (JsonIOException ex) {
            return false;
        }
        return true;
    }

}

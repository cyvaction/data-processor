package com.yulore.bigdata.data_processor.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class GsonUtil {
	
	private static Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static Gson disableHtmlEscapingGson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
	
	public static String toJson(Object src){
		
		return gson.toJson(src);
	}
	
	/**
	 * 不将html标签转化为unicode，ps：默认会转换，比如&转换为\u0026
	 * @param src
	 * @return
	 */
	public static String toDisableHtmlEscapingJson(Object src){
		
		return disableHtmlEscapingGson.toJson(src);
	}
	
	public static <T> T fromJson(String json,Class<T> clazz) throws JsonSyntaxException{
		
		return gson.fromJson(json, clazz);  
	}
	
	public static String getNullAsEmptyString(JsonElement jsonElement) {
		
        return getDefaultAsEmptyString(jsonElement, null);
    }
	
	public static String getDefaultAsEmptyString(JsonElement jsonElement,String defaultVal) {
		
        return jsonElement.isJsonNull() ? defaultVal : jsonElement.getAsString();
    }
	
	public static JsonObject getNullAsEmptyJsonObject(JsonElement jsonElement) {
		
        return jsonElement.isJsonNull() ? null : jsonElement.getAsJsonObject();
    }
	
	public static JsonArray getNullAsEmptyJsonArray(JsonElement jsonElement) {
		
        return jsonElement.isJsonNull() ? null : jsonElement.getAsJsonArray();
    }

	public static JsonObject getAsJsonObject(Object obj) {
		return obj==null ? null : gson.toJsonTree(obj).getAsJsonObject();
	}
	
	public static JsonElement toJsonTree(Object obj) {
		return obj==null ? null : gson.toJsonTree(obj);
	}
}

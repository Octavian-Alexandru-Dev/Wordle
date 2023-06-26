package Server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Parser {

    //// converte un array di byte in un JsonObject
    // public static Request deserializeRequest(byte[] data) {
    // String stringRequest = new String(data);
    // Gson gson = new Gson();
    // Type requestType = new TypeToken<Request>() {
    // }.getType();
    //
    // Request rm = gson.fromJson(stringRequest, requestType);
    // return rm;
    // }

    // converte un oggetto in un JsonElement
    public JsonElement toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJsonTree(object);
    }

    public static byte[] serialize(Object object) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        return jsonString.getBytes();
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Gson gson = new Gson();
        String jsonString = new String(bytes);
        return gson.fromJson(jsonString, clazz);
    }
}

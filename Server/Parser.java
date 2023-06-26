package Server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Parser {
    private static Gson gson;

    // singleton of gson with double checked locking
    private static Gson getGsonInstace() {
        if (gson == null) {
            synchronized (Parser.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    // converte un array di byte in un JsonElement
    public static JsonElement parse(byte[] data) {
        String string = new String(data);

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(string);

        return jsonElement;
    }

    // converte un oggetto in un JsonElement
    public JsonElement toJson(Object object) {
        return gson.toJsonTree(object);
    }

    public static byte[] serialize(Object object) {
        gson = getGsonInstace();
        String jsonString = gson.toJson(object);
        return jsonString.getBytes();
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        gson = getGsonInstace();
        String jsonString = new String(bytes);
        return gson.fromJson(jsonString, clazz);
    }
}

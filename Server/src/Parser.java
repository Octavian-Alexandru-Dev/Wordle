import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class Parser {
    private static Gson gson = getGson();

    public static Gson getGson() {
        if (gson == null) {
            synchronized (Parser.class) {
                if (gson == null) {
                    gson = new GsonBuilder().setPrettyPrinting().create();
                }
            }
        }
        return gson;
    }

    public static byte[] serialize(Object object) {
        String jsonString = gson.toJson(object);
        return jsonString.getBytes();
    }

    public static JsonElement toJsonElement(Object object) {
        return gson.toJsonTree(object);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        // Gson gson = new Gson();
        String jsonString = new String(bytes);
        return gson.fromJson(jsonString, clazz);
    }

}

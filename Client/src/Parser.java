
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        String jsonString = new String(bytes);
        return gson.fromJson(jsonString, clazz);
    }

    public static JsonElement toJsonElement(Object object) {
        return gson.toJsonTree(object);
    }

    public static LocalTime time(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        try {
            return LocalTime.parse(timeString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato ora non valido: " + timeString);
        }
    }

}

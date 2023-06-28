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
        // Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(object);
        return jsonString.getBytes();
    }

    public static JsonElement toJsonElement(Object object) {
        // Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJsonTree(object);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        // Gson gson = new Gson();
        String jsonString = new String(bytes);
        return gson.fromJson(jsonString, clazz);
    }

    // private static String dateTimeToString(LocalDateTime date) {
    // DateTimeFormatter formatter =
    // DateTimeFormatter.ofPattern(Config.DATE_FORMAT);
    // String formattedDate = date.format(formatter);
    // return formattedDate;
    // }
    //
    // private static LocalDateTime stringToDateTime(String dateString) {
    // DateTimeFormatter formatter =
    // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    // LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
    // return dateTime;
    // }
}

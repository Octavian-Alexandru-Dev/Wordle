
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Parser {

    public static byte[] serialize(Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(object);
        return jsonString.getBytes();
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Gson gson = new Gson();
        String jsonString = new String(bytes);
        return gson.fromJson(jsonString, clazz);
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

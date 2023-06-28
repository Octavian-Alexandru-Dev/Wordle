
import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.io.File;

public class Config {
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    // Attributi di configurazione
    public static int PLAYER_MAX_ATTEMPTS;
    public static int WORDS_LENGTH;
    public static int WORDS_DURATION;
    public static int DEFAULT_PORT;
    public static int MULTICAST_PORT;
    public static int THREAD_POOL_SIZE;
    public static String MULTICAST_GROUP;
    public static String DEFAULT_HOST;

    public static String USER_FILE_PATH = getAbsoluteDirectory() + "resources/users/";
    public static String WORDS_FILE_PATH = getAbsoluteDirectory() + "resources/words.txt";
    public static String SECRET_WORD_FILE_PATH = getAbsoluteDirectory() + "resources/lastSecretWord.json";

    // Percorso del file di configurazione
    private static final String CONFIG_FILE_PATH_RELATIVE = "/resources/server.properties";

    private static boolean loaded = loadConfig();

    // Metodo per caricare la configurazione
    public static boolean loadConfig() {
        try (FileInputStream fileInputStream = new FileInputStream(
                getAbsoluteDirectory() + CONFIG_FILE_PATH_RELATIVE)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            // Leggere i valori dal file di configurazione e assegnarli agli attributi
            PLAYER_MAX_ATTEMPTS = Integer.parseInt(properties.getProperty("PLAYER_MAX_ATTEMPTS"));
            WORDS_LENGTH = Integer.parseInt(properties.getProperty("WORDS_LENGTH"));
            DEFAULT_PORT = Integer.parseInt(properties.getProperty("DEFAULT_PORT"));
            WORDS_DURATION = Integer.parseInt(properties.getProperty("WORDS_DURATION"));
            MULTICAST_PORT = Integer.parseInt(properties.getProperty("MULTICAST_PORT"));
            MULTICAST_GROUP = properties.getProperty("MULTICAST_GROUP");
            DEFAULT_HOST = properties.getProperty("DEFAULT_HOST");
            THREAD_POOL_SIZE = Integer.parseInt(properties.getProperty("THREAD_POOL_SIZE"));

            System.out.println("[Config] Configurazione caricata");
            return true;
        } catch (IOException e) {
            System.out.println("[Config] Errore: caricamento della configurazione");
            e.printStackTrace();
            return false;
        }
    }

    private static String getAbsoluteDirectory() {
        String classPath = Config.class.getResource("Config.class").toString();

        if (classPath.startsWith("file:")) {
            String filePath = classPath.substring("file:".length());
            File file = new File(filePath);
            return file.getParent() + "/../";
        }

        return "";
    }

    // Metodo per controllare se la configurazione e' stata caricata
    public static boolean isLoaded() {
        return loaded;
    }
}

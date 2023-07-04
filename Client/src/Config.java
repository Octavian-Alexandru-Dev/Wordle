
import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.io.File;

public class Config {
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // parametri relativi al gioco
    public static int PLAYER_MAX_ATTEMPTS;
    public static int WORDS_LENGTH;

    // multicast
    public static int MULTICAST_PORT;
    public static String MULTICAST_GROUP;
    public static String MULTICAST_INTERFACE;

    // server config
    public static int SERVER_PORT;
    public static String SERVER_HOST;

    // Percorso del file di configurazione
    private static final String CONFIG_FILE_PATH_RELATIVE = "/resources/client.properties";

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

            MULTICAST_PORT = Integer.parseInt(properties.getProperty("MULTICAST_PORT"));
            MULTICAST_GROUP = properties.getProperty("MULTICAST_GROUP");
            MULTICAST_INTERFACE = properties.getProperty("MULTICAST_INTERFACE");

            SERVER_PORT = Integer.parseInt(properties.getProperty("SERVER_PORT"));
            SERVER_HOST = properties.getProperty("SERVER_HOST");

            System.out.println("MULTICAST_PORT: " + MULTICAST_PORT);
            System.out.println("MULTICAST_GROUP: " + MULTICAST_GROUP);
            System.out.println("MULTICAST_INTERFACE: " + MULTICAST_INTERFACE);
            System.out.println("SERVER_PORT: " + SERVER_PORT);
            System.out.println("SERVER_HOST: " + SERVER_HOST);

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

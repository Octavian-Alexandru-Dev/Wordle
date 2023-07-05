import com.google.gson.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    public final static ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<String, Client>();
    private final static String RESOURCES_PATH = Config.USER_FILE_PATH;

    // stringa codificato in base64 che contiene username e password
    private String credentials;
    private String username;
    private String password;
    private Player player;
    private String path;

    // l'id è rappresentato da l'indirizzo ip:porta del client
    private String connID;
    // indica se il client è registrato
    private boolean isRegistered;
    // indica se il client è loggato
    private boolean isLogged;

    // inizio della sessione di login
    private long loginTime;

    public Client(String credentials, String connID) {
        this.credentials = credentials;
        this.connID = connID;
        this.isRegistered = false;
        this.isLogged = false;
        this.player = null;
        this.password = getPassword();
        this.username = getUsername();
        this.path = getPath();
    }

    // questo mettodo che viene chiamato dal server quando un client si disconnette
    public static void logout(String connID) {
        Client client = clients.get(connID);
        if (client != null) {
            client.logout();
        }
    }

    // salvo le statistiche di tutti i giocatori
    public static void logoutAll() {
        System.out.println("Salvataggio delle statistiche di tutti i giocatori ...");
        for (Client client : clients.values()) {
            client.logout();
        }
    }

    // quasto metodo viene chiamto dal thread che gestisce la Secret word quando
    // viene generata una nuova parola
    public static void newWordEvent() {
        System.out.println("Salvataggio delle statistiche di tutti i giocatori ...");
        for (Client client : clients.values()) {
            if (client.player != null) {
                client.player.newWordEvent();
            }
        }
    }

    // ====================== METODI PRIVATI ======================
    public JsonObject playWordle() {
        if (this.player == null) {
            this.player = new Player(this);
            return this.player.play();
        } else {
            return new JsonObject();
        }
    }

    public boolean share() {
        if (this.player != null) {
            // genero il json da con le statistiche del giocatore
            JsonElement statistics = this.player.getStatistics();
            // creo il json da inviare
            JsonObject response = new JsonObject();
            // aggiungo le statistiche e l'username
            response.addProperty("username", this.username);
            response.add("statistics", statistics);
            // invio il json in multicast
            if (Multicast.share(Parser.serialize(response))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public JsonElement sendMeStatistics() {
        if (this.player != null) {
            return this.player.getStatistics();
        } else {
            return null;
        }
    }

    public JsonObject sendWord(String guessedWord) {
        String pattern = this.player.sendWord(guessedWord);
        JsonObject response = new JsonObject();
        response.addProperty("pattern", pattern);
        // se il pattern è WORD_CHANGED allora aggiungo alla risposta l'orario
        if (pattern.equals("WORD_CHANGED")) {
            response.addProperty("endAt", SecretWord.getEndTime());
        }
        return response;
    }

    public boolean isPlaying() {
        return this.player != null;
    }

    public boolean logout() {
        if (this.player != null) {
            if (!this.player.logout()) {
                return false;
            }
        }
        // rimuovo l'utente dalla hashmap
        clients.remove(connID);
        this.isLogged = false;

        System.out.println("Logout dell'utente :" + this.username);
        return true;
    }

    public String login() {
        // leggo le credenziali dal file <username>.json
        try {
            JsonObject json = readUserResource();
            if (json != null) {
                // controllo se le credenziali sono presenti nel file json
                String base64Credentials = json.get("credentials").getAsString();
                if (base64Credentials == null) {
                    return "ERROR";
                }
                // decodifico le credenziali
                String[] decodedCredentials = decodeBase64Credentials(base64Credentials);
                String username = decodedCredentials[0];
                String password = decodedCredentials[1];

                // controllo ricevute se sono uguali a quelle del file json
                if (username.equals(this.username) && password.equals(this.password)) {
                    this.isLogged = true;
                    this.loginTime = System.currentTimeMillis();
                    Player.loggedPlayer.put(this.username,true);
                    System.out.println("Login dell'utente: " + this.username);
                    return "OK";
                } else {
                    return "WRONG_CREDENTIALS";
                }
            } else {
                return "NOT_FOUND";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public JsonObject readUserResource() {
        File input = new File(this.path);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject OBJ = fileElement.getAsJsonObject();
            return OBJ;
        } catch (Exception e) {
            System.out.println("Errore di lettura del file:" + this.path);
            e.printStackTrace();
            return null;
        }
    }

    // registra l'utente creando il file <username>.json
    public boolean register() {
        // creo il file con nome <username>.json
        // popolo il file con i dati dell'utente
        try (Writer fw = new FileWriter(getPath())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<String, Object> map = new HashMap<>();
            map.put("credentials", this.credentials);
            gson.toJson(map, fw);
            this.isRegistered = true;
            System.out.println("Registrazione dell'utente '" + this.username + "' avvenuta con successo");
            return true;
        } catch (IOException e) {
            System.out.println("Errore di scrittura sul file durante la registrazione");
            e.printStackTrace();
            return false;
        }
    }

    // controlla se l'utente è registrato
    public boolean isRegistered() {
        if (this.isRegistered) { // se la variabile isRegistered è true, l'utente è registrato
            return true;
        } else {// se la variabile isRegistered è false, controllo se il file <username>.json
            File file = new File(this.path);
            return file.exists();
        }
    }

    public boolean isLogged() {
        return this.isLogged;
    }

    // ritorna la password dell'utente
    private String getPassword() {
        return decodeBase64Credentials(this.credentials)[1];
    }

    // ritorna l'username dell'utente
    public String getUsername() {
        return decodeBase64Credentials(this.credentials)[0];
    }

    // decodifica la stringa in base64
    private static String[] decodeBase64Credentials(String str) {
        try {
            return new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8).split(":");
        } catch (IllegalArgumentException e) {
            System.out.println("[decodeBase64] Errore durante la decodifica della stringa");
            e.printStackTrace();
            return new String[] { "" };
        }
    }

    private String getPath() {
        return RESOURCES_PATH + this.username + ".json";
    }
}

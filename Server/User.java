package Server;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.JsonObject;

public class User {
    // stringa codificato in base64 che contiene username e password
    private String credentials;

    // l'id è rappresentato da l'indirizzo ip:porta del client
    private String id;
    private boolean isRegistered;
    private boolean isLogged;
    private Player player;

    // inizio della sessione di login
    private long loginTime;

    private final static ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();

    public User(String credentials, String id) {
        this.credentials = credentials;
        this.id = id;
        this.isRegistered = false;
        this.isLogged = false;
        this.player = null;
    }

    public static byte[] handleRequest(byte[] request, String id) {
        Request req = new Request(Parser.parse(request));
        if (!req.isValid()) {
            System.out.println("[handleRequest] Invalid request");
            System.out.println("[handleRequest] request =>" + request.toString());
            return null;
        }

        // controllo se l'utente è registrato
        if (users.containsKey(id)) {
            // se l'utente è registrato, restituisco il suo stato
            User user = users.get(id);

        } else {
            // se non è presente nella hashmap, mi aspetto che il tipo di richiesta che ho
            // ricevuto sia di registrazione, login
            if (req.isRegisterRequest()) {
                System.out.println("[handleRequest] User not registered");
                return null;
            } else if (req.isLoginRequest()) {
                System.out.println("[handleRequest] User not registered");
                return null;
            } else {
                System.out.println("[handleRequest] User not registered");
                return null;
            }

            // se l'utente non è registrato, lo registro e restituisco il suo stato
            // users.put(id, new User("", id));
        }

        return null;
    }

    public String getPassword() {
        String decodedString = new String(Base64.getDecoder().decode(this.credentials));
        String[] parts = decodedString.split(":");
        return parts[1];
    }

    public String getUsername() {
        String decodedString = new String(Base64.getDecoder().decode(this.credentials));
        String[] parts = decodedString.split(":");
        return parts[0];
    }
}

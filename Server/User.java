package src.Server;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;

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
        System.out.println("[processRequest] Richiesta ricevuta => " + new String(request));
        Request req = Parser.deserialize(request, Request.class);
        if (!req.isValid()) {
            System.out.println("[handleRequest] Invalid request");
            System.out.println("[handleRequest] request =>" + request.toString());
            return null;
        }

        // controllo se l'utente è registrato
        if (users.containsKey(id)) {
            // se l'utente è registrato, restituisco il suo stato
            // User user = users.get(id);

        } else {
            System.out.println("[handleRequest] L'utente non è presente nella hashmap");
            // se non è presente nella hashmap, mi aspetto che il tipo di richiesta che ho
            // ricevuto sia di registrazione, login
            User user = new User(req.getCredentials(), id);

            if (req.isRegisterRequest()) {
                return user.registerRequesthandler(req);
            } else if (req.isLoginRequest()) {
                return user.loginRequestHandler(req);
            } else {
                JsonElement response = Response.creat(Response.Status.UNAUTHORIZED, "");
                return Parser.serialize(response);
            }
        }

        return null;
    }

    private byte[] registerRequesthandler(Request req) {
        return "ora sei registrato".getBytes();
    }

    private byte[] loginRequestHandler(Request req) {
        return "ora sei loggato".getBytes();
    }

    private String getPassword() {
        String decodedString = new String(Base64.getDecoder().decode(this.credentials));
        String[] parts = decodedString.split(":");
        return parts[1];
    }

    private String getUsername() {
        String decodedString = new String(Base64.getDecoder().decode(this.credentials));
        String[] parts = decodedString.split(":");
        return parts[0];
    }
}

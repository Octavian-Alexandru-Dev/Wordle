import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthRequest {
    private String method;
    private String credentials;

    // costruttore che viene utilizzato per le richieste di login e register
    public AuthRequest(String method, String username, String password) {
        this.method = method;
        this.credentials = encodeToBase64(username + ":" + password);
    }

    // costruttore che viene utilizzato per le richieste di logout
    public AuthRequest(String method, String username) {
        this.method = method;
        this.credentials = encodeToBase64(username + ":");
    }

    // data una stringa in input, la codifica in base64
    static String encodeToBase64(String credentials) {
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    public String getCredentials() {
        return credentials;
    }

    public String getMethod() {
        return method;
    }
}

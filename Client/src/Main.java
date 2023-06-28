
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {

        Client client = new Client("localhost", 8000);

        try {
            Game game = new Game(client);
            game.play();

            // byte[] responseData = client
            // .sendRequest(Parser.serialize(new AuthRequest("register",
            // encodeCredentials("admin", "admin"))));
            // String response = new String(responseData);
            // System.out.println("Risposta => " + response);
            //
            // responseData = client
            // .sendRequest(Parser.serialize(new AuthRequest("login",
            // encodeCredentials("admin", "admin"))));
            // response = new String(responseData);
            // System.out.println("Risposta => " + response);
            //
            // responseData = client
            // .sendRequest(Parser.serialize(new AuthRequest("register",
            // encodeCredentials("pippo", "admin"))));
            // response = new String(responseData);
            // System.out.println("Risposta => " + response);
            //
            // responseData = client
            // .sendRequest(Parser.serialize(new AuthRequest("register",
            // encodeCredentials("octavian", "admin"))));
            // response = new String(responseData);
            // System.out.println("Risposta => " + response);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close(); // Chiusura della connessione
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
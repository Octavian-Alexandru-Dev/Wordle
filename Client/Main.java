package Client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Client client = new Client("localhost", 8000);

        try {
            // Connessione al server
            client.connect();

            byte[][] requests = new byte[3][];
            requests[0] = "{\"method\": \"sendWord\", \"guessedWord\": \"parola da 10 caratteri\"}".getBytes();
            requests[1] = "{\"method\": \"login\", \"credentials\": \"admin:admin codificato in Base64\"}".getBytes();
            requests[2] = "Ping\n".getBytes();

            for (int i = 0; i < requests.length; i++) {
                // Invia la richiesta al server
                byte[] responseData = client.sendRequest(requests[i]);

                if (responseData != null) {
                    String response = new String(responseData);
                    System.out.println("Risposta => " + response);
                }
            }
        } catch (IOException e) {
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
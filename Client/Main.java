package Client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Client client = new Client("localhost", 8000);

        try {
            // Connessione al server
            client.connect();

            // Invia la richiesta al server
            byte[] requestData = "Ping\n".getBytes();

            byte[] responseData = client.sendRequest(requestData);

            if (responseData != null) {
                String response = new String(responseData);
                System.out.println("Risposta => " + response);
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
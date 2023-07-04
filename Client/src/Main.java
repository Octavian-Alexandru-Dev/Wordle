
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Leggo il file di configurazione
        if (!Config.isLoaded()) {
            System.out.println("[Main] Errore: lettura dei parametri di configurazione");
            return;
        }
        // Creazione del client
        Client client = new Client(Config.SERVER_HOST, Config.SERVER_PORT);

        try {
            // Creazione del gioco
            Game game = new Game(client);
            game.play();

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
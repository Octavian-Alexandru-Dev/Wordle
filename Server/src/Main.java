import java.io.*;

public class Main {
    public static void main(String[] args) {
        // Leggo il file di configurazione
        if (!Config.isLoaded()) {
            System.out.println("[Main] Errore: lettura dei parametri di configurazione");
            return;
        }

        try {
            Server server = new Server(Config.DEFAULT_PORT, Config.THREAD_POOL_SIZE);
            SecretWord secretWord = new SecretWord();

            Runtime.getRuntime().addShutdownHook(new TerminationHandler(server, secretWord));

            System.out.println("[Main] Avvio il thread per la parola segreta...");
            secretWord.start();
            // Avvio il server
            System.out.println("[Main] Avvio il server...");
            server.start();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[main] Errore: avvio del server");
            return;
        }
    }
}
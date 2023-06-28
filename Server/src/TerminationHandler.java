
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

public class TerminationHandler extends Thread {
    private final Server server;
    private final SecretWord secretWord;

    public TerminationHandler(Server server, SecretWord secretWord) {
        this.server = server;
        this.secretWord = secretWord;

    }

    public void run() {
        System.out.println("\nTerminazione in corso...");
        try {
            // Chiusura del server
            if (server != null && server.isRunning()) {
                this.server.stop();
                System.out.println("Server terminato");
            }
        } catch (IOException e) {
            System.err.printf("Errore durante la terminazione: %s\n", e.getMessage());
        }
        if (secretWord != null && secretWord.isRunning()) {
            secretWord.stop();
        }

        Client.logoutAll();

        System.out.println("Programma terminato con successo");
    }
}
package Server;

import java.io.IOException;

public class TerminationHandler extends Thread {
    private final Server server;

    public TerminationHandler(Server server) {
        this.server = server;
    }

    public void run() {
        System.out.println("Terminazione in corso...");

        try {
            // Chiusura del server
            if (server != null && server.isRunning()) {
                this.server.stop();
                System.out.println("Server terminato");
            }

        } catch (IOException e) {
            System.err.printf("Errore durante la terminazione: %s\n", e.getMessage());
        }

        System.out.println("Programma terminato con successo");
    }
}
package Server;

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

            Runtime.getRuntime().addShutdownHook(new TerminationHandler(server));

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
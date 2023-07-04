import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.JsonObject;

public class Multicast extends Thread {
    private MulticastSocket ms;
    private InetSocketAddress ia;
    private NetworkInterface netIf;
    private int MULTICAST_PORT = Config.MULTICAST_PORT;
    private String MULTICAST_GROUP = Config.MULTICAST_GROUP;
    private String MULTICAST_INTERFACE = Config.MULTICAST_GROUP;

    private static ConcurrentLinkedQueue<JsonObject> notifications;
    private static Multicast instance = getInstance();
    // variabile atomica che indica se il multicasting è attivo
    public static AtomicBoolean active;

    private Multicast() {
        try {
            active = new AtomicBoolean(false);
            notifications = new ConcurrentLinkedQueue<JsonObject>();
            MULTICAST_PORT = Config.MULTICAST_PORT;
            MULTICAST_GROUP = Config.MULTICAST_GROUP;
            MULTICAST_INTERFACE = Config.MULTICAST_GROUP;
            ms = new MulticastSocket(MULTICAST_PORT);
            ia = new InetSocketAddress(MULTICAST_GROUP, MULTICAST_PORT);
            netIf = NetworkInterface.getByName(MULTICAST_INTERFACE);
        } catch (Exception e) {
            System.out.println("Errore nella creazione del socket multicast");
            e.printStackTrace();
        }
    }

    public static Multicast getInstance() {
        if (instance == null) {
            instance = new Multicast();
        }
        return instance;
    }

    @Override
    public void run() {
        System.out.println("Starting multicast thread ...");
        if (this.joinGroup()) {
            this.awaitNotifications();
        }
    }

    private boolean joinGroup() {
        System.out.println("Joining multicast group");
        try {
            ms.joinGroup(ia, netIf);
            return true;
        } catch (Exception e) {
            System.out.println(Color.red("Errore nella join del gruppo multicast"));
            e.printStackTrace();
            return false;
        }
    }

    private void awaitNotifications() {
        System.out.println("Awaiting notifications...");
        active = new AtomicBoolean(true);
        while (!isInterrupted()) {
            // ricevo il pacchetto
            byte[] buffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            try {
                ms.receive(receivedPacket);
                byte[] message = Arrays.copyOfRange(receivedPacket.getData(), 0, receivedPacket.getLength());
                JsonObject json = Parser.deserialize(message, JsonObject.class);
                if (json != null) {
                    notifications.add(json.getAsJsonObject());
                } else {
                    System.out.println(Color.redBackground("Errore nella deserializzazione della notifica"));
                }
            } catch (Exception e) {
                System.out.println("Errore nella ricezione della notifica");
                e.printStackTrace();
            }
        }
        try {
            ms.leaveGroup(ia, netIf);
            ms.close();
        } catch (Exception e) {

        }

    }

    public static void showNotifications(String myUsername) {
        // itero sulla coda delle notifiche
        for (JsonObject notification : notifications) {
            String username = notification.get("username").getAsString();
            JsonObject message = notification.get("statistics").getAsJsonObject();
            Statistics statistic = Parser.deserialize(Parser.serialize(message), Statistics.class);
            // se la notifica e' per l'utente
            if (!myUsername.equals(username)) {
                System.out.println(Color.yellow("Statiche dell'utente: " + username));
                statistic.print();
            } else {
                System.out.println(Color.green("Queste sono le tue statistiche"));
                statistic.print();
            }
        }

    }

    public static void stopMulticast() {
        if (instance.isAlive()) {
            System.out.println("Stopping multicast...");
            instance.interrupt();
            System.out.println("Multicast stopped");
        }
    }
}

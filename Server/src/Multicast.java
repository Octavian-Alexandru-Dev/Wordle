import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Multicast {
    private static Multicast instance = getInstance();
    private static int MULTICAST_PORT = Config.MULTICAST_PORT;
    private static String MULTICAST_GROUP = Config.MULTICAST_GROUP;

    private InetAddress multicastAddress;
    public MulticastSocket ms;

    private Multicast() {
        try {
            multicastAddress = InetAddress.getByName(MULTICAST_GROUP);
            this.ms = new MulticastSocket(MULTICAST_PORT);
        } catch (Exception e) {
            System.out.println("Errore nella creazione del socket multicast");
        }
    }

    // singleton
    public static Multicast getInstance() {
        if (instance == null) {
            synchronized (Multicast.class) {
                if (instance == null) {
                    instance = new Multicast();
                }
            }
        }
        return instance;
    }

    public static boolean share(byte[] data) {
        instance = getInstance();
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, instance.multicastAddress, MULTICAST_PORT);
            instance.ms.send(packet);
            return true;
        } catch (IOException e) {
            System.out.println("Errore nell'invio del pacchetto in multicast");
            e.printStackTrace();
            return false;
        }
    }
}

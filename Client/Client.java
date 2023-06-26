package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    InetSocketAddress serverSocketAddress;
    private SocketChannel clientChannel;

    public Client(String serverAddress, int serverPort) {
        this.serverSocketAddress = new InetSocketAddress(serverAddress, serverPort);
    }

    public void connect() throws IOException {
        this.clientChannel = SocketChannel.open();
        this.clientChannel.configureBlocking(true);
        this.clientChannel.connect(this.serverSocketAddress);

        while (!this.clientChannel.finishConnect()) {
            // Attendi la connessione al server
        }

        System.out.println(
                "[Client] Connessione al server avvenuta con successo: " + this.serverSocketAddress.toString());
    }

    public byte[] sendRequest(byte[] requestData) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(requestData);

        // controllo se la connessione è aperta
        if (clientChannel == null || !(clientChannel.isConnected() && clientChannel.isOpen())) {
            System.out.println("[Client] La connessione è chiusa");
            return null;
        }

        // invio la richiesta al server
        clientChannel.write(buffer);
        System.out.println("[Client] Richiesta inviata al server");

        // ritorno la risposta del server
        return receiveResponse();
    }

    private byte[] receiveResponse() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            System.out.println("[Client] Connessione chiusa dal server");
            close();
            return null;
        } else {
            System.out.println("[Client] Ricevuti " + bytesRead + " byte dal server");
            buffer.flip();
            byte[] responseData = new byte[buffer.remaining()];
            System.out.println("[Client] Totale byte letti: " + responseData.length);
            buffer.get(responseData);
            System.out.println("[Client] Risposta ricevuta dal server");
            return responseData;
        }
    }

    public void close() throws IOException {
        if (clientChannel != null && clientChannel.isOpen()) {
            clientChannel.close();
            System.out.println("[Client] Connessione chiusa");
        }
    }
}

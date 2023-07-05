
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.google.gson.JsonObject;

public class Client {

    InetSocketAddress serverSocketAddress;
    private SocketChannel clientChannel;

    public Client(String serverAddress, int serverPort) {
        this.serverSocketAddress = new InetSocketAddress(serverAddress, serverPort);
    }

    public void connect() throws IOException {
        try {
            this.clientChannel = SocketChannel.open();
            this.clientChannel.configureBlocking(true);
            this.clientChannel.connect(this.serverSocketAddress);
            while (!this.clientChannel.finishConnect()) {
                // Attendi la connessione al server
            }
            System.out.println(
                    "[Client] Connessione al server avvenuta con successo: " + this.serverSocketAddress.toString());
        } catch (IOException e) {
            System.out.println("Errore durante la creazione della connessione");
            if (e.getMessage().equals("Connection refused")) {
                // se il server non è raggiungibile
                System.out.println(Color.redBackground("Il server è spento o non raggiungibile"));
                System.exit(1);
            } else {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public byte[] sendRequest(byte[] requestData) {
        ByteBuffer buffer = ByteBuffer.wrap(requestData);

        // controllo se la connessione è aperta
        if (clientChannel == null || !(clientChannel.isConnected() && clientChannel.isOpen())) {
            System.out.println("[Client] La connessione è chiusa");
            return null;
        }
        try {
            // invio la richiesta al server
            clientChannel.write(buffer);
            // ritorno la risposta del server
            return receiveResponse();
        } catch (IOException e) {
            System.out.println(" Erroe durrante la scrittura della richiesta");
            e.printStackTrace();
            return new byte[] {};
        }

    }

    public JsonObject sendRequest(Object requestData) {
        byte[] serializedRequest = Parser.serialize(requestData);
        byte[] serializedResponse = sendRequest(serializedRequest);
        if (serializedResponse == null || serializedResponse.length == 0) {
            return null;
        }
        return Parser.deserialize(serializedResponse, JsonObject.class);
    }

    private byte[] receiveResponse() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                System.out.println(Color.redBackground("Il server ha chiuso la connessione"));
                close();
                System.exit(1);
                return null;
            } else {
                buffer.flip();
                byte[] responseData = new byte[buffer.remaining()];
                buffer.get(responseData);
                return responseData;
            }
        } catch (IOException e) {
            System.out.println("Errore nella ricezione della risposta dal server");
            close();
            System.exit(1);
            return new byte[] {};
        }

    }

    public void close() throws IOException {
        if (clientChannel != null && clientChannel.isOpen()) {
            try {
                clientChannel.close();
                System.out.println("Ho chiuso la connessione");
            } catch (IOException e) {
                System.out.println("Errore nella chiusura della connessione");
            }
        }
    }
}

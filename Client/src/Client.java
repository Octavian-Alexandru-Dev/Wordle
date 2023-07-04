
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
            e.printStackTrace();
            throw e;
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
                // TODO: gestire la chiusura della connessione da parte del server cercando di
                // riconnettersi
                System.out.println("[Client] Connessione chiusa dal server");
                close();
                return null;
            } else {
                // System.out.println("[Client] Ricevuti " + bytesRead + " byte dal server");
                buffer.flip();
                byte[] responseData = new byte[buffer.remaining()];
                // System.out.println("[Client] Totale byte letti: " + responseData.length);
                buffer.get(responseData);
                // System.out.println("[Client] Risposta ricevuta dal server");
                return responseData;
            }
        } catch (IOException e) {
            System.out.println("Errore nella ricezione della risposta dal server");
            close();
            return new byte[] {};
        }

    }

    public void close() throws IOException {
        if (clientChannel != null && clientChannel.isOpen()) {
            try {
                clientChannel.close();
                System.out.println("[Client] Ho chiuso la connessione chiusa");
            } catch (IOException e) {
                System.out.println("[Client] Errore nella chiusura della connessione");
            }
        }
    }

    // class Listener extends Thread {
    // private Selector selector;
    //
    // public Listener() {
    // try {
    // selector = Selector.open();
    // } catch (IOException e) {
    // System.out.println("Errore nella creazione del selector");
    // e.printStackTrace();
    // }
    //
    // }
    //
    // public void run() {
    //
    // }
    // }
}

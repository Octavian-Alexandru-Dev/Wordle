package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService threadPool;
    private long maxDelay = 3000;

    public Server(int port, int poolSize) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void start() throws IOException {
        System.out.println("[Server] In ascolto sulla porta " + serverSocketChannel.socket().getLocalPort() + "...");
        while (true) {

            int readyChannels = this.selector.select(maxDelay);
            if (readyChannels == 0)
                continue;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                try {
                    if (key.isValid() && key.isReadable()) {
                        handleRead(key);
                    } else if (key.isValid() && key.isAcceptable()) {
                        handleAccept(key);
                    } else {
                        System.out.println("[Server] La richiesta ricevuto non è stata riconosciuta");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                keyIterator.remove();
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        try {
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = serverChannel.accept();

            if (clientChannel != null) {
                clientChannel.configureBlocking(false);
                clientChannel.register(selector, SelectionKey.OP_READ);
                System.out.println(
                        "[Server] Nuova connessione accettata: " + clientChannel.getRemoteAddress().toString());
            }
        } catch (Exception ex) {
            System.err.println("[Server] Errore durante l'accettazione della connessione: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        key.isWritable();
        try {
            SocketAddress clientAddress = clientChannel.getRemoteAddress();
            // Ottieni informazioni sull'indirizzo IP e la porta del client
            String clientInfo = clientAddress.toString();

            if (!clientChannel.isConnected()) {
                System.out.println("[handleRead] Connessione chiusa dal client: " + clientInfo);
                // La connessione è stata chiusa dal client
                clientChannel.close();
                key.cancel();
                return;
            }

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = clientChannel.read(buffer);

            if (bytesRead == -1) {
                System.out.println("[handleRead] Connessione chiusa dal client: " + clientInfo);
                // La connessione è stata chiusa dal client
                clientChannel.close();
                key.cancel();
            } else if (bytesRead > 0) {
                // preparo il buffer per la lettura
                buffer.flip();

                // crea un array di byte della dimensione del buffer
                byte[] requestData = new byte[buffer.remaining()];
                buffer.get(requestData);

                // Esegue l'elaborazione del dato ricevuto
                byte[] responseData = processRequest(requestData, clientInfo);

                // Invia la risposta al client
                clientChannel.write(ByteBuffer.wrap(responseData));

            }
        } catch (Exception e) {
            System.err.println("[Server] Errore durante l'handling della richiesta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            key.cancel();
        }

    }

    public boolean isRunning() {
        return !serverSocketChannel.socket().isClosed();
    }

    private byte[] processRequest(byte[] request, String id) {
        return User.handleRequest(request, id);
    }

    public void stop() throws IOException {

        // Chiudi il selettore
        if (selector != null && selector.isOpen()) {
            selector.close();
            System.out.println("[Server] Chiusura selettore...");
        }

        // Chiudi il canale del server
        serverSocketChannel.close();

        // Termina il thread pool
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS))
                threadPool.shutdownNow();
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}

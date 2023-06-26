si utilizza la classe InetAddress per rappresentare un indirizzo IP

- controllo dei tempi di permanenza in cache java.security.Security.setPropert("networkaddress.cache.ttl",”0”);
- per i tentativi non andati a buon fine: networkaddress.cache.negative.ttl

##### Lezione 1

thread pool

##### Lezione 2

threadPoolExecutor

##### Lezione 3

callable, ScheduleThreadPool, Monitor

##### Lezione 4

syncronized and concurrent collections

##### Lezione 5

InetAdress e TCP Sockets

**COME IL CLIENT ACCEDE AD UN SERVIZIO**
● per usufruire di un servizio, il client apre un socket individando host + porta che identificano il servizip en in fine invia/riceve messaggi su/da uno stream
● in JAVA: java.net.Socket
usa codice nativo per comunicare con lo stack TCP locale public socket(InetAddress host, int port) throws IOException
● crea un socket su una porta effimera e tenta di stabilire, tramite esso, una connessione con l’host individuato da InetAddress, sulla porta port.
● se la connessione viene rifiutata, lancia una eccezione di IO public socket (String host, int port) throws
UnKnownHostException, IOException
come il precedente, l’host è individuato dal suo nome simbolico: interroga automaticamente il DNS)

##### Lezione 6

Stream Socket, Volatile Atomic

##### Lezione 7

Serializzazione JSON e Java native serialization

##### Lezione 8

Java NIO Buffer e Channels

##### Lezione 9

NIO multiplexing

##### Lezione 10

UDP unicast e multicast

##### Lezione 11

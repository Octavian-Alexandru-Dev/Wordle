
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SecretWord {
    private final ExecutorService executor;
    private final static int WORDS_DURATION = Config.WORDS_DURATION;
    private final static int WORDS_LENGTH = Config.WORDS_LENGTH;
    private final static String WORDS_FILE_PATH = Config.WORDS_FILE_PATH;
    private final static String SECRET_WORD_FILE_PATH = Config.SECRET_WORD_FILE_PATH;

    // caratteri che vengono restituiti al client come suggerimento
    private final static char GREY = '_';// indica il carattere non presente
    private final static char YELLOW = 'Y';// indica il carattere presente ma nella posizione sbagliata
    private final static char GREEN = 'G';// indica il carattere presente e nella posizione giusta

    private volatile static String secretWord;
    private static LocalDateTime startAt;
    private static LocalDateTime endAt;
    private static boolean stop = false;

    public SecretWord() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public boolean isRunning() {
        return !this.executor.isShutdown();
    }

    public void stop() {
        System.out.println("Arresto del manager ...");
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                this.executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.executor.shutdownNow();
        }
        System.out.println("Manager arrestato.");
    }

    public void start() {
        System.out.println("Avvio del manager ...");
        Manager manager = new Manager();
        this.executor.execute(manager);
        System.out.println("Manager avviato.");
    }

    // usata per sapere se una parola è valida
    public static boolean wordExists(String word) {
        if (word.length() != WORDS_LENGTH) {
            return false;
        }

        try (final RandomAccessFile dictionary = new RandomAccessFile(WORDS_FILE_PATH, "r")) {
            long start = 0;
            long end = dictionary.length();
            String wordRidden;
            while (start <= end) {
                long mid = ((start + end) / 2);
                mid = mid - mid % 11;
                dictionary.seek(mid);
                wordRidden = dictionary.readLine();
                if (wordRidden.compareTo(word) == 0)
                    return true;
                else if (wordRidden.compareTo(word) < 0)
                    start = mid + 10;
                else
                    end = mid - 10;
            }
            return false;
        } catch (FileNotFoundException e) {
            System.out.println(
                    "[SERVER] Errore nell'analisi del file delle parole: file non trovato.\n" + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("[SERVER] Errore nell'analisi del file delle parole.\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // TODO : stabilire se e più efficiente leggere da un file oppure caricare tutto
    // in memoria
    // funzione prende la parola da provare,
    // restituisce la parola colorata in base alla Secret Word
    public static String getWordHint(String guessedWord) {
        StringBuilder hints = new StringBuilder();
        for (int i = 0; i < secretWord.length(); i++) {
            char currChar = guessedWord.charAt(i);
            if (secretWord.charAt(i) == currChar) { // lettera nella posizione giusta
                hints.append(GREEN);
            } else if (secretWord.indexOf(currChar) != -1) { // lettera c'è ma non nella posizione i
                hints.append(YELLOW);
            } else { // lettera non ce
                hints.append(GREY);
            }
        }
        return hints.toString();
    }

    public static boolean isSecretWord(String guessedWord) {
        return secretWord.equals(guessedWord);
    }

    public static String getEndTime() {
        // restituisco solamente l'orario
        return endAt.format(Config.TIME_FORMAT);
    }

    public static String getWord() {
        return secretWord;
    }

    public static LocalDateTime getEndAt() {
        return endAt;
    }

    class Manager implements Runnable {

        @Override
        // Il thread genera una nuova Secret Word ogni
        public void run() {
            boolean loaded = loadLastSecretWord();
            try {
                while (!stop) {
                    LocalDateTime now = LocalDateTime.now();
                    if (endAt.isBefore(now)) {
                        System.out.println("La Secret Word è scaduta");
                        generateNewSecretWord();
                    }
                    long delay = java.time.Duration.between(now, endAt).toMillis();
                    System.out.println("SecretWord: " + Color.greenBackground(secretWord));
                    System.out.println("La Secret Word cambierà alle " + endAt.format(Config.TIME_FORMAT));
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
            }
        }

        // aggiorna il file
        private static void writeSecretWord() {
            JsonObject obj = new JsonObject();
            obj.addProperty("secretWord", secretWord);
            obj.addProperty("startAt", startAt.toString());
            try (FileWriter writer = new FileWriter(SECRET_WORD_FILE_PATH)) {
                writer.write(obj.toString());
            } catch (IOException e) {
                System.err.println("Errore durante la scrittura del file JSON: " + e.getMessage());
            }
        }

        private static void generateNewSecretWord() {
            System.out.println("Genero una nuova Secret Word ...");
            secretWord = getRandomWord();
            startAt = LocalDateTime.now();
            endAt = startAt.plusMinutes(WORDS_DURATION);
            writeSecretWord();
            Client.newWordEvent();
        }

        // sceglie una Secret Word in modo random
        private static String getRandomWord() {
            try (RandomAccessFile words = new RandomAccessFile(WORDS_FILE_PATH, "r")) {
                long random = (long) (Math.random() * words.length());
                random = random - random % 11;
                words.seek(random);
                String secretWord = words.readLine();
                return secretWord;
            } catch (IOException e) {
                System.out.println("Errore durante la lettura del dizionario");
                throw new RuntimeException(e);
            }
        }

        // inizializza la secretword e il tempo di inizio con i vecchi valori
        public static boolean loadLastSecretWord() {
            File input = new File(SECRET_WORD_FILE_PATH);
            try {
                JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
                JsonObject lastWord = fileElement.getAsJsonObject();
                if (lastWord != null) {
                    secretWord = lastWord.get("secretWord").getAsString();
                    startAt = LocalDateTime.parse(lastWord.get("startAt").getAsString());
                    endAt = startAt.plusMinutes(WORDS_DURATION);
                }
                System.out.println("Ho caricato la Secret Word dal file");
                return true;
            } catch (Exception e) {
                // TODO diferenziare la gestione degli errori per FileNotFoundException e per
                // JsonSyntaxException
                System.out.println("Errore di lettura del file:" + SECRET_WORD_FILE_PATH);
                secretWord = "";
                startAt = LocalDateTime.now().plusMinutes(-WORDS_DURATION * 2);
                endAt = startAt;
                e.printStackTrace();
                return false;
            }
        }

    }
}
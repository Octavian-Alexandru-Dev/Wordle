import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Player {
    public static ConcurrentHashMap<String, Boolean> loggedPlayer = new ConcurrentHashMap<>();
    private final String path;
    private Client client;
    private String username;
    private JsonObject json;
    private final static int MAX_ATTEMPTS = Config.PLAYER_MAX_ATTEMPTS;
    private PlayerStatistics statistics;

    private String lastWord;
    // indica quando il giocatore può giocare di nuovo
    // viene settato quando il giocatore vince o perde una partita
    // serve sopratutto quando il server si riavvia e per lo stato del giocatore
    private volatile boolean hasFinishMatch;

    public Player(Client client) {
        this.client = client;
        this.username = client.getUsername();
        this.path = Config.USER_FILE_PATH + this.username + ".json";
        this.json = client.readUserResource();

        JsonElement statistics = this.json.get("statistics");
        this.statistics = statistics != null ? new PlayerStatistics(statistics.getAsJsonObject())
                : new PlayerStatistics(null);

        // caso in cui il giocatore abbia già concluso la partita
        // con la parola segreta attuale
        JsonElement tempHasFinishMatch = this.json.get("hasFinishMatch");
        this.hasFinishMatch = tempHasFinishMatch != null ? tempHasFinishMatch.getAsBoolean() : false;

        JsonElement lastWord = this.json.get("lastWord");
        if (lastWord != null && SecretWord.isSecretWord(lastWord.getAsString())) {
            this.lastWord = lastWord.getAsString();
        } else {
            // questa è una nuova partita
            this.statistics.newMatch();
            this.hasFinishMatch = false;
            this.lastWord = SecretWord.getWord();
        }

    }

    // questo metodo viene chiamato dal thread che gestisce la Secret Word
    // nel momento in viene generata una nuova Secret Word
    public void newWordEvent() {
        if (!hasFinishMatch) {
            this.statistics.newLose();
            writeStatistics();
            // il metodo this.statistics.newMatch() verra chimato solamente quando il client
            // inviera una nuova GuessWord
        }
        this.hasFinishMatch = false;
    }

    public boolean canPlay() {
        return !hasFinishMatch;
    }

    public JsonObject play() {
        // this.statistics.newMatch();
        JsonObject obj = new JsonObject();
        obj.addProperty("endAt", SecretWord.getEndTime());
        obj.addProperty("remainingAttempts", (MAX_ATTEMPTS - this.statistics.getAttempts()));
        obj.addProperty("hasFinishedMatch", hasFinishMatch);
        // obj.addProperty("WORDS_DURATION", Config.WORDS_DURATION);
        return obj;
    }

    public String sendWord(String guessedWord) {
        // controllo se il giocatore ha raggiunto il numero massimo di tentativi
        if (hasFinishMatch) {
            return "MATCH_FINISHED";
        } else if (!SecretWord.isSecretWord(this.lastWord)) {
            // se la parola è cambiata la aggiorno e comunico il cambiamento al client
            this.lastWord = SecretWord.getWord();

            this.statistics.newMatch();
            return "WORD_CHANGED";
        } else if (this.statistics.getAttempts() >= MAX_ATTEMPTS) {
            System.out.println(
                    "Il giocatore " + Color.red(this.username) + " ha raggiunto il numero massimo di tentativi!");
            System.out.println("Attempts: " + Color.green(this.statistics.getAttempts()));
            return "MAX_ATTEMPTS_REACHED";
        } else {
            // controllo se la parola è valida
            if (SecretWord.wordExists(guessedWord)) {
                // aggiorno le statistiche del giocatore
                statistics.newAttempt();
                // controllo se la parola è quella segreta
                if (SecretWord.isSecretWord(guessedWord)) {
                    statistics.newWin();
                    System.out
                            .println("Il giocatore '" + Color.green(this.username) + "' ha indoivinato la SecretWord!");
                    this.hasFinishMatch = true;
                    this.writeStatistics();
                } else if (this.statistics.getAttempts() >= MAX_ATTEMPTS) {
                    // se il giocatore ha raggiunto il numero massimo di tentativi
                    statistics.newLose();
                    System.out.println("Il giocatore '" + Color.green(this.username)
                            + "' ha raggiunto il numero massimo di tentativi!");
                    this.hasFinishMatch = true;
                    this.writeStatistics();
                }

                return SecretWord.getWordHint(guessedWord);
            } else {
                return "INVALID_WORD";
            }
        }
    }

    public boolean logout() {
        loggedPlayer.remove(this.username);
        // aggiorno le statisctiche del giocatore
        return writeStatistics();
    }

    public JsonElement getStatistics() {
        return Parser.toJsonElement(this.statistics);
    }

    public boolean writeStatistics() {
        // aggiorno le variabili di stato del giocatore
        this.statistics.setAttempts(MAX_ATTEMPTS);

        // aggiongo le statistiche del giocatore al JSON
        this.json.add("lastWord", Parser.toJsonElement(this.lastWord));
        this.json.add("hasFinishMatch", Parser.toJsonElement(hasFinishMatch));
        this.json.add("statistics", Parser.toJsonElement(this.statistics));
        byte[] json = Parser.serialize(this.json);

        // scrivo le statistiche del giocatore nel file JSON
        try (FileWriter writer = new FileWriter(this.path)) {
            writer.write(new String(json));
            System.out.println("Statistiche aggiornate per: " + Color.green(this.username));
            return true;
        } catch (IOException e) {
            System.err.println("Si è verificato un errore durante la scrittura del file JSON: " + e.getMessage());
            return false;
        }
    }

}
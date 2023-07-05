
import java.time.LocalTime;
import java.util.ArrayList;

import com.google.gson.JsonObject;

public class Game {
    private final static int MAX_ATTEMPTS = Config.PLAYER_MAX_ATTEMPTS;
    public final static int WORDS_LENGTH = Config.WORDS_LENGTH;

    private boolean started;
    private Client client;
    private boolean isLogged;
    private boolean isRegistered;
    private boolean isPlaying;
    private Input input;
    private String username;

    // private int WORDS_DURATION;

    private LocalTime endAt;
    private int remainingAttempts;
    private boolean winStatus;
    private boolean hasFinishedMatch;

    // array che contine
    private ArrayList<String> wordsTried;
    // private String password;

    public Game(Client client) {
        this.started = false;
        this.client = client;
        this.isLogged = false;
        this.isRegistered = false;
        this.isPlaying = false;
        this.input = new Input();
        this.username = "";
        this.wordsTried = new ArrayList<String>();
        this.remainingAttempts = MAX_ATTEMPTS;
        this.winStatus = false;
    }

    public void showMeSharing() {
        System.out.println("\nHai scelto " + Color.yellow("SHOWMESHARING"));
        Multicast.showNotifications(this.username);
    }

    public void sendMeStatistics() {
        System.out.println("\nHai scelto " + Color.yellow("SENDMESTATISTICS"));
        Request request = new Request("sendMeStatistics");
        JsonObject response = client.sendRequest(request);
        if (response.get("status").getAsInt() == 200) {
            System.out.println("Statistiche:");
            JsonObject jsonStatistics = response.get("message").getAsJsonObject();
            Statistics statistics = new Statistics(jsonStatistics);
            statistics.print();
        } else {
            System.out.println("Errore nel recupero delle statistiche");
        }
    }

    public void share() {
        System.out.println("\nHai scelto " + Color.yellow("SHARE"));
        Request request = new Request("share");
        JsonObject response = client.sendRequest(request);
        if (response.get("status").getAsInt() == 200) {
            System.out.println("Condivisione effettuata con successo !!");
        } else {
            System.out.println("Errore nel recupero delle statistiche");
        }
    }

    public void play() {
        start();
        while (this.started) {
            Operation[] ops = getAvailableOperation();
            int chosedOp = choseOperation(ops);
            performOperation(chosedOp, ops);
        }
        stop();
    }

    private void playWORDLE() {
        System.out.println("\nHai scelto " + Color.yellow("PLAYWORDLE"));
        Request request = new Request("playWORDLE");
        JsonObject response = client.sendRequest(request);
        if (response.get("status").getAsInt() == 200) {
            System.out.println("Partita iniziata");
            try {
                JsonObject message = response.get("message").getAsJsonObject();
                if (message != null) {
                    hasFinishedMatch = message.get("hasFinishedMatch").getAsBoolean();
                    String endAtString = message.get("endAt").getAsString();
                    endAt = LocalTime.parse(endAtString);
                    remainingAttempts = message.get("remainingAttempts").getAsInt();
                    // WORDS_DURATION = message.get("WORDS_DURATION").getAsInt();

                    if (hasFinishedMatch) {
                        System.out.println(Color.red("Hai già giocato questa partita, dovrai aspettare la prossima "));
                    } else {
                        System.out.println("Hai ancora " + remainingAttempts + " tentativi");
                    }
                    System.out.println("Hai tempo fino alle " + Color.yellow(endAt.toString())
                            + " per indovianre la Secret Word.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.isPlaying = true;
        } else {
            System.out.println("Inizio partita fallito");
            System.out.println("Risposta => " + response.toString());
        }
    }

    private void sendWord() {
        if (this.winStatus && endAt.isAfter(LocalTime.now())) {
            System.out.println("\nHai già indovinato la Secret Word !!");
            System.out.println("La prossima Secret Word sarà disponibile alle " + Color.yellow(endAt.toString()));
            return;
        }

        System.out.println("\nHai scelto " + Color.yellow("SENDWORD"));
        System.out.println("Hai ancora " + remainingAttempts + " tentativi");

        System.out.println("Digita 'exit' per uscire tornare al menu principale");
        while ((remainingAttempts > 0 && !this.hasFinishedMatch) || this.endAt.isBefore(LocalTime.now())) {
            System.out.print(Color.yellow("Inserisci la parola: "));

            // leggo la parola inserita dall'utente
            String guessedWord = this.input.readString(WORDS_LENGTH);
            if (guessedWord.equals("exit")) {
                return;
            }
            send(guessedWord);
        }
        if (remainingAttempts <= 0) {
            System.out.println("Hai raggiunto il numero massimo di tentativi");
            System.out.println(
                    "La prossima Secret Word sarà disponibile alle " + Color.yellow(endAt.toString()));
            this.hasFinishedMatch = true;
            return;
        }

    }

    // questo metodo si occupa di inviare la guessWord al server
    // e si gestire le risposte ricevute
    private void send(String guessedWord) {
        GuessRequest request = new GuessRequest("sendWord", guessedWord);

        // invio la parola al server
        JsonObject response = client.sendRequest(request);

        // controllo la risposta del server
        if (response.get("status").getAsInt() == 200) {
            try {
                JsonObject message = response.get("message").getAsJsonObject();
                String pattern = message.get("pattern").getAsString();
                if (pattern.equals("MAX_ATTEMPTS_REACHED")) {
                    // questo non dovrebbe mai succedere se
                    // il client continua a fare i dovuti controlli
                    System.out.println("Erroe: hai raggiunto il numero massimo di tentativi");
                    return;
                } else if (pattern.equals("INVALID_WORD")) {
                    System.out.println(Color.red("La parola non è presente nel dizionario"));
                    return;
                } else if (pattern.equals("MATCH_FINISHED")) {
                    // questo è il caso in cui il clinet ha indovinato la Secret Word
                    // oppure ha esaurito i tentativi
                    // ma sta provando a inviarne una nuova
                    System.out.println("Per te il match e finito");
                    System.out.println(
                            "La prossima Secret Word sarà disponibile alle " + Color.yellow(endAt.toString()));
                    return;
                } else if (pattern.equals("WORD_CHANGED")) {
                    System.out.println(Color.red("Attenzione la Secret Word è stata cambiata nel frattempo"));
                    String endAtString = message.get("endAt").getAsString();
                    newSecretWord(LocalTime.parse(endAtString));
                    // reinvio la guesseWord che l'utente ha scelto
                    send(guessedWord);
                } else {
                    String coloredWord = Color.colorGuessedWord(guessedWord, pattern);
                    // aggiungo la parola alla lista delle parole provate
                    this.wordsTried.add(coloredWord);
                    // controllo se l'utente ha vinto e lo comunico
                    if (iWon(pattern)) {
                        System.out.println(Color.green("Hai vinto!"));
                        System.out.println("La Secret Word era: " + coloredWord);
                        System.out.println(
                                "La prossima Secret Word sarà disponibile alle " + Color.yellow(endAt.toString()));
                        return;
                    } else {
                        System.out.println("\nLe parole che hai provato sono:");
                        for (String word : this.wordsTried) {
                            System.out.println(word);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            remainingAttempts = MAX_ATTEMPTS - this.wordsTried.size();
        } else {
            System.out.println("Invio parola fallito");
            System.out.println("Risposta => " + response.toString());
        }
    }

    private void logout() {
        System.out.println("\nHai scelto " + Color.yellow("Logout"));
        AuthRequest authRequest = new AuthRequest("logout", this.username);
        JsonObject response = client.sendRequest(authRequest);
        if (response.get("status").getAsInt() == 200) {
            System.out.println("Logout effettuato con successo");
            this.isLogged = false;
            this.isPlaying = false;
        } else {
            System.out.println("Logout fallito");
            System.out.println("Risposta => " + response.toString());
        }
    }

    private void login() {
        System.out.println("\nHai scelto " + Color.yellow("LOGIN"));
        System.out.println("Digita 'exit' per uscire tornare al menu principale");
        String[] credentials = this.input.readCredentials();
        if (!credentials[0].equals("exit")) {
            AuthRequest authRequest = new AuthRequest("login", credentials[0], credentials[1]);
            JsonObject response = client.sendRequest(authRequest);
            if (response.get("status").getAsInt() == 200) {
                System.out.println("Login effettuato con successo");
                // aggiorno il flag di login e di registrazione
                this.isLogged = true;
                this.isRegistered = true;
                // salvo l'username
                this.username = credentials[0];
                // Avvio del multicast
                Multicast.getInstance().start();
                while (!Multicast.active.get()) {
                    // aspetto che il multicast sia attivo
                }
            } else {
                System.out.println("Login fallito");
                System.out.println("Risposta => " + response.toString());
            }
        }
    }

    private void register() {
        System.out.println("\nHai scelto " + Color.yellow("REGISTER"));
        System.out.println("Digita 'exit' per uscire tornare al menu principale");
        String[] credentials = this.input.readCredentials();
        if (!credentials[0].equals("exit")) {
            AuthRequest authRequest = new AuthRequest("register", credentials[0], credentials[1]);
            JsonObject response = this.client.sendRequest(authRequest);
            if (response.get("status").getAsInt() == 200) {
                System.out.println("Registrazione effettuato con successo");
                // aggiorno il flag di registrazione
                this.isRegistered = true;
                // salvo l'username
                this.username = credentials[0];
            } else {
                System.out.println("Registrazione fallita");
                System.out.println("Risposta => " + response.toString());
            }
        }
    }

    private void exit() {
        System.out.println("Uscita dal gioco ...");
        this.started = false;
        stop();
        Multicast.stopMulticast();
        System.exit(0);
    }

    private void newSecretWord(LocalTime endAt) {
        this.endAt = endAt;
        this.winStatus = false;
        this.hasFinishedMatch = false;
        this.wordsTried.clear();
        this.remainingAttempts = MAX_ATTEMPTS;
    }

    private boolean iWon(String pattern) {
        if (pattern.equals(Color.SecretWordPattern)) {
            this.winStatus = true;
            this.hasFinishedMatch = true;
            return true;
        } else {
            return false;
        }
    }

    private void start() {
        // Connessione al server
        try {
            this.client.connect();
        } catch (Exception e) {
            System.out.println("Errore nella connessione al server");
            e.printStackTrace();
            return;
        }
        this.started = true;
    }

    private void stop() {
        // Connessione al server
        try {
            this.client.close();
        } catch (Exception e) {
            System.out.println("Errore durante la chiusura della connessione con il server");
            e.printStackTrace();
            return;
        }
        this.started = true;
    }

    public String getUsername() {
        return this.username;
    }

    private void performOperation(int op, Operation[] ops) {
        // converto l'operazione in un intero
        try {
            if (op == 0) {
                exit();
            } else {
                ;
                switch (ops[op - 1]) {
                    case REGISTER:
                        register();
                        break;
                    case LOGIN:
                        login();
                        break;
                    case LOGOUT:
                        logout();
                        break;
                    case PLAYWORDLE:
                        playWORDLE();
                        break;
                    case SENDWORD:
                        sendWord();
                        break;
                    case SENDMESTATISTICS:
                        sendMeStatistics();
                        break;
                    case SHARE:
                        share();
                        break;
                    case SHOWMESHARING:
                        showMeSharing();
                        break;
                    case REFRESH:
                        break;
                    default:
                        System.out.println("Hai scelto Operazione non valida");
                        break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("L'operazione scelta non è valida, scegli un numero");
            e.printStackTrace();
            return;
        }
    }

    private Operation[] getAvailableOperation() {

        if (!this.isRegistered || !this.isLogged) {
            return new Operation[] { Operation.LOGIN, Operation.REGISTER };
        } else if (!this.isPlaying) {
            return new Operation[] { Operation.PLAYWORDLE, Operation.LOGOUT };
        } else if (hasFinishedMatch && endAt.isAfter(LocalTime.now())) {
            return new Operation[] { Operation.SENDMESTATISTICS, Operation.SHARE,
                    Operation.SHOWMESHARING, Operation.LOGOUT, Operation.REFRESH };
        } else {
            return new Operation[] { Operation.SENDWORD, Operation.SENDMESTATISTICS, Operation.SHARE,
                    Operation.SHOWMESHARING, Operation.LOGOUT, Operation.REFRESH };
        }
    }

    private int choseOperation(Operation[] ops) {
        System.out.println("\nScegli un'operazione:");
        System.out.println(Color.green(0) + " => Esci");
        for (int i = 0; i < ops.length; i++) {
            System.out.println(Color.green(i + 1) + " => " + ops[i].toString().toUpperCase());
        }
        System.out.print("Operazione scelta: ");
        return this.input.readInt(ops.length);
    }

    enum Operation {
        LOGIN, REGISTER, LOGOUT, PLAYWORDLE, SENDWORD, SENDMESTATISTICS, SHARE, SHOWMESHARING, REFRESH;

        public String toString() {
            switch (this) {
                case LOGIN:
                    return "login";
                case REGISTER:
                    return "register";
                case LOGOUT:
                    return "logout";
                case PLAYWORDLE:
                    return "playwordle";
                case SENDWORD:
                    return "sendword";
                case SENDMESTATISTICS:
                    return "sendmestatistics";
                case SHARE:
                    return "share";
                case SHOWMESHARING:
                    return "showmesharing";
                case REFRESH:
                    return "Ricarica le opzioni";
                default:
                    return "";
            }
        }
    }
}
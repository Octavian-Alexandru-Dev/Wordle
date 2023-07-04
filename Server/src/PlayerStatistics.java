import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PlayerStatistics {
    private static final int MAX_ATTEMPTS = Config.PLAYER_MAX_ATTEMPTS;
    private int playedMatches;
    private float winPercentage;
    private int wonMatches;
    private int streakWin;
    private int maxStreakWin;
    private int[] guessDistribution;
    private int currentAttemps;

    public PlayerStatistics(JsonObject json) {
        if (json == null) {
            this.currentAttemps = 0;
            this.playedMatches = 0;
            this.wonMatches = 0;
            this.streakWin = 0;
            this.maxStreakWin = 0;
            this.winPercentage = 0;
            this.guessDistribution = new int[MAX_ATTEMPTS];
            return;
        } else {
            this.playedMatches = json.get("playedMatches") != null ? json.get("playedMatches").getAsInt() : 0;
            this.wonMatches = json.get("wonMatches") != null ? json.get("wonMatches").getAsInt() : 0;
            this.streakWin = json.get("streakWin") != null ? json.get("streakWin").getAsInt() : 0;
            this.maxStreakWin = json.get("maxStreakWin") != null ? json.get("maxStreakWin").getAsInt() : 0;
            this.winPercentage = json.get("winPercentage") != null ? json.get("winPercentage").getAsInt() : 0;

            if (json.get("guessDistribution") != null) {
                JsonArray arr = json.get("guessDistribution").getAsJsonArray();
                this.guessDistribution = new int[MAX_ATTEMPTS];
                for (int i = 0; i < arr.size(); i++) {
                    this.guessDistribution[i] = arr.get(i).getAsInt();
                }
            } else {
                this.guessDistribution = new int[MAX_ATTEMPTS];
            }
        }

    }

    public void newMatch() {
        this.playedMatches++;
        this.currentAttemps = 0;
    }

    public int getAttempts() {
        return this.currentAttemps;
    }

    public void newAttempt() {
        this.currentAttemps++;
    }

    public void newWin() {
        // incremento il numero di partite vinte
        this.wonMatches++;
        // incremento la streak di vittorie attuale
        this.streakWin++;
        // aggiorno la streak massima di vittorie
        if (this.streakWin > this.maxStreakWin) {
            this.maxStreakWin = this.streakWin;
        }
        // aggiorno la percentuale di vittorie
        this.winPercentage = ((float) this.wonMatches / (float) this.playedMatches) * 100;
        // aggiorno la distribuzione delle risposte
        this.guessDistribution[currentAttemps - 1]++;
        this.currentAttemps = 0;
    }

    // questo metodo viene chiamaot quando il giocatore partecipa ad una partita
    // senza riuscire ad indovinare la SecretWord
    public void newLose() {
        this.streakWin = 0;
        // aggiorno la percentuale di vittorie
        this.winPercentage = (this.wonMatches / this.playedMatches) * 100;
        // this.currentAttemps = 0;
    }

    public void setAttempts(int attempts) {
        this.currentAttemps = attempts;
    }

}

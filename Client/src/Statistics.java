import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Statistics {
    // private static final int MAX_ATTEMPTS = Config.PLAYER_MAX_ATTEMPTS;
    public int playedMatches;
    public float winPercentage;
    public int wonMatches;
    public int streakWin;
    public int maxStreakWin;
    public int[] guessDistribution;
    public int currentAttemps;

    public Statistics(JsonObject json) {
        this.playedMatches = json.get("playedMatches") != null ? json.get("playedMatches").getAsInt() : 0;
        this.wonMatches = json.get("wonMatches") != null ? json.get("wonMatches").getAsInt() : 0;
        this.streakWin = json.get("streakWin") != null ? json.get("streakWin").getAsInt() : 0;
        this.maxStreakWin = json.get("maxStreakWin") != null ? json.get("maxStreakWin").getAsInt() : 0;
        this.winPercentage = json.get("winPercentage") != null ? json.get("winPercentage").getAsInt() : 0;
        JsonArray arr = json.get("guessDistribution").getAsJsonArray();
        this.guessDistribution = new int[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            this.guessDistribution[i] = arr.get(i).getAsInt();
        }
    }

    public String getguessDistribution() {
        String s = "\n";
        for (int i = 0; i < guessDistribution.length; i++) {

            s += (i + 1) + ") " + guessDistribution[i] + "\n";
        }
        return s;
    }
}

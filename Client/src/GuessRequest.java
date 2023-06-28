
public class GuessRequest {
    private String method;
    private String guessedWord;

    public GuessRequest(String method, String guessedWord) {
        this.method = method;
        this.guessedWord = guessedWord;
    }

    public String getGuessedWord() {
        return guessedWord;
    }

    public String getMethod() {
        return method;
    }
}

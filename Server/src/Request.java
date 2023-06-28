
public class Request {

    private String method;

    // se è una richiesta con metodo REGISTER, LOGIN o LOGOUT è stringa
    // codificata in base64
    private String credentials;

    // se è una richiesta con metodo "PLAY_WORDLE" allora contine la Guessed Word
    // alrimenti stringa vuota
    private String guessedWord;

    public Request(byte[] req) throws Exception {
        // deserializzo la richiesta
        Request request = Parser.deserialize(req, Request.class);
        this.method = request.getMethod();
        this.credentials = request.getCredentials();
        this.guessedWord = request.getGuessedWord();

        // eseguo la validazione della richiesta
        validateMethod();
        validatePayload();
    }

    public String getMethod() {
        return method;
    }

    public String getCredentials() {
        return credentials == null ? "" : credentials;
    }

    public String getGuessedWord() {
        return guessedWord == null ? "" : guessedWord;
    }

    private void validateMethod() throws IllegalArgumentException {
        if (isRegisterRequest() || isLoginRequest() || isLogoutRequest() || isPlayWordleRequest()
                || isSendWordRequest() || isSendMeStatisticsRequest() || isShareRequest()
                || isShowMeSharingRequest()) {
        } else {
            // lancio un errore perche il metodo non è valido
            throw new IllegalArgumentException("Metodo non valido");
        }
    }

    private void validatePayload() throws IllegalArgumentException {
        if (isLoginRequest() || isRegisterRequest() || isLogoutRequest()) {
            if (credentials.equals("")) {
                // lancio un errore perche non ho ricevuto le credenziali
                throw new IllegalArgumentException("Credenziali non ricevute");
            }
        } else if (isSendWordRequest()) {
            if (guessedWord.equals("")) {
                // lancio un errore perche non ho ricevuto la parola
                throw new IllegalArgumentException("Parola non ricevuta");
            }
        } else {
            if (!credentials.equals("") || !guessedWord.equals("")) {
                // lancio un errore perche non mi aspetto di ricevere credenziali o parola
                throw new IllegalArgumentException("Payload non valido");
            }
        }
    }

    public boolean isRegisterRequest() {
        return method.equals("register");
    }

    public boolean isLoginRequest() {
        return method.equals("login");
    }

    public boolean isLogoutRequest() {
        return method.equals("logout");
    }

    public boolean isPlayWordleRequest() {
        return method.equals("playWORDLE");
    }

    public boolean isSendWordRequest() {
        return method.equals("sendWord");
    }

    public boolean isSendMeStatisticsRequest() {
        return method.equals("sendMeStatistics");
    }

    public boolean isShareRequest() {
        return method.equals("share");
    }

    public boolean isShowMeSharingRequest() {
        return method.equals("showMeSharing");
    }

}
package Server;

import com.google.gson.JsonObject;

public class Request {

    private Method method;

    // nel caso di registrazione, login e logout, il payload è una stringa
    // codificata in base64
    // nel caso di una richiesta di gioco, potrebbe essere o la Guessed Word oppure
    // stringa vuota
    private String payload;

    public Request(JsonObject request) {
        setMethod(request);
        setPayload(request);
    }

    public boolean isValid() {
        return method != Method.INVALID;
    }

    public Method getMethod() {
        return method;
    }

    public String getPayload() {
        return payload;
    }

    private void setMethod(JsonObject reqObject) {
        if (reqObject.get("method") != null) {
            switch (reqObject.get("method").getAsString()) {
                case "register":
                    method = Method.REGISTER;
                case "login":
                    method = Method.LOGIN;
                case "logout":
                    method = Method.LOGOUT;
                case "playWORDLE":
                    method = Method.PLAY_WORDLE;
                case "sendWord":
                    method = Method.SEND_WORD;
                case "sendMeStatistics":
                    method = Method.SEND_ME_STATISTICS;
                case "share":
                    method = Method.SHARE;
                case "showMeSharing":
                    method = Method.SHOW_ME_SHARING;
                default:
                    method = Method.INVALID;
            }
        }
    }

    private void setPayload(JsonObject reqObject) {
        if (method == Method.REGISTER || method == Method.LOGIN || method == Method.LOGOUT) {
            if (reqObject.get("credentials") == null) {
                payload = "";
                method = Method.INVALID;
                return;
            }
            payload = reqObject.get("credentials").getAsString();

        } else if (method == Method.SEND_WORD) {
            if (reqObject.get("word") == null) {
                payload = "";
                method = Method.INVALID;
                return;
            }
            payload = reqObject.get("word").getAsString();
        } else {
            this.payload = "";
        }
    }

    public boolean isRegisterRequest() {
        return method == Method.REGISTER;
    }

    public boolean isLoginRequest() {
        return method == Method.LOGIN;
    }

    public boolean isLogoutRequest() {
        return method == Method.LOGOUT;
    }

    public boolean isPlayWordleRequest() {
        return method == Method.PLAY_WORDLE;
    }

    public boolean isSendWordRequest() {
        return method == Method.SEND_WORD;
    }

    public boolean isSendMeStatisticsRequest() {
        return method == Method.SEND_ME_STATISTICS;
    }

    public boolean isShareRequest() {
        return method == Method.SHARE;
    }

    public boolean isShowMeSharingRequest() {
        return method == Method.SHOW_ME_SHARING;
    }

    private static enum Method {
        // Metodi che nel payload hanno una stringa codificata in base64
        REGISTER,
        LOGIN,
        LOGOUT,

        // Metodi che nel payload hanno una stringa in utf-8
        SEND_WORD,

        // Metodi che non hanno payload
        PLAY_WORDLE,
        SEND_ME_STATISTICS,
        SHARE,
        SHOW_ME_SHARING,
        INVALID,
    }

}
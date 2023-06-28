
class Response {

    private int status;
    private String statusMessage;
    private Object message;

    public Response(Status status, Object message) {
        this.status = getStatusCode(status);
        this.statusMessage = getStatusMessage(status);
        this.message = message;
    }

    public String toString() {
        return "Status: " + status + "\nStatus Message: " + statusMessage + "\nMessage: " + message;
    }

    private int getStatusCode(Status status) {
        switch (status) {
            case OK:
                return 200;
            case BAD_REQUEST:
                return 400;
            case UNAUTHORIZED:
                return 401;
            case NOT_FOUND:
                return 404;
            case METHOD_NOT_ALLOWED:
                return 405;
            case CONFLICT:
                return 409;
            case INTERNAL_SERVER_ERROR:
                return 500;
            default:
                return 500;
        }
    }

    private String getStatusMessage(Status status) {
        switch (status) {
            case OK:
                return "200 OK";
            case BAD_REQUEST:
                return "400 Bad Request";
            case UNAUTHORIZED:
                return "401Unauthorized";
            case NOT_FOUND:
                return "404 Not Found ";
            case METHOD_NOT_ALLOWED:
                return "405 Method Not Allowed";
            case CONFLICT:
                return "409 Conflict";
            case INTERNAL_SERVER_ERROR:
                return "500 Internal Server Error";
            default:
                return "";
        }
    }

    public enum Status {
        OK,
        BAD_REQUEST, // 400
        UNAUTHORIZED, // 401
        NOT_FOUND, // 404
        METHOD_NOT_ALLOWED, // 405
        CONFLICT, // 409
        INTERNAL_SERVER_ERROR,// 500
    }
}

package Server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class Response {

    public static JsonElement creat(Status status, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("status", getStatusCode(status));
        json.addProperty("message", getStatusMessage(status) + message);
        return json;
    }

    private static String getStatusMessage(Status status) {
        switch (status) {

            case BAD_REQUEST:
                return "Bad Request: ";
            case UNAUTHORIZED:
                return "Unauthorized: ";
            case METHOD_NOT_ALLOWED:
                return "Method Not Allowed: ";
            case INTERNAL_SERVER_ERROR:
                return "Internal Server Error: ";
            default:
                return "";
        }
    }

    private static int getStatusCode(Status status) {
        switch (status) {
            case OK:
                return 200;
            case BAD_REQUEST:
                return 400;
            case UNAUTHORIZED:
                return 401;
            case METHOD_NOT_ALLOWED:
                return 405;
            case INTERNAL_SERVER_ERROR:
                return 500;
            default:
                return 0;
        }
    }

    public static enum Status {
        OK,
        BAD_REQUEST, // 400
        UNAUTHORIZED, // 401
        METHOD_NOT_ALLOWED, // 405
        INTERNAL_SERVER_ERROR,// 500
    }
}
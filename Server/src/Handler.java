import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Handler {
    public Handler() {
    }

    public static Response handleRequest(Request req, String connID) {
        // System.out.println(req.getMethod());
        // System.out.println(req.getCredentials());
        // controllo se l'utente è registrato
        if (Client.clients.containsKey(connID)) {
            Client client = Client.clients.get(connID);

            if (req.isRegisterRequest()) {
                return registerRequesthandler(req, connID, client);

            } else if (req.isLoginRequest()) {
                return loginRequestHandler(req, connID, client);

            } else if (req.isLogoutRequest()) {
                return logoutRequestHandler(req, connID, client);

            } else if (req.isPlayWordleRequest()) {
                return playWordleHandler(req, connID, client);

            } else if (req.isSendWordRequest()) {
                return sendWordHandler(req, connID, client);

            } else if (req.isSendMeStatisticsRequest()) {
                return sendMeStatisticsHandler(req, connID, client);

            } else if (req.isShareRequest()) {
                return shareHandler(req, connID, client);
            } else {
                return new Response(Response.Status.INTERNAL_SERVER_ERROR, " TODO");
            }
        } else {
            System.out.println("[handleRequest] L'utente non è presente nella hashmap");
            // se non è presente nella hashmap, mi aspetto che il tipo di richiesta che ho
            // ricevuto sia di registrazione, login
            Client client = new Client(req.getCredentials(), connID);

            // inserisco l'utente nella hashmap
            Client.clients.put(connID, client);
            if (req.isRegisterRequest()) {
                return registerRequesthandler(req, connID, client);
            } else if (req.isLoginRequest()) {
                return loginRequestHandler(req, connID, client);
            } else {
                return new Response(Response.Status.BAD_REQUEST, "");
            }

        }

    }

    // questo mettodo che viene chiamato dal server quando un client si disconnette
    public static void logout(String connID) {
        Client client = Client.clients.get(connID);
        if (client != null) {
            client.logout();
        }
    }

    private static Response logoutRequestHandler(Request req, String connID, Client client) {
        if (!client.isLogged()) {
            return new Response(Response.Status.METHOD_NOT_ALLOWED, "Non sei loggato");
        } else {
            if (client.logout()) {
                return new Response(Response.Status.OK, "Logout effettuato con successo");
            } else {
                return new Response(Response.Status.INTERNAL_SERVER_ERROR, "");
            }
        }
    }

    private static Response loginRequestHandler(Request req, String connID, Client client) {
        if (!client.isRegistered()) {// se l'utente non è registrato
            return new Response(Response.Status.NOT_FOUND, ": sei non registrato");

        } else if (client.isLogged()) {// se l'utente è già loggato
            return new Response(Response.Status.METHOD_NOT_ALLOWED, ": sei già loggato");

            // se l'utente è già loggato con un altro client
        } else if (Player.loggedPlayer.containsKey(client.getUsername())) {
            return new Response(Response.Status.CONFLICT, ": l'clientname risulta già loggato con un altro client");
        } else {// se si deve loggare
            String opResult = client.login();
            if (opResult.equals("OK")) {
                return new Response(Response.Status.OK, ": ora sei loggato");
            } else if (opResult.equals("WRONG_CREDENTIALS")) {
                return new Response(Response.Status.UNAUTHORIZED, ": credenziali errate");
            } else {
                return new Response(Response.Status.INTERNAL_SERVER_ERROR, "");
            }
        }
    }

    private static Response registerRequesthandler(Request req, String connID, Client client) {
        try {

            // controllo se nella cartella resources/clients essite il file con nome
            // <clientname>.json
            // restituisco errore
            // se non esiste, creo il file e lo popolo con i dati dell'utente
            if (client.isLogged()) {
                return new Response(Response.Status.METHOD_NOT_ALLOWED, "sei già loggato");
            }
            if (client.isRegistered()) {
                return new Response(Response.Status.CONFLICT, "utente già registrato");
            } else {
                if (client.register()) {
                    return new Response(Response.Status.OK, "ora sei registrato");
                } else {
                    return new Response(Response.Status.INTERNAL_SERVER_ERROR, "");
                }
            }
        } catch (Exception e) {
            System.out.println("[registerRequestHandler] Errore durante la registrazione");
            e.printStackTrace();
            return new Response(Response.Status.INTERNAL_SERVER_ERROR, "");
        }
    }

    private static Response playWordleHandler(Request req, String connID, Client client) {
        if (!client.isLogged()) {
            return new Response(Response.Status.UNAUTHORIZED, "Non sei loggato");
        } else if (client.isPlaying()) {
            return new Response(Response.Status.METHOD_NOT_ALLOWED, "La partita è già iniziata");
        } else {
            JsonObject message = client.playWordle();
            return new Response(Response.Status.OK, message);
        }
    }

    private static Response sendWordHandler(Request req, String connID, Client client) {
        if (!client.isLogged()) {
            return new Response(Response.Status.UNAUTHORIZED, "Non sei loggato");
        } else if (!client.isPlaying()) {
            return new Response(Response.Status.METHOD_NOT_ALLOWED, "Devi prima iniziare una partita");
        } else {
            try {
                return new Response(Response.Status.OK, client.sendWord(req.getGuessedWord()));
            } catch (Exception e) {
                e.printStackTrace();
                return new Response(Response.Status.INTERNAL_SERVER_ERROR, "");
            }

        }
    }

    // sendMeStatisticsHandler
    private static Response sendMeStatisticsHandler(Request req, String connID, Client client) {
        if (!client.isLogged()) {
            return new Response(Response.Status.UNAUTHORIZED, "Non sei loggato");
        } else {
            JsonElement message = client.sendMeStatistics();
            if (message != null) {
                return new Response(Response.Status.OK, client.sendMeStatistics());
            } else {
                return new Response(Response.Status.INTERNAL_SERVER_ERROR, "");
            }
        }
    }

    private static Response shareHandler(Request req, String connID, Client client) {
        if (!client.isLogged()) {
            return new Response(Response.Status.UNAUTHORIZED, "Non sei loggato");
        } else {
            if (client.share()) {
                return new Response(Response.Status.OK, "");
            } else {
                return new Response(Response.Status.INTERNAL_SERVER_ERROR, "");
            }
        }
    }
}

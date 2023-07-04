import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Input {
    private BufferedReader reader;

    public Input() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public int readInt(int maxInput) {
        int opInt = -1;
        boolean ok = false;
        while (!ok) {
            try {
                String op = this.reader.readLine();
                try {
                    opInt = Integer.parseInt(op);
                    // controllo se l'operazione è nel range atteso
                    if (opInt < 0 || opInt > maxInput) {
                        System.out.println("L'operazione scelta non è valida, riprova...");
                    } else {
                        ok = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("L'input inserito non è un numero, riprova...");
                    // e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("Errore nella lettura dell'input");
                e.printStackTrace();
            }
        }
        return opInt;
    }

    public String readString() {
        String input = "";
        boolean ok = false;
        while (!ok) {
            try {
                input = this.reader.readLine();
                if (input.length() == 0) {
                    System.out.println("L'input inserito non è valido, riprova...");
                }
                ok = true;
            } catch (IOException e) {
                System.out.println("Errore nella lettura dell'input");
                e.printStackTrace();
                return "";
            }
        }
        return input;
    }

    public String readString(int length) {
        String input = "";
        boolean ok = false;
        while (!ok) {
            try {
                input = this.reader.readLine();
                if (input.equals("exit")) {
                    return "exit";
                } else if (input.length() != length) {
                    System.out.println("L'input deve essere lungo " + length + " caratteri, riprova...");
                }
                ok = true;
            } catch (IOException e) {
                System.out.println("Errore nella lettura dell'input");
                e.printStackTrace();
                return "";
            }
        }
        return input;
    }

    public String[] readCredentials() {
        String[] credentials = new String[2];

        System.out.print("Inserisci il tuo username: ");
        credentials[0] = readString();
        if (credentials[0].equals("exit")) {
            return new String[] { "exit" };
        }

        // TODO: oscurare la password sostiutendola con asterischi
        System.out.print("Inserisci la tua password: ");
        credentials[1] = readString();
        if (credentials[1].equals("exit")) {
            return new String[] { "exit" };
        }

        return credentials;
    }
}

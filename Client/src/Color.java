public class Color {
    private static String ANSI_RESET = "\u001B[0m";
    private static String ANSI_GREEN_BCK = "\u001B[42m";
    private static String ANSI_RED_BCK = "\u001B[41m";
    private static String ANSI_YELLOW_BCK = "\u001B[43m";
    // colori non fluorescenti
    private static String ANSI_GREEN = "\u001B[32m";
    private static String ANSI_RED = "\u001B[31m";
    private static String ANSI_YELLOW = "\u001B[33m";

    public static String SecretWordPattern = "GGGGGGGGGG";

    private static String green(char c) {
        return ANSI_GREEN + c + ANSI_RESET;
    }

    private static String yellow(char c) {
        return ANSI_YELLOW + c + ANSI_RESET;
    }

    public static String greenBackground(String c) {
        return ANSI_GREEN_BCK + c + ANSI_RESET;
    }

    public static String redBackground(String s) {
        return ANSI_RED_BCK + s + ANSI_RESET;
    }

    public static String yellowBackground(String s) {
        return ANSI_YELLOW_BCK + s + ANSI_RESET;
    }

    public static String green(Object s) {
        return ANSI_GREEN + s + ANSI_RESET;
    }

    public static String yellow(Object s) {
        return ANSI_YELLOW + s + ANSI_RESET;
    }

    public static String red(Object s) {
        return ANSI_RED + s + ANSI_RESET;
    }

    // data la guessWord e il pattern matching, ritorna la guessWord colorata
    public static String colorGuessedWord(String guessedWord, String patterMatching) {
        if (patterMatching.equals(SecretWordPattern)) {
            return greenBackground(guessedWord);
        }
        // System.out.println("patterMatching: " + patterMatching);
        String coloredWord = "";
        for (int i = 0; i < guessedWord.length(); i++) {
            if (patterMatching.charAt(i) == ('G')) {
                coloredWord += Color.green(guessedWord.charAt(i));

            } else if (patterMatching.charAt(i) == 'Y') {
                coloredWord += Color.yellow(guessedWord.charAt(i));
            } else {
                coloredWord += guessedWord.charAt(i);
            }
        }
        return coloredWord;
    }
}

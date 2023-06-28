public class Color {
    private static String ANSI_RESET = "\u001B[0m";
    private static String ANSI_GREEN_BCK = "\u001B[42m";
    private static String ANSI_RED_BCK = "\u001B[41m";
    private static String ANSI_YELLOW_BCK = "\u001B[43m";
    // colori non fluorescenti
    private static String ANSI_GREEN = "\u001B[32m";
    private static String ANSI_RED = "\u001B[31m";
    private static String ANSI_YELLOW = "\u001B[33m";

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

    public static String yellow(String s) {
        return ANSI_YELLOW + s + ANSI_RESET;
    }

    public static String red(String s) {
        return ANSI_RED + s + ANSI_RESET;
    }

}

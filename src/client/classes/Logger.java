package client.classes;

public class Logger {

    private static boolean isEnabled = true;

    //Singleton class
    private Logger() {
    }

    public static void enableLogging() {
        isEnabled = true;
    }

    public static void disableLogging() {
        isEnabled = false;
    }

    public static void print(String s) {
        if (isEnabled)
            System.out.print(s);
    }

    public static void println(String s) {
        if (isEnabled)
            System.out.println(s);
    }

    public static void error(String s) {
        if (isEnabled)
            System.out.println("**** " + s + " ****");
    }
}

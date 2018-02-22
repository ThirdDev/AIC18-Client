package client.classes;

public class Logger {

    //Singleton class
    private Logger() {
    }

    public static void print(String s) {
        System.out.print(s);
    }

    public static void println(String s) {
        System.out.println(s);
    }

    public static void error(String s) { System.out.println("**** " + s + " ****"); }
}

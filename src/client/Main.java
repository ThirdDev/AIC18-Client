package client;

import common.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Initial point of execution.
 * Do not change this class.
 */
public class Main {

    private static final boolean GLOBAL_VERBOSE_FLAG = false;

    private static final String[] argNames = {"AICHostIP", "AICHostPort", "AICToken", "AICRetryDelay", "ClientMode", "GeneFile"};
    private static final String[] argDefaults = {"localhost", "7099", "00000000000000000000000000000000", "1000", "damn", "damn.txt"};

    public static void main(String[] args) {
        try {
            run(getArgs(args));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void run(String[] args) throws FileNotFoundException {

        if (args[args.length - 2].equals("damn")) {
            System.out.println("ClientMode?");

            Scanner scanner = new Scanner(System.in);
            String type = scanner.nextLine();

            args[args.length - 2] = type;
        }

        if(GLOBAL_VERBOSE_FLAG || Arrays.asList(args).contains("--verbose")) {
            Log.DEV_MODE = true;
            Log.LOG_LEVEL = Log.VERBOSE;

            Log.outputFile = new FileOutputStream("client.log", false);
        }

        try {
            Controller c = new Controller(args[0], Integer.parseInt(args[1]), args[2], Long.parseLong(args[3]));
            c.start(args[4], args[5]);
        } catch (Exception e) {
            System.err.println("Client terminated with error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String[] getArgs(String[] inArgs) {
        String[] args = new String[argNames.length];
        for (int i = 0; i < argNames.length; i++) {
            //args[i] = System.getenv(argNames[i]);
            //if (args[i] == null)
            if (i < inArgs.length)
                args[i] = inArgs[i];
            else
                args[i] = argDefaults[i];
            Log.i("PARAM", argNames[i] + "=" + args[i]);
        }
        return args;
    }

}
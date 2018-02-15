package client;

import client.classes.*;
import client.classes.exceptions.*;
import client.model.*;

public class BankController {

    public static final String BANK_ACCOUNT_DEFENCE = "Defence";
    public static final String BANK_ACCOUNT_ATTACK = "Attack";

    private static int lastTurnMoney = 0;

    private BankController() { } //Static class

    public static void handleMoney(Player player) {
        if (!Bank.IsInitialized())
            initBank();

        int income = player.getMoney() - lastTurnMoney;
        Bank.income(income);
        lastTurnMoney = player.getMoney();
    }

    public static void initBank() {
        try {
            System.out.println("Initializing bank...");
            Bank.registerAccount(BANK_ACCOUNT_ATTACK, 0.5);
            Bank.registerAccount(BANK_ACCOUNT_DEFENCE, 0.5);
        }
        catch (TotalPercentageExceededException e) {
            System.out.println("Something went terribly wrong in bank initialization !!!");
        }
    }
}

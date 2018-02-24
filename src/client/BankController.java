package client;

import client.classes.*;
import client.classes.exceptions.*;
import client.model.*;

public class BankController {

    public static final String BANK_ACCOUNT_DEFENCE = "Defence";
    public static final String BANK_ACCOUNT_ATTACK = "Attack";

    private static int lastTurnMoney = 0;

    private BankController() {
    } //Static class

    public static void handleMoney(Player player) {
        if (!Bank.isInitialized())
            initBank();

        int income = player.getMoney() - lastTurnMoney;

        if (income < 0)
            Logger.error("!!! THEFT ALERT !!! Haji poola ro bordan!!!");

        Bank.income(income);
        lastTurnMoney = player.getMoney();
    }

    public static void spentMoney(int amount) {
        lastTurnMoney -= amount;
    }

    public static void initBank() {
        System.out.println("Initializing bank...");
        Bank.registerAccount(BANK_ACCOUNT_ATTACK, 0.6);
        Bank.registerAccount(BANK_ACCOUNT_DEFENCE, 0.4);
    }

    @SuppressWarnings("Duplicates")
    public static void setAttackPercentage(double percentage) throws AccountNotFoundException, InvalidArgumentException {
        BankAccount attackAccout = Bank.getAccount(BANK_ACCOUNT_ATTACK);
        BankAccount defendAccount = Bank.getAccount(BANK_ACCOUNT_DEFENCE);
        double previousAttackPercentage = attackAccout.getPercent();
        double delta = percentage - previousAttackPercentage;
        Bank.changeDistributionPercentage(attackAccout, defendAccount, delta);
    }

    @SuppressWarnings("Duplicates")
    public static void setDefendPercentage(double percentage) throws AccountNotFoundException, InvalidArgumentException {
        BankAccount attackAccout = Bank.getAccount(BANK_ACCOUNT_ATTACK);
        BankAccount defendAccount = Bank.getAccount(BANK_ACCOUNT_DEFENCE);
        double previousDefendPercentage = defendAccount.getPercent();
        double delta = percentage - previousDefendPercentage;
        Bank.changeDistributionPercentage(defendAccount, attackAccout, delta);
    }
}

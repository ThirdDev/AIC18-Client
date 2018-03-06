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

    private static int theftAmount = 0;

    public static void handleMoney(Player player) {
        if (!Bank.isInitialized())
            initBank();

        int income = player.getMoney() - lastTurnMoney;

        if (income < 0) {
            Logger.error("!!! THEFT ALERT !!! Haji poola ro bordan!!!");
            theftAmount += income;
            Logger.error("current theft amount: " + income + ", total theft amount: " + theftAmount);
        }

        Bank.income(income);
        lastTurnMoney = player.getMoney();
    }

    public static void spentMoney(int amount) {
        lastTurnMoney -= amount;
    }

    public static void initBank() {
        Logger.println("Initializing bank...");
        Bank.registerAccount(BANK_ACCOUNT_ATTACK, 0.9);
        Bank.registerAccount(BANK_ACCOUNT_DEFENCE, 0.1);
    }

    @SuppressWarnings("Duplicates")
    public static void setAttackPercentage(double percentage) {
        BankAccount attackAccout = Bank.getAccount(BANK_ACCOUNT_ATTACK);
        BankAccount defendAccount = Bank.getAccount(BANK_ACCOUNT_DEFENCE);
        double previousAttackPercentage = attackAccout.getPercent();
        double delta = percentage - previousAttackPercentage;
        Bank.changeDistributionPercentage(attackAccout, defendAccount, delta);
    }

    @SuppressWarnings("Duplicates")
    public static void setDefendPercentage(double percentage) {
        BankAccount attackAccout = Bank.getAccount(BANK_ACCOUNT_ATTACK);
        BankAccount defendAccount = Bank.getAccount(BANK_ACCOUNT_DEFENCE);
        double previousDefendPercentage = defendAccount.getPercent();
        double delta = percentage - previousDefendPercentage;
        Bank.changeDistributionPercentage(defendAccount, attackAccout, delta);
    }

    public static void setAccountsPercentage(double attackAccountPercentage, double defendAccountPercentage) {
        if (attackAccountPercentage + defendAccountPercentage != 1) {
            Logger.error("Lanati poola ro dorost taghsim kon!!");
        }
        setAttackPercentage(attackAccountPercentage);
        setDefendPercentage(defendAccountPercentage);
    }

    private static int budgetChangePhase = -1;

    public static void updateBudgetDistribution(World game) {
        if (game.getCurrentTurn() > 8 && budgetChangePhase == -1) {
            setAccountsPercentage(0.4, 0.6);
            budgetChangePhase = 0;
        }
        if (game.getCurrentTurn() > 100 && budgetChangePhase == 0) {
            setAccountsPercentage(0.5, 0.5);
            budgetChangePhase = 1;
        }
        if (game.getCurrentTurn() > 400 && budgetChangePhase == 1) {
            setAccountsPercentage(0.35, 0.65);
            budgetChangePhase = 2;
        }
        if (game.getCurrentTurn() > 600 && budgetChangePhase == 2) {
            setAccountsPercentage(0.2, 0.8);
            budgetChangePhase = 3;
        }
    }

}

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
        Logger.println("Initializing bank...");
        Bank.registerAccount(BANK_ACCOUNT_ATTACK, 0.9);
        Bank.registerAccount(BANK_ACCOUNT_DEFENCE, 0.1);
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

    private static int budgetChangePhase = -1;

    public static void updateBudgetDistribution(World game) {
        if (game.getCurrentTurn() > 8 && budgetChangePhase == -1) {
            Bank.changeDistributionPercentage(Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE),
                    Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK), 0.5);
            budgetChangePhase = 0;
        }
        if (game.getCurrentTurn() > 100 && budgetChangePhase == 0) {
            Bank.changeDistributionPercentage(Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK),
                    Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE), 0.1);
            budgetChangePhase = 1;
        }
        if (game.getCurrentTurn() > 400 && budgetChangePhase == 1) {
            Bank.changeDistributionPercentage(Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK),
                    Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE), 0.15);
            budgetChangePhase = 2;
        }
        if (game.getCurrentTurn() > 600 && budgetChangePhase == 2) {
            Bank.changeDistributionPercentage(Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK),
                    Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE), 0.15);
            budgetChangePhase = 3;
        }
    }

}

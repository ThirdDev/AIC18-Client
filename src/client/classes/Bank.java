package client.classes;

import client.classes.exceptions.*;

import java.util.*;

public class Bank {

    private static Map<String, BankAccount> accounts;
    private static double totalPercentage;
    private static int floatingMoney;

    static {
        accounts = new HashMap<String, BankAccount>();
        totalPercentage = 0.0;
        floatingMoney = 0;
    }

    private Bank() {
    } //Static class

    public static BankAccount registerAccount(String nickname, double percent) {
        BankAccount ba = new BankAccount(nickname, percent);
        accounts.put(nickname, ba);
        totalPercentage += percent;
        return ba;
    }

    public static BankAccount getAccount(String nickname) {
        if (!accounts.containsKey(nickname))
            return null;

        return accounts.get(nickname);
    }

    public static boolean changeDistributionPercentage(BankAccount accountToIncrease,
                                                    BankAccount accountToDecrease,
                                                    double percentage) {

        double accountToIncreaseNewPercentage = accountToIncrease.getPercent() + percentage;
        double accountToDecreaseNewPercentage = accountToDecrease.getPercent() - percentage;

        if (percentage < 0 || percentage > 1)
            return false;
        if (accountToIncreaseNewPercentage > 1)
            return false;
        if (accountToDecreaseNewPercentage < 0)
            return false;

        accountToIncrease.setPercent(accountToIncreaseNewPercentage);
        accountToDecrease.setPercent(accountToDecreaseNewPercentage);
        return true;
    }

    public static boolean isInitialized() {
        return (totalPercentage > 0);
    }

    public static boolean transferMoney(BankAccount sender, BankAccount receiver, int amount) {
        if (sender.retrieveMoney(amount) == false)
            return false;
        receiver.increaseBalance(amount);

        return true;
    }

    public static void income(int amount) {
        //Logger.println("Earned " + amount + " this turn!");

        floatingMoney += amount;
        distribute();
        checkGoals();
    }

    private static void distribute() {
        int totalDistributedAmount = 0;
        for (BankAccount ba : accounts.values()) {
            int amount = (int) Math.floor(ba.getPercent() * floatingMoney);
            ba.increaseBalance(amount);

            totalDistributedAmount += amount;
            //Logger.println("About " + amount + " goes to " + ba.getNickname());
        }

        floatingMoney -= totalDistributedAmount;
    }

    public static void checkGoals() {
        for (BankAccount b1 : accounts.values()) {
            for (BankAccount b2 : accounts.values()) {
                if (b1.getNickname().equals(b2.getNickname()))
                    continue;

                int g1 = b1.getGoal();
                int g2 = b2.getGoal();

                if (g1 > 0 && g2 < 0) {
                    int amount = Math.min(g1, -g2);

                    if (b2.isGoalExact() && amount != -g2)
                        continue;

                    if (transferMoney(b1, b2, amount)) {
                        b1.setGoal(g1 - amount);
                        b2.setGoal(g2 + amount);

                        Logger.println("Transferred donation " + amount + " from " + b1.getNickname() + " to " + b2.getNickname() + ".");
                    }
                }
            }
        }
    }
}

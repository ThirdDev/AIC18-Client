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

    public static BankAccount registerAccount(String nickname, double percent) throws TotalPercentageExceededException {
        if ((totalPercentage + percent) > 1.0)
            throw new TotalPercentageExceededException();

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

    public static boolean IsInitialized() {
        return (totalPercentage == 1.0);
    }

    public static boolean TransferMoney(BankAccount sender, BankAccount receiver, int amount) {
        if (sender.retrieveMoney(amount) == false)
            return false;
        receiver.increaseBalance(amount);

        return true;
    }

    public static void income(int amount) {
        floatingMoney += amount;
        distribute();
    }

    private static void distribute() {
        for (BankAccount ba : accounts.values()) {
            int amount = (int) Math.floor(ba.getPercent() * floatingMoney);
            ba.increaseBalance(amount);
            floatingMoney -= amount;
        }
    }
}

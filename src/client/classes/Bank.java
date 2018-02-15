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

    private Bank() {} //Static class

    public static BankAccount registerAccount(String nickname, double percent) throws TotalPercentageExceededException {
        if ((totalPercentage + percent) > 1.0)
            throw new TotalPercentageExceededException();

        BankAccount ba = new BankAccount(nickname, percent);
        accounts.put(nickname, ba);
        return ba;
    }

    public static BankAccount getAccount(String nickname) throws AccountNotFoundException {
        if (!accounts.containsKey(nickname))
            throw new AccountNotFoundException();

        return accounts.get(nickname);
    }

    public static void changeDistributionPercentage(BankAccount accountToIncrease,
                                                    BankAccount accountToDecrease,
                                                    double percentage) throws InvalidArgumentException {

        double accountToIncreaseNewPercentage = accountToIncrease.getPercent() + percentage;
        double accountToDecreaseNewPercentage = accountToDecrease.getPercent() - percentage;

        if (percentage < 0 || percentage > 1)
            throw new InvalidArgumentException();
        if (accountToIncreaseNewPercentage > 1)
            throw new InvalidArgumentException();
        if (accountToDecreaseNewPercentage < 0)
            throw new InvalidArgumentException();

        accountToIncrease.setPercent(accountToIncreaseNewPercentage);
        accountToDecrease.setPercent(accountToDecreaseNewPercentage);
    }

    public static void income(int amount) {
        floatingMoney += amount;
        distribute();
    }

    private static void distribute() {
        for (BankAccount ba : accounts.values()) {
           int amount = (int)Math.floor(ba.getPercent() * floatingMoney);
           ba.increaseBalance(amount);
           floatingMoney -= amount;
        }
    }
}

package client.classes;

import client.BankController;

public class BankAccount {
    String nickname;
    double percent;
    int balance;

    public BankAccount(String nickname, double percent) {
        this.nickname = nickname;
        this.percent = percent;
        balance = 0;
    }

    public String getNickname() {
        return nickname;
    }

    public double getPercent() {
        return percent;
    }

    public int getBalance() {
        return balance;
    }

    protected void increaseBalance(int amount) {
        balance += amount;
    }

    protected void setPercent(double value) {
        percent = value;
    }

    public boolean canSpend(int amount) {
        return ((balance - amount) >= 0);
    }

    public boolean retrieveMoney(int amount) {
        if (balance - amount < 0)
            return false;

        balance -= amount;
        BankController.spentMoney(amount);
        return true;
    }

}

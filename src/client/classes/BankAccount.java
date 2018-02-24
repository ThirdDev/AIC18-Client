package client.classes;

import client.BankController;

public class BankAccount {
    String nickname;
    double percent;
    int balance;
    int goal;
    boolean isGoalExact;

    public BankAccount(String nickname, double percent) {
        this.nickname = nickname;
        this.percent = percent;
        balance = 0;
        goal = 0;
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

    protected synchronized void increaseBalance(int amount) {
        balance += amount;
    }

    protected void setPercent(double value) {
        percent = value;
    }

    public boolean canSpend(int amount) {
        return ((balance - amount) >= 0);
    }

    public void setGoal(int amount) {
        goal = amount;
    }

    protected int getGoal() {
        return goal;
    }

    public void setIsGoalExact(boolean isExact) {
        isGoalExact = isExact;
    }

    protected boolean isGoalExact() {
        return isGoalExact;
    }

    public synchronized boolean retrieveMoney(int amount) {
        if (balance - amount < 0)
            return false;

        balance -= amount;
        BankController.spentMoney(amount);
        return true;
    }

}

package client.ahmadalli.utils;

import client.classes.BankAccount;
import client.model.ArcherTower;
import client.model.CannonTower;

public class Finance {
    public static boolean canCreateBasicTower(BankAccount defenceBankAccount) {
        return defenceBankAccount.canSpend(ArcherTower.INITIAL_PRICE) ||
                defenceBankAccount.canSpend(CannonTower.INITIAL_PRICE);
    }
}

package client;

import client.ahmadalli.Common;
import client.ahmadalli.defend.StormCreation;
import client.ahmadalli.defend.TowerCreation;
import client.classes.Bank;
import client.classes.Logger;
import client.classes.genes.GeneCollections;
import client.model.*;

import java.util.*;

/**
 * AI class.
 * You should fill body of the method {@link }.
 * Do not change name or modifiers of the methods or fields
 * and do not add constructor for this class.
 * You can add as many methods or fields as you want!
 * Use world parameter to access and modify game's
 * world!
 * See World interface for more details.
 */
public class AI {

    Defence defence;

    public AI() {
        // Logger.disableLogging();
        GeneCollections.getCollections();
    }

    void simpleTurn(World game) {
        commonTurnFunctions(game);
    }

    void complexTurn(World game) {
        commonTurnFunctions(game);
    }

    //This function will be called on both simple and complex turns
    private void commonTurnFunctions(World game) {
        if (defence == null) defence = new Defence(
                Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE),
                game
        );
        Logger.println("Turn " + game.getCurrentTurn());
        BankController.handleMoney(game.getMyInformation());
        Common.initialize(game);

        Logger.print("Attack budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getBalance());
        Logger.print(", Defence budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE).getBalance());
        Logger.println(", Total: " + game.getMyInformation().getMoney());
        Logger.println(Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getPercent() + ", " + Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE).getPercent());

        TowerCreation.simpleTowerCreation(game);
        StormCreation.stormIfNecessary(game);

        Attack.Attack(game);

        BankController.updateBudgetDistribution(game);
        MorningBeams(game);
    }

    //int beansCount = 0;
    private void MorningBeams(World game) {
        if (game.getMyInformation().getBeansLeft() > 0) {
            for (Tower t : game.getVisibleEnemyTowers()) {
                game.plantBean(t.getLocation().getX(), t.getLocation().getY());
                //beansCount++;
            }
        }
    }
}

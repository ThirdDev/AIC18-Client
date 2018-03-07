package client;

import client.ahmadalli.Common;
import client.ahmadalli.defend.StormCreation;
import client.ahmadalli.defend.TowerCreation;
import client.classes.Bank;
import client.classes.Logger;
import client.classes.genes.GeneCollections;
import client.model.*;
import common.util.Log;

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
        commonTurnFunctions(game, false);
    }

    void complexTurn(World game) {
        commonTurnFunctions(game, true);
    }

    //This function will be called on both simple and complex turns
    private void commonTurnFunctions(World game, boolean isHeavyTurn) {
        Logger.println("Turn " + game.getCurrentTurn());

        long turnInitMilliseconds = System.currentTimeMillis();
        long taskInitMilliseconds = System.currentTimeMillis();

        if (defence == null) {
            taskInitMilliseconds = System.currentTimeMillis();
            defence = new Defence(
                    Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE),
                    game
            );
            logTaskTime("amirhosein's defence initialization", taskInitMilliseconds, turnInitMilliseconds);
        }

        taskInitMilliseconds = System.currentTimeMillis();
        BankController.handleMoney(game.getMyInformation());
        logTaskTime("handling money", taskInitMilliseconds, turnInitMilliseconds);

        taskInitMilliseconds = System.currentTimeMillis();
        Common.initialize(game);
        logTaskTime("ahmadalli's initialize", taskInitMilliseconds, turnInitMilliseconds);

        Logger.print("Attack budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getBalance());
        Logger.print(", Defence budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE).getBalance());
        Logger.println(", Total: " + game.getMyInformation().getMoney());
        Logger.println(Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getPercent() + ", " + Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE).getPercent());

        taskInitMilliseconds = System.currentTimeMillis();
        defence.run(game);
        logTaskTime("amirhosein's defence", taskInitMilliseconds, turnInitMilliseconds);

        // taskInitMilliseconds = System.currentTimeMillis();
        // TowerCreation.simpleTowerCreation(game);
        // logTaskTime("ahmadalli's defence", taskInitMilliseconds, turnInitMilliseconds);

        taskInitMilliseconds = System.currentTimeMillis();
        StormCreation.stormIfNecessary(game);
        logTaskTime("ahmadalli's storm", taskInitMilliseconds, turnInitMilliseconds);

        taskInitMilliseconds = System.currentTimeMillis();
        Attack.Attack(game, isHeavyTurn);
        logTaskTime("attack", taskInitMilliseconds, turnInitMilliseconds);

        taskInitMilliseconds = System.currentTimeMillis();
        BankController.updateBudgetDistribution(game);
        logTaskTime("updating bank's budget distribution", taskInitMilliseconds, turnInitMilliseconds);

        taskInitMilliseconds = System.currentTimeMillis();
        MorningBeams(game);
        logTaskTime("morning beans", taskInitMilliseconds, turnInitMilliseconds);

        long totalPassed = (System.currentTimeMillis() - turnInitMilliseconds);
        Logger.println("turn passed in " + totalPassed + "ms");

        if ((game.getCurrentTurn() % 10 != 0 && totalPassed > 200) || (game.getCurrentTurn() % 10 == 0 && totalPassed > 1000)) {
            Logger.error("golam leftesh dadi server bikhial shold. bi haya!");
        }

        Logger.println("===== end of turn " + game.getCurrentTurn() + "=====");
        Logger.println("");
    }

    private void logTaskTime(String taskName, long taskInitMilliseconds, long turnInitMilliseconds) {
        Logger.println(">>>>> " + taskName + " took " + (System.currentTimeMillis() - taskInitMilliseconds) + "ms" +
                " and passed " + (System.currentTimeMillis() - turnInitMilliseconds) + "ms in turn <<<<<");
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

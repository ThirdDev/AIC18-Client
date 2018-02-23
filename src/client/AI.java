package client;

import client.classes.AttackMapAnalyser;
import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.classes.exceptions.AccountNotFoundException;
import client.classes.exceptions.NotEnoughMoneyException;
import client.classes.genes.GeneCollections;
import client.classes.genes.Recipe;
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

    Random rnd = new Random();

    public AI() {
        GeneCollections.getCollections();
    }

    void simpleTurn(World game) {
        commonTurnFunctions(game);

        /*
        Log.d(TAG,"lightTurn Called"+" Turn:"+game.getCurrentTurn());

        int t=rnd.nextInt();
        if(t%3==2){
            game.createArcherTower(rnd.nextInt(4),rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));

        }else if(t%3==1){
            game.createHeavyUnit(rnd.nextInt(game.getDefenceMapPaths().size()));

        }else if(t%3==0){
            game.createLightUnit(rnd.nextInt(game.getAttackMapPaths().size()));
        }
        */
    }

    void complexTurn(World game) {
        commonTurnFunctions(game);

        /*
        Log.d(TAG,"HeavyTurn Called"+" Turn:"+game.getCurrentTurn());

        int t=rnd.nextInt();
        if(t%3==2){
            game.createStorm(rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));
        }else if(t%3==1){
            game.plantBean(rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));

        }else if(t%3==0){
            game.createCannonTower(rnd.nextInt(4),rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));

        }
        */
    }

    private void InsertPathTower(World game, HashMap<Point, List<Path>> cellPaths, HashMap<Path, List<Tower>> pathTowers, Tower t, int x, int y) {
        if (!Util.inRange(x, y, game.getAttackMap()))
            return;

        Point point = new Point(x, y);
        if (cellPaths.containsKey(point)) {
            for (Path p : cellPaths.get(point)) {
                if (!pathTowers.containsKey(p))
                    pathTowers.put(p, new ArrayList<>());

                pathTowers.get(p).add(t);
            }
        }
    }

    //This function will be called on both simple and complex turns
    private void commonTurnFunctions(World game) {
        Logger.println("Turn " + game.getCurrentTurn());
        BankController.handleMoney(game.getMyInformation());

        Logger.print("Attack budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getBalance());
        Logger.print(", Defence budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE).getBalance());
        Logger.println(", Total: " + game.getMyInformation().getMoney());

        ahmadalli.plantRandomTowerInASidewayCell(game);

        Attack.Explore(game);
    }
}

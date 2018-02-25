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
    int budgetChangePhase = -1;
    Defence defence;

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
        if (defence == null) defence = new Defence(
                Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE),
                game
        );
        Logger.println("Turn " + game.getCurrentTurn());
        BankController.handleMoney(game.getMyInformation());
        ahmadalli.initialize(game);

        Logger.print("Attack budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getBalance());
        Logger.print(", Defence budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE).getBalance());
        Logger.println(", Total: " + game.getMyInformation().getMoney());
        Logger.println(Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getPercent() + ", " + Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE).getPercent());
        //defence.run(game);
        ahmadalli.simpleTowerCreation(game);

        ahmadalli.stormIfNecessary(game);

        Attack.Attack(game);

        UpdateBudgetDistribution(game);
        MorningBeams(game);
    }

    //int beansCount = 0;
    private void MorningBeams(World game) {
        for (Tower t : game.getVisibleEnemyTowers()) {
            game.plantBean(t.getLocation().getX(), t.getLocation().getY());
            //beansCount++;
        }
    }

    private void UpdateBudgetDistribution(World game) {
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
        if (game.getCurrentTurn() > 300 && budgetChangePhase == 1) {
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

    private void simpleStorm(World game) {
        for (Path path : game.getDefenceMapPaths()) {
            int damage = 0;
            for (int i = Math.max(path.getRoad().size() - 5, 0); i < path.getRoad().size(); i++) {
                for (Unit unit : path.getRoad().get(i).getUnits()) {
                    if (unit instanceof HeavyUnit)
                        damage += 5;
                    else
                        damage += 1;
                }
            }

            if (damage >= 10) {
                int index = path.getRoad().size() - 3;

                if (index < 0)
                    index = path.getRoad().size();

                Point p = path.getRoad().get(index).getLocation();
                game.createStorm(p.getX(), p.getY());

                Logger.println("Created storm at " + p.getX() + ", " + p.getY());
            }

        }
    }
}

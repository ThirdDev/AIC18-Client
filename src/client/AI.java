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

    int attackTurn = 0;
    HashMap<Path, Recipe> currentAttackRecipe = new HashMap<>();

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
        BankController.handleMoney(game.getMyInformation());

        ahmadalli.plantRandomTowerInASidewayCell(game);

        Attack(game);
    }

    private void Attack(World game) {
        if (attackTurn == 0) {
            Logger.println("Attack begin");

            for (Path path : game.getAttackMapPaths()) {
                Set<TowerDetails> enemyTowers = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, path);
                Recipe recipe = GeneCollections.getCollections().getRecipe(enemyTowers, path, GeneCollections.Strategy.Explore);
                currentAttackRecipe.put(path, recipe);

                Logger.println("For " + path.toString() + " : ");

                Logger.print("Cannons: ");
                for (byte b : recipe.getCreeps())
                    Logger.print(b + ", ");
                Logger.print("Archers: ");
                for (byte b : recipe.getHeros())
                    Logger.print(b + ", ");
            }


            Logger.println("");

            ProceedAttack(game);
        } else {
            ProceedAttack(game);
        }
    }

    private void ProceedAttack(World game) {
        boolean hasDoneAnything = false;

        for (Path path : currentAttackRecipe.keySet()) {
            Recipe recipe = currentAttackRecipe.get(path);

            if ((attackTurn >= recipe.getCreeps().length) && (attackTurn >= recipe.getHeros().length)) {
                continue;
            }

            hasDoneAnything = true;

            int creepCount = recipe.getCreeps()[attackTurn];
            int heroCount = recipe.getHeros()[attackTurn];

            int totalPrice = LightUnit.getCurrentPrice(creepCount) + HeavyUnit.getCurrentPrice(heroCount);

            try {
                if (Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getBalance() >= totalPrice) {
                    if (attackTurn < recipe.getCreeps().length) {
                        TryCreateSoldiers(game, path, UnitType.Creep, recipe.getCreeps()[attackTurn]);
                    }

                    if (attackTurn < recipe.getHeros().length) {
                        TryCreateSoldiers(game, path, UnitType.Hero, recipe.getHeros()[attackTurn]);
                    }

                    attackTurn++;
                }
            } catch (AccountNotFoundException e) {
                Logger.error("Something's very wrong in ProceedAttack!");
            }
        }

        if (!hasDoneAnything) {
            attackTurn = 0;
        }
    }

    private void TryCreateSoldiers(World game, Path path, UnitType type, int count) {
        try {
            BankAccount attackerAccount = Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK);

            int totalPrice;

            if (type == UnitType.Creep)
                totalPrice = LightUnit.getCurrentPrice(count);
            else
                totalPrice = HeavyUnit.getCurrentPrice(count);

            attackerAccount.retrieveMoney(totalPrice);

            int pathIndex = getPathIndex(game, path);

            if (type == UnitType.Creep) {
                game.createLightUnit(pathIndex);
                LightUnit.createdUnit();
            }
            else {
                game.createHeavyUnit(pathIndex);
                HeavyUnit.createdUnit();
            }

        } catch (AccountNotFoundException e) {
            Logger.error("Something's very wrong in TryCreateSoldiers!");
        } catch (NotEnoughMoneyException e) {
            Logger.error("Didn't have enough money to create " + count + " creeps. But why? we should've had enough...");
        }
    }

    private int getPathIndex(World game, Path path) {
        for (int i = 0; i < game.getAttackMapPaths().size(); i++)
            if (path.equals(game.getAttackMapPaths().get(i)))
                return i;

        Logger.error("getPathIndex fatal error");
        return -1;
    }

    public enum UnitType {
        Creep,
        Hero
    }
}

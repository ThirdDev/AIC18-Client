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

    HashMap<Path, Recipe> currentAttackRecipe = new HashMap<>();
    HashMap<Path, Integer> attackTurns = new HashMap<>();
    boolean allowedToInitiateAttack = true;

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

        ahmadalli.plantRandomTowerInASidewayCell(game);

        Attack(game);

        try {
            Logger.print("Attack budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).getBalance());
            Logger.print(", Defence budget: " + Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE).getBalance());
            Logger.println(", Total: " + game.getMyInformation().getMoney());

        } catch (Exception ex) {}
    }

    private void Attack(World game) {
        for (Path path : game.getAttackMapPaths()) {
            if (!attackTurns.containsKey(path))
                attackTurns.put(path, 0);

            if (attackTurns.get(path) == 0) {
                if (!allowedToInitiateAttack)
                    continue;
                Logger.println("Attack begin for " + path.toString());

                Set<TowerDetails> enemyTowers = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, path);
                Recipe recipe = GeneCollections.getCollections().getRecipe(enemyTowers, path, GeneCollections.Strategy.DamageFullForce);

                int totalCost = recipe.getTotalCost();
                if (!Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).canSpend(totalCost)) {
                    Logger.println("Not enough money. (we need " + totalCost + ")");
                    Logger.println("");
                    continue;
                }

                currentAttackRecipe.put(path, recipe);

                Logger.print("Creeps: ");
                for (byte b : recipe.getCreeps())
                    Logger.print(b + ", ");
                Logger.print("Heros: ");
                for (byte b : recipe.getHeros())
                    Logger.print(b + ", ");
                Logger.println("");

                ProceedAttack(game, path);

                allowedToInitiateAttack = false;
            } else if (attackTurns.get(path) > 0) {
                ProceedAttack(game, path);
            } else {
                attackTurns.put(path, attackTurns.get(path) + 1);
            }
        }
    }

    private void ProceedAttack(World game, Path path) {
        Recipe recipe = currentAttackRecipe.get(path);

        if ((attackTurns.get(path) >= recipe.getCreeps().length) && (attackTurns.get(path) >= recipe.getHeros().length)) {
            allowedToInitiateAttack = true;
            attackTurns.put(path, -5);
            Logger.println("Finished creating attack wave for " + path.toString());
            return;
        }

        if (attackTurns.get(path) < recipe.getCreeps().length) {
            TryCreateSoldiers(game, path, UnitType.Creep, recipe.getCreeps()[attackTurns.get(path)]);
        }

        if (attackTurns.get(path) < recipe.getHeros().length) {
            TryCreateSoldiers(game, path, UnitType.Hero, recipe.getHeros()[attackTurns.get(path)]);
        }

        attackTurns.put(path, attackTurns.get(path) + 1);
    }

    private void TryCreateSoldiers(World game, Path path, UnitType type, int count) {
        BankAccount attackerAccount = Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK);
        int totalPrice;

        if (type == UnitType.Creep)
            totalPrice = LightUnit.getCurrentPrice(count);
        else
            totalPrice = HeavyUnit.getCurrentPrice(count);

        if (attackerAccount.retrieveMoney(totalPrice) == false) {
            Logger.error("Didn't have enough money to create " + count + " creeps. But why? we should've had enough...");
            return;
        }

        int pathIndex = getPathIndex(game, path);

        if (type == UnitType.Creep) {
            for (int i = 0; i < count; i++) {
                game.createLightUnit(pathIndex);
                LightUnit.createdUnit();

                System.out.println("Created a creep.");
            }
        }
        else {
            for (int i = 0; i < count; i++) {
                game.createHeavyUnit(pathIndex);
                HeavyUnit.createdUnit();

                System.out.println("Created a hero.");
            }
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

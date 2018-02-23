package client;

import client.classes.AttackMapAnalyser;
import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.classes.genes.GeneCollections;
import client.classes.genes.Recipe;
import client.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Attack {
    static HashMap<Path, Recipe> currentAttackRecipe = new HashMap<>();
    static HashMap<Path, Integer> attackTurns = new HashMap<>();
    static HashMap<Path, Integer> exploreCount = new HashMap<>();
    static boolean allowedToInitiateAttack = true;


    public static void Explore(World game) {
        game.getAttackMapPaths().stream().sorted((x1, x2) -> Integer.compare(getExploreCount(x1), getExploreCount(x2))).forEach(path -> {
            if (!attackTurns.containsKey(path))
                attackTurns.put(path, 0);

            if (attackTurns.get(path) == 0) {
                if (allowedToInitiateAttack) {
                    Logger.println("Explore begin for " + path.toString());

                    Set<TowerDetails> enemyTowers = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, path);
                    Recipe recipe = GeneCollections.getCollections().getRecipe(enemyTowers, path, GeneCollections.Strategy.Explore);

                    int totalCost = recipe.getTotalCost();
                    if (!Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).canSpend(totalCost)) {
                        Logger.println("Not enough money. (we need " + totalCost + ")");
                        Logger.println("");
                    } else {
                        currentAttackRecipe.put(path, recipe);

                        Logger.print("Creeps: ");
                        for (byte b : recipe.getCreeps())
                            Logger.print(b + ", ");
                        Logger.print("Heros: ");
                        for (byte b : recipe.getHeros())
                            Logger.print(b + ", ");
                        Logger.println("");

                        IncrementExploreCount(path);
                        ProceedAttack(game, path);

                        allowedToInitiateAttack = false;
                    }
                }
            } else if (attackTurns.get(path) > 0) {
                ProceedAttack(game, path);
            } else {
                attackTurns.put(path, attackTurns.get(path) + 1);
            }
        });
    }

    private static int getExploreCount(Path p) {
        if (!exploreCount.containsKey(p))
            return 0;

        return exploreCount.get(p);
    }

    private static void IncrementExploreCount(Path path) {
        if (exploreCount.containsKey(path))
            exploreCount.put(path, exploreCount.get(path) + 1);
        else
            exploreCount.put(path, 1);
    }

    private static void ProceedAttack(World game, Path path) {
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

    private static void TryCreateSoldiers(World game, Path path, UnitType type, int count) {
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

                Logger.println("Created a creep.");
            }
        }
        else {
            for (int i = 0; i < count; i++) {
                game.createHeavyUnit(pathIndex);
                HeavyUnit.createdUnit();

                Logger.println("Created a hero.");
            }
        }
    }

    private static int getPathIndex(World game, Path path) {
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

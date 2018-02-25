package client;

import client.classes.AttackMapAnalyser;
import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.classes.genes.GeneCollections;
import client.classes.genes.Recipe;
import client.model.*;

import java.util.*;

public class Attack {
    static HashMap<Path, Recipe> currentAttackRecipe = new HashMap<>();
    static HashMap<Path, Integer> attackTurns = new HashMap<>();
    static HashMap<Path, Integer> exploreCount = new HashMap<>();
    static boolean allowedToInitiateExplore = true;
    static boolean allowedToInitiateDamage = false;
    static boolean damageInProgress = false;
    static boolean isInitialized = false;
    static int turnsBetweenExplores = 20;

    static long totalMoneySpentOnExplore = 0;
    static long totalExploreTimes = 0;

    public static void Attack(World game) {
        if (game.getCurrentTurn() < 8 ) {
            DawnAttack(game);
        }
        Prepare(game);
        ScheduledOperations(game);
    }

    private static void DawnAttack(World game) {
        if (game.getCurrentTurn() % 2 == 0)
            return;

        for (Path path : game.getAttackMapPaths()) {
            TryCreateSoldiers(game, path, UnitType.Creep, 3);
        }
    }

    private static void Prepare(World game) {
        if (!isInitialized) {
            //First cycle init

            isInitialized = true;
        }

        if (game.getCurrentTurn() > 75) {
            turnsBetweenExplores = 60;
            allowedToInitiateDamage = true;
            //allowedToInitiateExplore = false;

        }
    }

    private static double getAverageSpentOnExplore() {
        if (totalExploreTimes == 0)
            return 0.0;

        return ((double)totalMoneySpentOnExplore) / totalExploreTimes;
    }

    private static void InitiateDamage(World game) {
        if (!allowedToInitiateDamage)
            return;

        BankAccount attackerAccount = Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK);
        Path bestPath = null;
        double bestPathScore = -100000.0;

        for (Path path : game.getAttackMapPaths()) {
            Set<TowerDetails> enemyTowers = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, path);

            double score = calculatePathAttackScore(path, enemyTowers);

            if (score > bestPathScore) {
                bestPath = path;
                bestPathScore = score;
            }
        }

        Set<TowerDetails> enemyTowers = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, bestPath);
        double towerLevelAverage = calculateTowerLevelAverage(enemyTowers, game.getVisibleEnemyTowers());

        Logger.println("++++2 " + towerLevelAverage + ", " + LightUnit.getCurrentLevel() + ", " + HeavyUnit.getCurrentLevel());

        Recipe recipe1 = GeneCollections.getCollections().getRecipe(enemyTowers,
                bestPath,
                GeneCollections.Strategy.Explore,
                LightUnit.getCurrentLevel(),
                HeavyUnit.getCurrentLevel(),
                towerLevelAverage,
                game.getCurrentTurn() < 300 ? 3.0 : 3.0);
        Recipe recipe2 = GeneCollections.getCollections().getRecipe(enemyTowers,
                bestPath,
                GeneCollections.Strategy.DamageFullForce,
                LightUnit.getCurrentLevel(),
                HeavyUnit.getCurrentLevel(),
                towerLevelAverage,
                game.getCurrentTurn() < 200 ? 2.0 : 1.0);

        Recipe recipe;

        if (recipe1.getTotalCost() < recipe2.getTotalCost())
            recipe = recipe1;
        else
            recipe = recipe2;

        int totalCost = recipe.getTotalCost();

        if (!attackerAccount.canSpend(totalCost)) {
            attackerAccount.setGoal(totalCost - attackerAccount.getBalance());
            attackerAccount.setIsGoalExact(true);
            Bank.checkGoals();

            if (!attackerAccount.canSpend(totalCost)) {
                attackerAccount.setGoal(0);

                Logger.println("Not enough money to damage. (we need " + totalCost + ")");
                Logger.println("");
                return;
            }
        }

        Logger.println("Damage initiated to " + bestPath.toString());

        currentAttackRecipe.put(bestPath, recipe);

        logRecipe(recipe);

        attackTurns.put(bestPath, 0);
        ProceedAttack(game, bestPath);

        allowedToInitiateExplore = false;
        allowedToInitiateDamage = false;
        damageInProgress = true;
    }

    private static double calculateTowerLevelAverage(Set<TowerDetails> enemyTowers, ArrayList<Tower> visibleEnemyTowers) {
        Set<Integer> towerIndices = new HashSet<>();

        for (TowerDetails td : enemyTowers)
            towerIndices.add(td.getId());

        int levelSum = 0;
        int count = 0;
        for (Tower t : visibleEnemyTowers) {
            if (towerIndices.contains(t.getId())) {
                levelSum += t.getLevel();
                count++;
            }
        }

        if (count == 0)
            return 1.0;

        return ((double)levelSum) / count;
    }

    private static void logRecipe(Recipe recipe) {
        Logger.print("Creeps: ");
        for (byte b : recipe.getCreeps())
            Logger.print(b + ", ");
        Logger.print("Heros: ");
        for (byte b : recipe.getHeros())
            Logger.print(b + ", ");
        Logger.println("");
    }

    private static double calculatePathAttackScore(Path path, Set<TowerDetails> enemyTowers) {
        int pathLength = path.getRoad().size();
        double score = 1.0 * pathLength - 2.0 * enemyTowers.size();

        return score;
    }

    public static void ScheduledOperations(World game) {
        List<Path> paths = new ArrayList<>();

        for (Path path : game.getAttackMapPaths())
            paths.add(path);


        //TODO: Sort?

        for (Path path : paths) {
            if (!attackTurns.containsKey(path))
                attackTurns.put(path, 0);

            if (attackTurns.get(path) == 0) {
                InitiateDamage(game);
                if (game.getCurrentTurn() < 100)
                    InitiateExplore(game, path);
            } else if (attackTurns.get(path) > 0) {
                ProceedAttack(game, path);
            } else if (attackTurns.get(path) < 0) {
                attackTurns.put(path, attackTurns.get(path) + 1);
            }
        }
    }

    private static void InitiateExplore(World game, Path path) {
        if (allowedToInitiateExplore && (!damageInProgress)) {
            Logger.println("InitiateExplore begin for " + path.toString());

            Set<TowerDetails> enemyTowers = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, path);

            double towerLevelAverage = calculateTowerLevelAverage(enemyTowers, game.getVisibleEnemyTowers());
            Logger.println("++++1 " + towerLevelAverage + ", " + LightUnit.getCurrentLevel() + ", " + HeavyUnit.getCurrentLevel());

            double multiplier = 2;

            Recipe recipe = GeneCollections.getCollections().getRecipe(enemyTowers,
                    path,
                    GeneCollections.Strategy.Damage,
                    LightUnit.getCurrentLevel(),
                    HeavyUnit.getCurrentLevel(),
                    towerLevelAverage,
                    multiplier);

            int totalCost = recipe.getTotalCost();
            if (!Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK).canSpend(totalCost)) {
                Logger.println("Not enough money. (we need " + totalCost + ")");
                Logger.println("");
            } else {
                Logger.println("Explore began for " + path.toString());
                currentAttackRecipe.put(path, recipe);

                logRecipe(recipe);

                IncrementExploreCount(path);
                ProceedAttack(game, path);

                allowedToInitiateExplore = false;
            }
        }
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
            allowedToInitiateExplore = true;
            damageInProgress = false;
            attackTurns.put(path, -turnsBetweenExplores);
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

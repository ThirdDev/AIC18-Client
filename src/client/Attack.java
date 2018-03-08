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

    static long beginTime;

    public static void Attack(World game, boolean isHeavyTurn) {
        beginTime = System.currentTimeMillis();

        if (game.getCurrentTurn() < 8 ) {
            DawnAttack(game);
            return;
        }
        Prepare(game);
        ScheduledOperations(game, isHeavyTurn);
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

        //Logger.println("xxx(3) -> " + (System.currentTimeMillis() - beginTime));

        BankAccount attackerAccount = Bank.getAccount(BankController.BANK_ACCOUNT_ATTACK);
        Path bestPath = null;
        double bestPathScore = -100000.0;

        for (Path path : game.getAttackMapPaths()) {
            Set<TowerDetails> enemyTowers = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, path);

            double score;
            score = calculatePathAttackScore(path, enemyTowers);

            if (score > bestPathScore) {
                bestPath = path;
                bestPathScore = score;
            }
        }

        //Logger.println("xxx(4) -> " + (System.currentTimeMillis() - beginTime));

        Set<TowerDetails> enemyTowers = null;
        enemyTowers = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, bestPath);
       /* if (isKhashmeEzhdehaStarted(game)) {
            Logger.println("--- Attacker is in KhashmeEzhdeha mode.");
            for (Path p : game.getAttackMapPaths()) {
                Set<TowerDetails> et = AttackMapAnalyser.getVisibleTowerDetailsForPath(game, p);

                if (enemyTowers == null || et.size() > enemyTowers.size()) {
                    enemyTowers = et;
                }
            }
        }*/

        //Logger.println("xxx(5) -> " + (System.currentTimeMillis() - beginTime));
        double towerLevelAverage = calculateTowerLevelAverage(enemyTowers, game.getVisibleEnemyTowers());
        //Logger.println("xxx(6) -> " + (System.currentTimeMillis() - beginTime));
        Logger.println("++++2 " + towerLevelAverage + ", " + LightUnit.getCurrentLevel() + ", " + HeavyUnit.getCurrentLevel());

        Recipe recipe = GeneCollections.getCollections().getRecipe(enemyTowers,
                bestPath,
                GeneCollections.Strategy.Explore,
                LightUnit.getCurrentLevel(),
                HeavyUnit.getCurrentLevel(),
                towerLevelAverage,
                isKhashmeEzhdehaStarted(game) ? 1.5 : 1.9);

        if (isKhashmeEzhdehaStarted(game))
            recipe.repeat(2);

        //Logger.println("xxx(7) -> " + (System.currentTimeMillis() - beginTime));
        int totalCost = recipe.getTotalCost();
        //Logger.println("xxx(8) -> " + (System.currentTimeMillis() - beginTime));
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
        //Logger.println("xxx(9) -> " + (System.currentTimeMillis() - beginTime));
        Logger.println("Damage initiated to " + bestPath.toString());

        currentAttackRecipe.put(bestPath, recipe);

        logRecipe(recipe);

        attackTurns.put(bestPath, 0);
        ProceedAttack(game, bestPath);

        allowedToInitiateExplore = false;
        allowedToInitiateDamage = false;
        damageInProgress = true;

        //Logger.println("xxx(10) -> " + (System.currentTimeMillis() - beginTime));
    }

    private static boolean isKhashmeEzhdehaStarted(World game) {
        return ((double)game.getCurrentTurn()) > Math.min(Game.MAX_TURNS_IN_GAME / 2.0, 600.0);
    }

    private static double calculatePathAttackScore2(Path path) {
        return (double)path.getRoad().size();
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
        double score = 2.0 * pathLength - 1.0 * enemyTowers.size();

        return score;
    }

    public static void ScheduledOperations(World game, boolean allowInitiate) {
        //Logger.println("xxx(1) -> " + (System.currentTimeMillis() - beginTime));

        List<Path> paths = new ArrayList<>();

        for (Path path : game.getAttackMapPaths())
            paths.add(path);


        //TODO: Sort?

        for (Path path : paths) {
            //Logger.println("xxx(2+) -> " + (System.currentTimeMillis() - beginTime));
            if (!attackTurns.containsKey(path))
                attackTurns.put(path, 0);

            if (attackTurns.get(path) == 0 && allowInitiate) {
                if (game.getCurrentTurn() < 150) // بهتره ضریب نباشه و عدد ثابت باشه، بعد از ۲۰۰ سیکل دیگه سخت شده کار بریم سراغ همون دمیج زدن
                    InitiateExplore(game, path);
                else
                    InitiateDamage(game);


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

            double multiplier = 3;

            Recipe recipe = GeneCollections.getCollections().getRecipe(enemyTowers,
                    path,
                    GeneCollections.Strategy.Damage,
                    LightUnit.getCurrentLevel(),
                    HeavyUnit.getCurrentLevel(),
                    towerLevelAverage,
                    multiplier);
            recipe.repeat(5);

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

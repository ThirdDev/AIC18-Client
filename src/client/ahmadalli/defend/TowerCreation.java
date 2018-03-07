package client.ahmadalli.defend;

import client.BankController;
import client.Util;
import client.ahmadalli.utils.Cell;
import client.ahmadalli.utils.Finance;
import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.model.*;

import java.util.*;

public class TowerCreation {

    private static Random rnd = new Random();

    public static void simpleTowerCreation(World world) {
        long startMills = System.currentTimeMillis();
        Logger.println("-- starting to defend simply --");

        BankAccount defendAccount = Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE);
        if (defendAccount == null || !Finance.canCreateBasicTower(defendAccount))
            return;

        GrassCell[] sidewayCells = world.getDefenceMapPaths().stream()
                .flatMap(x -> x.getRoad().stream())
                .flatMap(x -> Util.radialCells(x, 2, world.getDefenceMap()).stream())
                .distinct()
                .filter(x -> x instanceof GrassCell)
                .toArray(GrassCell[]::new);

        if (sidewayCells.length == 0)
            return;

        LinkedList<GrassCell> sidewayShuffle = new LinkedList<>(Arrays.asList(sidewayCells));

        Collections.shuffle(sidewayShuffle);

        while (Finance.canCreateBasicTower(defendAccount)) {
            if ((System.currentTimeMillis() - startMills) > 100) {
                break;
            }
            Logger.println("calculating grass cells score. time passed: " + (System.currentTimeMillis() - startMills));
            GrassCell bestCell = sidewayShuffle.stream()
                    .filter(x -> !Cell.hasTowerBesideOfIt(x, world))
                    .sorted(client.ahmadalli.comparator.Cell.byDefendScore(
                            world.getDefenceMap(), world.getDefenceMapPaths()))
                    .findFirst()
                    .get();

            GrassCell cellToBuild;

            // GrassCell randomSideWayCell = bestCells[rnd.nextInt(bestCells.length)];
            // cellToBuild = randomSideWayCell;

            cellToBuild = bestCell;

            Tower tower = cellToBuild.getTower();

            Logger.println("deciding on build or upgrade on the best grass cell. time passed: " + (System.currentTimeMillis() - startMills));

            if (tower != null) {
                int towerLevel = tower.getLevel();
                int upgradePrice = 0;
                String towerType = "";
                if (tower instanceof ArcherTower) {
                    upgradePrice = ArcherTower.getPrice(towerLevel + 1) - ArcherTower.getPrice(towerLevel);
                    towerType = "archer";
                }
                if (tower instanceof CannonTower) {
                    upgradePrice = CannonTower.getPrice(towerLevel + 1) - CannonTower.getPrice(towerLevel);
                    towerType = "cannon";
                }
                if (defendAccount.retrieveMoney(upgradePrice)) {
                    int x = tower.getLocation().getX();
                    int y = tower.getLocation().getY();
                    Logger.println("upgrading a level " + towerLevel + " " + towerType + " tower @(" + x + ", " + y + ")");
                    world.upgradeTower(tower);
                } else {
                    int x = tower.getLocation().getX();
                    int y = tower.getLocation().getY();
                    Logger.println("not enough money fo upgrading a level " + towerLevel + " " + towerType + " tower @(" + x + ", " + y + ")");
                    break;
                }
            } else {
                int creepCount = (int) Cell.getNearbyRoadCells(cellToBuild, world.getDefenceMap())
                        .flatMap(x -> x.getUnits().stream())
                        .filter(x -> x instanceof LightUnit)
                        .count();

                int heroCount = (int) Cell.getNearbyRoadCells(cellToBuild, world.getDefenceMap())
                        .flatMap(x -> x.getUnits().stream())
                        .filter(x -> x instanceof HeavyUnit)
                        .count();

                int towerType = rnd.nextInt(5);
                if (heroCount > 0 || creepCount > 0) {
                    double heroScore = heroCount * HeavyUnit.DAMAGE / (double) HeavyUnit.MOVE_SPEED;
                    double creepScore = creepCount * LightUnit.DAMAGE / (double) LightUnit.MOVE_SPEED;
                    if (heroScore > creepScore) {
                        towerType = 0;
                    } else {
                        towerType = 1;
                    }
                }
                int level = 1;
                if (towerType == 0) {
                    if (defendAccount.retrieveMoney(ArcherTower.getPrice(level))) {
                        int x = cellToBuild.getLocation().getX();
                        int y = cellToBuild.getLocation().getY();
                        Logger.println("creating a level " + level + " archer tower @(" + x + ", " + y + ")");
                        world.createArcherTower(level, x, y);
                    } else {
                        int x = cellToBuild.getLocation().getX();
                        int y = cellToBuild.getLocation().getY();
                        Logger.println("not enough money for creating a level " + level + " archer tower @(" + x + ", " + y + ")");
                        break;
                    }
                }
                if (towerType != 0) {
                    if (defendAccount.retrieveMoney(CannonTower.getPrice(level))) {
                        int x = cellToBuild.getLocation().getX();
                        int y = cellToBuild.getLocation().getY();
                        Logger.println("creating a level " + level + " cannon tower @(" + x + ", " + y + ")");
                        world.createCannonTower(level, x, y);
                    } else {
                        int x = cellToBuild.getLocation().getX();
                        int y = cellToBuild.getLocation().getY();
                        Logger.println("not enough money for creating a level " + level + " cannon tower @(" + x + ", " + y + ")");
                        break;
                    }
                }
            }
        }

        Logger.println("-- simple defend ended, time passed: " + (System.currentTimeMillis() - startMills) + " --");
    }
}

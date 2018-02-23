package client;

import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.model.*;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

public class ahmadalli {

    private static Random rnd = new Random();

    private static Comparator<Cell> compareByTroopAndRoadCellCount(World world) {
        return Comparator.comparingInt(o -> cellScore(o, world));
    }

    public static int cellScore(Cell cell, World world) {
        return (int) getNearbyRoadCells(cell, world)
                .count();
    }

    public static Stream<RoadCell> getNearbyRoadCells(Cell cell, World world) {
        return Util.radialCells(cell, 2, world.getDefenceMap()).stream()
                .filter(x -> x instanceof RoadCell)
                .map(x -> (RoadCell) x);
    }

    public static Stream<Path> getNearbyPaths(Cell cell, int range, World world) {
        return world.getDefenceMapPaths().stream()
                .filter(x -> Stream.concat(x.getRoad().stream()
                                .flatMap(y -> Util.radialCells(y, range, world.getDefenceMap()).stream()),
                        x.getRoad().stream())
                        .anyMatch(y -> y.equals(cell)));
    }

    public static boolean hasTowerBesideOfIt(Cell cell, World world) {
        return Util.radialCells(cell, 1, world.getDefenceMap()).stream()
                .filter(x -> x instanceof GrassCell)
                .map(x -> (GrassCell) x)
                .anyMatch(x -> !x.isEmpty());
    }

    public static void plantRandomTowerInASidewayCell(World world) {
        GrassCell[] sidewayCells = world.getDefenceMapPaths().stream()
                .flatMap(x -> x.getRoad().stream())
                .flatMap(x -> Util.radialCells(x, 2, world.getDefenceMap()).stream())
                .filter(x -> x instanceof GrassCell)
                .map(x -> (GrassCell) x)
                .filter(x -> x.isEmpty())
                .filter(x -> !hasTowerBesideOfIt(x, world))
                .distinct()
                .sorted(compareByTroopAndRoadCellCount(world))
                .toArray(GrassCell[]::new);

        if (sidewayCells.length == 0)
            return;


        GrassCell cellToBuild = null;

        // GrassCell randomSideWayCell = sidewayCells[rnd.nextInt(sidewayCells.length)];
        // cellToBuild = randomSideWayCell;

        cellToBuild = sidewayCells[sidewayCells.length - 1];

        BankAccount defendAccount = Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE);

        int towerType = rnd.nextInt() % 5;
        int level = 1;
        if (towerType == 0 && defendAccount.canSpend(ArcherTower.INITIAL_PRICE)) {
            if (defendAccount.retrieveMoney(ArcherTower.INITIAL_PRICE)) {
                int x = cellToBuild.getLocation().getX();
                int y = cellToBuild.getLocation().getY();
                Logger.println("creating an archer tower @(" + x + ", " + y + ")");
                world.createArcherTower(level, x, y);
            }
        }
        if (towerType != 0 && defendAccount.canSpend(CannonTower.INITIAL_PRICE)) {
            if (defendAccount.retrieveMoney(CannonTower.INITIAL_PRICE)) {
                int x = cellToBuild.getLocation().getX();
                int y = cellToBuild.getLocation().getY();
                Logger.println("creating an cannon tower @(" + x + ", " + y + ")");
                world.createCannonTower(level, x, y);
            }
        }
    }


}

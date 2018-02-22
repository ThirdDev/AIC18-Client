package client;

import client.classes.Bank;
import client.classes.BankAccount;
import client.model.*;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

public class ahmadalli {

    private static Comparator<Cell> compareByTroopAndRoadCellCount(World world) {
        return Comparator.comparingInt(o -> cellScore(o, world));
    }

    public static int cellScore(Cell cell, World world) {
        return getNearbyRoadCells(cell, world).
                mapToInt(x -> (int) x.getUnits().stream().count() + 1).sum();
    }

    public static Stream<RoadCell> getNearbyRoadCells(Cell cell, World world) {
        return Util.radiusCells(cell, 2, world.getDefenceMap()).stream()
                .filter(x -> x instanceof RoadCell)
                .map(x -> (RoadCell) x);
    }

    public static void plantRandomTowerInASidewayCell(World world) {
        GrassCell[] sidewayCells = world.getDefenceMapPaths().stream()
                .flatMap(x -> x.getRoad().stream())
                .flatMap(x -> Util.radiusCells(x, 2, world.getDefenceMap()).stream())
                .filter(x -> x instanceof GrassCell)
                .map(x -> (GrassCell) x)
                .filter(x -> x.getTower() == null)
                .distinct()
                .sorted(compareByTroopAndRoadCellCount(world))
                .toArray(GrassCell[]::new);

        GrassCell randomSideWayCell = sidewayCells[rnd.nextInt(sidewayCells.length)];
        BankAccount defendAccount = null;
        try {
            defendAccount = Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE);
        } catch (Exception ex) {
            return;
        }

        int towerType = rnd.nextInt() % 2;
        int level = 1;
        if (towerType == 0 && defendAccount.canSpend(ArcherTower.INITIAL_PRICE)) {
            try {
                defendAccount.retrieveMoney(ArcherTower.INITIAL_PRICE);
                world.createArcherTower(level, randomSideWayCell.getLocation().getX(), randomSideWayCell.getLocation().getX());
            } catch (Exception ex) {
            }
        }
        if (towerType == 1 && defendAccount.canSpend(CannonTower.INITIAL_PRICE)) {
            try {
                defendAccount.retrieveMoney(CannonTower.INITIAL_PRICE);
                world.createCannonTower(level, randomSideWayCell.getLocation().getX(), randomSideWayCell.getLocation().getX());
            } catch (Exception ex) {
            }
        }
    }

    static Random rnd = new Random();
}

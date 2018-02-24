package client;

import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

public class ahmadalli {

    private static Random rnd = new Random();

    private static Comparator<Cell> compareByTroopAndRoadCellCount(World world) {
        return Comparator.comparingInt(o -> cellScore(o, world));
    }

    public static int cellScore(Cell cell, World world) {
        return getNearbyRoadCells(cell, world).
                mapToInt(x -> (int) x.getUnits().stream().count() + 1).sum();
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

    public static Stream<RoadCell> dangerousCellsOrderByDangerScore(ArrayList<Path> paths, Map map, double portion, int minCount, int maxCount) {
        return paths.stream()
                .flatMap(x -> endingRoadCells(x, portion, minCount, maxCount).stream())
                .filter(x -> dangerScore(x, map) > 0)
                .sorted((x, y) -> (int) (dangerScore(y, map) - dangerScore(x, map)));
    }

    public static long dangerScore(RoadCell roadCell, Map map) {
        Stream<Cell> radialCells = Util.radialCells(roadCell, 2, map).stream();
        Stream<GrassCell> towerCellsInRange = radialCells
                .filter(x -> x instanceof GrassCell)
                .map(x -> (GrassCell) x)
                .filter(x -> !x.isEmpty());

        boolean hasCannonCellsInRange = towerCellsInRange
                .filter(x -> x.getTower() instanceof CannonTower)
                .count() > 0;

        long archerCellsInRangeCount = towerCellsInRange
                .filter(x -> x.getTower() instanceof ArcherTower)
                .count();

        Stream<Unit> units = roadCell.getUnits().stream();

        long creepsCount = units.filter(x -> x instanceof LightUnit).count();
        long herosCount = units.filter(x -> x instanceof HeavyUnit).count();

        long score = 0;

        if (creepsCount > 0 && !hasCannonCellsInRange)
            score += creepsCount * LightUnit.DAMAGE;
        if (archerCellsInRangeCount < herosCount)
            score += (herosCount) * HeavyUnit.DAMAGE;

        return score;
    }

    public static ArrayList<RoadCell> endingRoadCells(Path path, double portion, int minCount, int maxCount) {
        ArrayList<RoadCell> roeadCells = path.getRoad();

        int portionCount = (int) (roeadCells.size() * portion);
        int count = Math.max(minCount, portionCount);
        count = Math.min(maxCount, count);
        count = Math.min(roeadCells.size(), count);


        ArrayList<RoadCell> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(roeadCells.get(roeadCells.size() - 1 - i));
        }

        return result;

    }


}

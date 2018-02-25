package client;

import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.model.*;
import client.model.Map;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class ahmadalli {

    private static Random rnd = new Random();

    private static Comparator<Cell> compareByTroopAndRoadCellCount(Map map, ArrayList<Path> paths) {
        return Comparator.comparingDouble(o -> cellScore(o, map, paths));
    }

    public static void initialize(World world) {
        initializePathRoadeCellIndex(world.getDefenceMapPaths());
        updateBeans(world.getBeansInThisTurn());
    }

    private static void updateBeans(ArrayList<BeanEvent> beanEvents) {
        for (BeanEvent beanEvent : beanEvents) {
            Logger.println("bean event with owner " + beanEvent.getOwner() +
                    " in point " + beanEvent.getPoint() + " received");
            if (beanEvent.getOwner() == Owner.ENEMY)
                beannedLocations.add(beanEvent.getPoint().hashCode());
        }
    }

    public static double cellScore(Cell cell, Map map, ArrayList<Path> paths) {
        int nearbyCellsScore = getNearbyRoadCells(cell, map).
                mapToInt(x -> x.getUnits().size() + 1).sum() * 10;

        double towerScore = 0;
        if (cell instanceof GrassCell) {
            Tower tower = ((GrassCell) cell).getTower();
            if (tower != null) {
                towerScore = (-tower.getLevel() * 2);
            }
        }

        double pathScore = getNearbyPaths(cell, 2, paths, map)
                .flatMapToDouble(path -> {
                    int pathPossibleCoverage = (int) path.getRoad().stream()
                            .flatMap(x -> Util.radialCells(x, 2, map).stream())
                            .distinct()
                            .filter(x -> x instanceof GrassCell)
                            .count();

                    int pathActualCoverage = (int) path.getRoad().stream()
                            .flatMap(x -> Util.radialCells(x, 2, map).stream())
                            .distinct()
                            .filter(x -> x instanceof GrassCell)
                            .map(x -> (GrassCell) x)
                            .filter(x -> !x.isEmpty())
                            .count();

                    return DoubleStream.of(-(double) pathActualCoverage / pathPossibleCoverage);
                })
                .sum();

        double finalScore = nearbyCellsScore + towerScore + pathScore;

        return finalScore;
    }

    public static Stream<RoadCell> getNearbyRoadCells(Cell cell, Map map) {
        return Util.radialCells(cell, 2, map).stream()
                .filter(x -> x instanceof RoadCell)
                .map(x -> (RoadCell) x);
    }

    public static Stream<Path> getNearbyPaths(Cell cell, int range, ArrayList<Path> paths, Map map) {
        return paths.stream()
                .filter(x -> Stream.concat(x.getRoad().stream()
                                .flatMap(y -> Util.radialCells(y, range, map).stream()),
                        x.getRoad().stream())
                        .anyMatch(y -> y.equals(cell)));
    }

    public static boolean hasTowerBesideOfIt(Cell cell, World world) {
        return Util.radialCells(cell, 1, world.getDefenceMap()).stream()
                .filter(x -> x instanceof GrassCell)
                .map(x -> (GrassCell) x)
                .anyMatch(x -> !x.isEmpty());
    }

    private static boolean canCreateBasicTower(BankAccount defenceBankAccount) {
        return defenceBankAccount.canSpend(ArcherTower.INITIAL_PRICE) ||
                defenceBankAccount.canSpend(CannonTower.INITIAL_PRICE);
    }

    private static HashSet<Integer> beannedLocations = new HashSet<>();

    public static boolean isBeanned(Point point) {
        return beannedLocations.contains(point.hashCode());
    }

    public static void simpleTowerCreation(World world) {
        BankAccount defendAccount = Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE);
        if (defendAccount == null)
            return;

        while (canCreateBasicTower(defendAccount)) {
            GrassCell[] sidewayCells = world.getDefenceMapPaths().stream()
                    .flatMap(x -> x.getRoad().stream())
                    .flatMap(x -> Util.radialCells(x, 2, world.getDefenceMap()).stream())
                    .distinct()
                    .filter(x -> !isBeanned(x.getLocation()))
                    .filter(x -> x instanceof GrassCell)
                    .map(x -> (GrassCell) x)
                    .filter(x -> !hasTowerBesideOfIt(x, world))
                    .sorted(compareByTroopAndRoadCellCount(world.getDefenceMap(), world.getDefenceMapPaths()))
                    .toArray(GrassCell[]::new);

            if (sidewayCells.length == 0)
                return;

            GrassCell cellToBuild;

            // GrassCell randomSideWayCell = sidewayCells[rnd.nextInt(sidewayCells.length)];
            // cellToBuild = randomSideWayCell;

            cellToBuild = sidewayCells[sidewayCells.length - 1];

            Tower tower = cellToBuild.getTower();

            if (tower != null) {
                int towerLevel = tower.getLevel();
                int upgradePrice = 0;
                if (tower instanceof ArcherTower) {
                    upgradePrice = ArcherTower.getPrice(towerLevel + 1) - ArcherTower.getPrice(towerLevel);
                }
                if (tower instanceof CannonTower) {
                    upgradePrice = CannonTower.getPrice(towerLevel + 1) - ArcherTower.getPrice(towerLevel);
                }
                if (defendAccount.retrieveMoney(upgradePrice)) {
                    world.upgradeTower(tower);
                } else {
                    break;
                }
            } else {
                int creepCount = (int) getNearbyRoadCells(cellToBuild, world.getDefenceMap())
                        .flatMap(x -> x.getUnits().stream())
                        .filter(x -> x instanceof LightUnit)
                        .count();

                int heroCount = (int) getNearbyRoadCells(cellToBuild, world.getDefenceMap())
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
                        Logger.println("creating an archer tower @(" + x + ", " + y + ")");
                        world.createArcherTower(level, x, y);
                    } else {
                        break;
                    }
                }
                if (towerType != 0) {
                    if (defendAccount.retrieveMoney(CannonTower.getPrice(level))) {
                        int x = cellToBuild.getLocation().getX();
                        int y = cellToBuild.getLocation().getY();
                        Logger.println("creating an cannon tower @(" + x + ", " + y + ")");
                        world.createCannonTower(level, x, y);
                    } else {
                        break;
                    }
                }
            }


        }
    }

    public static void stormIfNecessary(World world) {
        if (world.getMyInformation().getStormsLeft() == 0)
            return;

        RoadCell[] dangerousInRange3Ordered = dangerousCellsOrderByDangerScoreAscending(
                world.getDefenceMapPaths(),
                0.2, 3, 3);
        if (dangerousInRange3Ordered.length == 0)
            return;

        RoadCell mostDangerous = dangerousInRange3Ordered[0];
        Cell bestShot = getCenterOfMostVulnerableAreaContainingRoadCell(mostDangerous, world.getDefenceMap(), 2);
        int bestShotScore = stormDamageScoreSum(bestShot, world.getDefenceMap(), 2);
        int mostDangerousScore = dangerScore(mostDangerous);
        Logger.println("bestShotScore = " + bestShotScore + ", mostDangerousScore = " + mostDangerousScore);
        if ((bestShotScore >= 10) || (mostDangerousScore >= world.getMyInformation().getStrength())) {
            world.createStorm(bestShot.getLocation().getX(), bestShot.getLocation().getY());
        }

    }

    public static int stormDamageScoreSum(Cell cell, Map map, int stormRange) {
        return Util.radialCells(cell, stormRange, map).stream()
                .filter(x -> x instanceof RoadCell)
                .map(x -> (RoadCell) x)
                .mapToInt(x -> dangerScore(x))
                .sum();
    }

    public static Cell getCenterOfMostVulnerableAreaContainingRoadCell(RoadCell cell, Map map, int range) {
        return Util.radialCells(cell, range, map).stream()
                .sorted((x, y) -> stormDamageScoreSum(y, map, range) - stormDamageScoreSum(x, map, range))
                .findFirst().get();
    }

    static HashMap<Point, Integer> pathIndexOfRoadMap = null;

    public static int getPathIndexOfRoadCell(Point roadCellLocation) {
        return pathIndexOfRoadMap.get(roadCellLocation);
    }

    private static void initializePathRoadeCellIndex(ArrayList<Path> paths) {
        if (pathIndexOfRoadMap == null) {
            pathIndexOfRoadMap = new HashMap<>();
            for (int i = 0; i < paths.size(); i++) {
                for (RoadCell theRoadCell : paths.get(i).getRoad()) {
                    pathIndexOfRoadMap.put(theRoadCell.getLocation(), i);
                }
            }
        }
    }

    public static RoadCell[] dangerousCellsOrderByDangerScoreAscending(ArrayList<Path> paths, double portion, int minCount, int maxCount) {
        return paths.stream()
                .flatMap(x -> endingRoadCells(x, portion, minCount, maxCount).stream())
                .filter(x -> dangerScore(x) > 0)
                .sorted((x, y) -> dangerScore(y) - dangerScore(x))
                .toArray(RoadCell[]::new);
    }

    public static int dangerScore(RoadCell roadCell) {
        long creepsCount = roadCell.getUnits().stream().filter(x -> x instanceof LightUnit).count();
        long herosCount = roadCell.getUnits().stream().filter(x -> x instanceof HeavyUnit).count();

        long score = 0;

        score += creepsCount * LightUnit.DAMAGE;
        score += (herosCount) * HeavyUnit.DAMAGE;

        return (int) score;
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

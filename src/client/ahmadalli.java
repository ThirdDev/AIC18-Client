package client;

import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.classes.simulator.towers.Cannon;
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
    }

    public static double cellUnitScore(RoadCell roadCell) {
        int creepCount = (int) roadCell.getUnits().stream()
                .filter(x -> x instanceof LightUnit)
                .count();

        int heroCount = (int) roadCell.getUnits().stream()
                .filter(x -> x instanceof HeavyUnit)
                .count();

        return creepCount * 1.0 + heroCount * 2.5;

    }

    public static double pathCoverageScore(Path path, Map map) {
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
                .mapToInt(x -> x.getTower().getLevel())
                .count();

        return -(double) pathActualCoverage / pathPossibleCoverage * 1.0;
    }

    public static double cellScore(Cell cell, Map map, ArrayList<Path> paths) {
        double nearbyCellsScore = getNearbyRoadCells(cell, map).
                mapToDouble(x -> cellUnitScore(x) + 1).sum();

        double towerScore = 0;
        if (cell instanceof GrassCell) {
            Tower tower = ((GrassCell) cell).getTower();
            if (tower != null) {
                towerScore = (-tower.getLevel() * 30);
            }
        }

        double pathScore = 0;

        /**/
        pathScore = getNearbyPaths(cell, 2, paths, map)
                .mapToDouble(path -> pathCoverageScore(path, map))
                .sum();
        /**/

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

    @SuppressWarnings("RedundantStreamOptionalCall")
    public static void simpleTowerCreation(World world) {
        Logger.println("-- starting to defend simply --");

        BankAccount defendAccount = Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE);
        if (defendAccount == null)
            return;

        while (canCreateBasicTower(defendAccount)) {
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
            GrassCell[] bestCells = sidewayShuffle.stream()
                    .filter(x -> x instanceof GrassCell)
                    .map(x -> x)
                    .filter(x -> !hasTowerBesideOfIt(x, world))
                    .sorted(compareByTroopAndRoadCellCount(world.getDefenceMap(), world.getDefenceMapPaths()))
                    .toArray(GrassCell[]::new);

            GrassCell cellToBuild;

            // GrassCell randomSideWayCell = bestCells[rnd.nextInt(bestCells.length)];
            // cellToBuild = randomSideWayCell;

            cellToBuild = bestCells[bestCells.length - 1];

            Tower tower = cellToBuild.getTower();

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
                        Logger.println("creating a level " + level + " archer tower @(" + x + ", " + y + ")");
                        world.createArcherTower(level, x, y);
                    } else {
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
                        break;
                    }
                }
            }
        }

        Logger.println("-- simple defend ended --");
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

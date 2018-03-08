package client.classes.genes;

import client.classes.Logger;
import client.classes.simulator.Simulator;
import client.classes.simulator.towers.Archer;
import client.classes.simulator.towers.Cannon;
import client.model.Path;
import client.model.Point;
import client.model.TowerDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GeneCollections {

    public enum Strategy {
        Explore,
        Damage,
        DamageFullForce,
    }

    // pathLength = 20
    public final int FourTowersMaxLength = 20;
    public FullStateGeneCollection FourTowersDamage500;
    public FullStateGeneCollection FourTowersDamage1200;
    public FullStateGeneCollection FourTowersExplore500;

    public final int ThreeTowersMaxLength = 25;
    public FullStateGeneCollection ThreeTowersDamage600;
    public FullStateGeneCollection ThreeTowersExplore500;

    // pathLength = 40
    public final int TwoDoubleTowersMaxLength = 40;
    public FullStateGeneCollection TwoDoubleTowersDamage700;
    public FullStateGeneCollection TwoDoubleTowersExplore200;

    // pathLength = 100
    public final int TwoTowersMaxLength = 100;
    public FullStateGeneCollection TwoTowersDamage500;
    public FullStateGeneCollection TwoTowersExplore400;

    public final int SingleTowerMaxLength = 100;
    public FullStateGeneCollection SingleTowerDamage200;
    public FullStateGeneCollection SingleTowerDamage500;
    public FullStateGeneCollection SingleTowerExplore150;

    //public final int RandomMaxCount = 20;
    //public CountStateGeneCollection RandomDamage2500;
    //public CountStateGeneCollection RandomExplore1000;

    public final int UnifiedRandomMaxCount = 20;
    public CountStateGeneCollection UniformRandomDamage;
    public CountStateGeneCollection UniformRandomExplore;

    //Singleton class
    private GeneCollections() {
        long time = System.currentTimeMillis();

        FourTowersDamage500 = new FullStateGeneCollection("FourTowers-DamagePolicy 500-pathLength 20-2018-21-2--00-51-00.sgdf");
        FourTowersDamage1200 = new FullStateGeneCollection("FourTowers-DamagePolicy 1200-pathLength 20-2018-21-2--01-00-47.sgdf");
        FourTowersExplore500 = new FullStateGeneCollection("FourTowers-ExplorePolicy 500-pathLength 20-2018-23-2--04-06-26.sgdf");

        ThreeTowersDamage600 = new FullStateGeneCollection("ThreeTowers-DamagePolicy 600-pathLength 25-2018-23-2--04-11-39.sgdf");
        ThreeTowersExplore500 = new FullStateGeneCollection("ThreeTowers-ExplorePolicy 500-pathLength 25-2018-23-2--04-05-51.sgdf");

        TwoDoubleTowersDamage700 = new FullStateGeneCollection("TwoDoubleTowers-DamagePolicy 700-pathLength 40-2018-20-2--19-19-12.sgdf");
        TwoDoubleTowersExplore200 = new FullStateGeneCollection("TwoDoubleTowers-ExplorePolicy 200-pathLength 40-2018-20-2--19-05-53.sgdf");

        TwoTowersDamage500 = new FullStateGeneCollection("TwoTowers-DamagePolicy 500-pathLength 100-2018-23-2--04-49-45.sgdf");
        TwoTowersExplore400 = new FullStateGeneCollection("TwoTowers-ExplorePolicy 400-pathLength 100-2018-23-2--04-43-28.sgdf");

        SingleTowerDamage200 = new FullStateGeneCollection("SingleTower-DamagePolicy 200-pathLength 100-2018-21-2--17-26-28.sgdf");
        SingleTowerDamage500 = new FullStateGeneCollection("SingleTower-DamagePolicy 500-pathLength 100-2018-21-2--22-03-37.sgdf");
        SingleTowerExplore150 = new FullStateGeneCollection("SingleTower-ExplorePolicy 150-pathLength 100-2018-21-2--19-20-24.sgdf");

        UniformRandomExplore = new CountStateGeneCollection(
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 20-2018-23-2--14-53-16.sgdf",
                "ahmadalli.sgdf",
                "amirhossein.sgdf",
                "de1.sgdf",
                "de2.sgdf",
                "pc1.sgdf",
                "pc2.sgdf",
                "surface1.sgdf",
                "us1.sgdf",
                "us2.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--13-09-53.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--13-20-57.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--13-21-19.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--13-22-12.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--19-04-06.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--19-05-14.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--19-06-41.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--19-08-17.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--19-09-10.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-07-3--22-27-28.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-08-3--04-51-08.sgdf",
                "UniformRandomTowers-ExplorePolicyByTowerCount -1-pathLength 25-2018-08-3--04-52-08.sgdf"
                );
        UniformRandomDamage = new CountStateGeneCollection("UniformRandomTowers-DamagePolicyByTowerCount -1-pathLength 20-2018-23-2--14-49-51.sgdf");

        long delta = System.currentTimeMillis() - time;
        Logger.println(delta + " ms");
    }

    private static GeneCollections instance = null;
    public static GeneCollections getCollections() {
        if (instance == null)
            instance = new GeneCollections();

        return instance;
    }

    public Recipe getRecipe(Set<TowerDetails> towers, Path path, Strategy strategy, int creepLevel, int heroLevel, double towerLevelAverage, double multiplier) {
        List<Integer> cannonsModel = new ArrayList<>();
        List<Integer> archersModel = new ArrayList<>();

        List<Cannon> cannonsSimulationModel = new ArrayList<>();
        List<Archer> archersSimulationModel = new ArrayList<>();

        CreatePathModel(towers, path, cannonsModel, archersModel, cannonsSimulationModel, archersSimulationModel);

        GeneCollection collection = FindSuitableGeneCollectionFor(cannonsModel, archersModel, cannonsSimulationModel, archersSimulationModel, strategy);

        if (collection == null)
            Logger.println("Okay, I don't know what to do. I have " + cannonsModel.size() + " cannons and " + archersModel.size() + " archers...");
        else
            Logger.println("Will get gene from " + collection.getResourceName() + " (we have " + cannonsModel.size() + " cannons and " + archersModel.size() + " archers now)");
        collection.setMultiplier(multiplier);
        collection.setCreepLevel(creepLevel);
        collection.setHeroLevel(heroLevel);
        collection.setTowerLevelAverage(towerLevelAverage);
        Recipe recipe = collection.getRecipe(cannonsModel.stream().mapToInt(Integer::intValue).toArray(), archersModel.stream().mapToInt(Integer::intValue).toArray());

        //Sorry for the hack.
        if (collection instanceof FullStateGeneCollection) {
            recipe.repeat(3);
        }

        return recipe;
    }

    private void CreatePathModel(Set<TowerDetails> towers, Path path, List<Integer> cannons, List<Integer> archers, List<Cannon> cannonsSimu, List<Archer> archersSimu) {
        for (TowerDetails details : towers) {
            List<Point> points = details.getPointsForPath(path);
            List<List<Point>> adjacentPoints = groupAdjacentPoints(points);

            CreatePathModelForGenes(path, cannons, archers, details, adjacentPoints);
            CreatePathModelForSimulation(path, cannonsSimu, archersSimu, details, adjacentPoints);
        }
    }

    private void CreatePathModelForSimulation(Path path, List<Cannon> cannons, List<Archer> archers, TowerDetails details, List<List<Point>> adjacentPoints) {
        for (List<Point> pointGroup : adjacentPoints) {
            int centerPos = path.getPointIndex(pointGroup.get(pointGroup.size() / 2));
            int[] delta = new int[pointGroup.size()];
            for (int i = 0; i < pointGroup.size(); i++)
                delta[i] = i - (pointGroup.size() / 2);

            if (details.isArcher())
                archers.add(new Archer(centerPos, delta));
            else
                cannons.add(new Cannon(centerPos, delta));
        }
    }

    private void CreatePathModelForGenes(Path path, List<Integer> cannons, List<Integer> archers, TowerDetails details, List<List<Point>> adjacentPoints) {
        for (List<Point> pointGroup : adjacentPoints) {
            if ((pointGroup.size() == 1) || (pointGroup.size() == 2)) {
                int pointIndex = path.getPointIndex(pointGroup.get(0));

                if (pointIndex < 0) {
                    Logger.error("Something's wrong in getRecipe! (1)");
                    continue;
                }

                if (details.isArcher())
                    archers.add(pointIndex);
                else
                    cannons.add(pointIndex);
            } else if (pointGroup.size() > 2) {
                int start = 1;
                int end = pointGroup.size() - 2;

                while (start <= end) {
                    int pointIndex1 = path.getPointIndex(pointGroup.get(start));
                    int pointIndex2 = path.getPointIndex(pointGroup.get(end));

                    if (pointIndex1 < 0 || pointIndex2 < 0) {
                        Logger.error("Something's wrong in getRecipe! (2)");
                        continue;
                    }

                    if (details.isArcher())
                        archers.add(pointIndex1);
                    else
                        cannons.add(pointIndex1);

                    if (pointIndex1 != pointIndex2) {
                        if (details.isArcher())
                            archers.add(pointIndex2);
                        else
                            cannons.add(pointIndex2);
                    }

                    start += 2;
                    end -= 2;
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private GeneCollection FindSuitableGeneCollectionFor(List<Integer> cannons, List<Integer> archers, List<Cannon> cannonsSimulationModel, List<Archer> archersSimulationModel, Strategy strategy) {
        int totalCount = cannons.size() + archers.size();

        int maxPosition;

        if (cannons.size() == 0 && archers.size() == 0) {
            if (strategy == Strategy.Explore)
                return SingleTowerExplore150;
            else if (strategy == Strategy.Damage)
                return SingleTowerDamage200;
            else
                return SingleTowerDamage500;
        }
        else if (cannons.size() == 0)
            maxPosition = Collections.max(archers);
        else if (archers.size() == 0)
            maxPosition = Collections.max(cannons);
        else
            maxPosition = Math.max(Collections.max(cannons), Collections.max(archers));

        Simulator simulator = new Simulator(maxPosition + 1, (maxPosition + 1) * 4, cannonsSimulationModel, archersSimulationModel);

        UniformRandomDamage.setSimulator(simulator);
        UniformRandomDamage.setTimeout(30);

        UniformRandomExplore.setSimulator(simulator);
        UniformRandomExplore.setTimeout(30);

        if (totalCount <= 1) {
            if (maxPosition < SingleTowerMaxLength) {
                if (strategy == Strategy.Explore)
                    return SingleTowerExplore150;
                else if (strategy == Strategy.Damage)
                    return SingleTowerDamage200;
                else
                    return SingleTowerDamage500;
            } else {
                if (strategy == Strategy.Explore)
                    return UniformRandomExplore;
                else
                    return UniformRandomDamage;
            }
        } else if (totalCount == 2) {
            if (maxPosition < TwoTowersMaxLength) {
                if (strategy == Strategy.Explore)
                    return TwoTowersExplore400;
                else
                    return TwoTowersDamage500;
            } else {
                if (strategy == Strategy.Explore)
                    return UniformRandomExplore;
                else
                    return UniformRandomDamage;
            }
        } else if (totalCount == 3) {
            if (maxPosition < ThreeTowersMaxLength) {
                if (strategy == Strategy.Explore)
                    return ThreeTowersExplore500;
                else
                    return ThreeTowersDamage600;
            } else {
                if (strategy == Strategy.Explore)
                    return UniformRandomExplore;
                else
                    return UniformRandomDamage;
            }
        } else if (totalCount == 4) {
            if (maxPosition < FourTowersMaxLength) {
                if (strategy == Strategy.Explore)
                    return FourTowersExplore500;
                else if (strategy == Strategy.Damage)
                    return FourTowersDamage500;
                else
                    return FourTowersDamage1200;
            } else {
                if (strategy == Strategy.Explore)
                    return UniformRandomExplore;
                else
                    return UniformRandomDamage;
            }
        } else {
            if (strategy == Strategy.Explore)
                return UniformRandomExplore;
            else
                return UniformRandomDamage;
        }
    }

    private List<List<Point>> groupAdjacentPoints(List<Point> points) {
        List<List<Point>> adjacentPoints = new ArrayList<>();

        if (points.size() == 0)
            return adjacentPoints;

        adjacentPoints.add(new ArrayList<>());
        adjacentPoints.get(0).add(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            if (!IsAdjacent(points.get(i - 1), points.get(i))) {
                adjacentPoints.add(new ArrayList<>());
            }

            adjacentPoints.get(adjacentPoints.size() - 1).add(points.get(i));
        }

        return adjacentPoints;
    }

    private boolean IsAdjacent(Point p1, Point p2) {
        return (p1.getX() + 1 == p2.getX() && p1.getY() == p2.getY()) ||
                (p1.getX() - 1 == p2.getX() && p1.getY() == p2.getY()) ||
                (p1.getX() == p2.getX() && p1.getY() + 1 == p2.getY()) ||
                (p1.getX() == p2.getX() && p1.getY() - 1 == p2.getY());
    }
}

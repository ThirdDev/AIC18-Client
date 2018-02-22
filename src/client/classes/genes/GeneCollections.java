package client.classes.genes;

import client.classes.Logger;
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
    public FullStateGeneCollection FourTowersExplore200;

    public final int ThreeTowersMaxLength = 20;
    public FullStateGeneCollection ThreeTowersDamage400;
    public FullStateGeneCollection ThreeTowersDamage600;
    public FullStateGeneCollection ThreeTowersExplore200;

    // pathLength = 40
    public final int TwoDoubleTowersMaxLength = 40;
    public FullStateGeneCollection TwoDoubleTowersDamage700;
    public FullStateGeneCollection TwoDoubleTowersExplore200;

    // pathLength = 100
    public final int TwoTowersMaxLength = 100;
    public FullStateGeneCollection TwoTowersDamage275;
    public FullStateGeneCollection TwoTowersDamage500;
    public FullStateGeneCollection TwoTowersExplore200;

    public final int SingleTowerMaxLength = 100;
    public FullStateGeneCollection SingleTowerDamage200;
    public FullStateGeneCollection SingleTowerDamage500;
    public FullStateGeneCollection SingleTowerExplore150;

    public final int RandomMaxCount = 20;
    public CountStateGeneCollection RandomDamage2500;
    public CountStateGeneCollection RandomExplore1000;

    //Singleton class
    private GeneCollections() {
        long time = System.currentTimeMillis();

        FourTowersDamage500 = new FullStateGeneCollection("FourTowers-DamagePolicy 500-pathLength 20-2018-21-2--00-51-00.sgdf");
        FourTowersDamage1200 = new FullStateGeneCollection("FourTowers-DamagePolicy 1200-pathLength 20-2018-21-2--01-00-47.sgdf");
        FourTowersExplore200 = new FullStateGeneCollection("FourTowers-ExplorePolicy 200-pathLength 20-2018-21-2--00-52-30.sgdf");

        ThreeTowersDamage400 = new FullStateGeneCollection("ThreeTowers-DamagePolicy 400-pathLength 20-2018-20-2--20-49-25.sgdf");
        ThreeTowersDamage600 = new FullStateGeneCollection("ThreeTowers-DamagePolicy 600-pathLength 20-2018-20-2--20-38-47.sgdf");
        ThreeTowersExplore200 = new FullStateGeneCollection("ThreeTowers-ExplorePolicy 200-pathLength 20-2018-20-2--20-32-26.sgdf");

        TwoDoubleTowersDamage700 = new FullStateGeneCollection("TwoDoubleTowers-DamagePolicy 700-pathLength 40-2018-20-2--19-19-12.sgdf");
        TwoDoubleTowersExplore200 = new FullStateGeneCollection("TwoDoubleTowers-ExplorePolicy 200-pathLength 40-2018-20-2--19-05-53.sgdf");

        TwoTowersDamage275 = new FullStateGeneCollection("TwoTowers-DamagePolicy 275-pathLength 100-2018-21-2--22-17-54.sgdf");
        TwoTowersDamage500 = new FullStateGeneCollection("TwoTowers-DamagePolicy 500-pathLength 100-2018-21-2--13-25-39.sgdf");
        TwoTowersExplore200 = new FullStateGeneCollection("TwoTowers-ExplorePolicy 200-pathLength 100-2018-21-2--23-49-39.sgdf");

        SingleTowerDamage200 = new FullStateGeneCollection("SingleTower-DamagePolicy 200-pathLength 100-2018-21-2--17-26-28.sgdf");
        SingleTowerDamage500 = new FullStateGeneCollection("SingleTower-DamagePolicy 500-pathLength 100-2018-21-2--22-03-37.sgdf");
        SingleTowerExplore150 = new FullStateGeneCollection("SingleTower-ExplorePolicy 150-pathLength 100-2018-21-2--19-20-24.sgdf");

        RandomDamage2500 = new CountStateGeneCollection("RandomTowers-DamagePolicy 2500-pathLength 20-2018-21-2--01-47-18.sgdf");
        RandomExplore1000 = new CountStateGeneCollection("RandomTowers-ExplorePolicy 1000-pathLength 20-2018-21-2--01-47-39.sgdf");

        long delta = System.currentTimeMillis() - time;
        System.out.println(delta + " ms");
    }

    private static GeneCollections instance = null;
    public static GeneCollections getCollections() {
        if (instance == null)
            instance = new GeneCollections();

        return instance;
    }

    public Recipe getRecipe(Set<TowerDetails> towers, Path path, Strategy strategy) {
        List<Integer> cannons = new ArrayList<>();
        List<Integer> archers = new ArrayList<>();

        if (towers.size() > 0) {
            String s = "hello";
        }

        for (TowerDetails details : towers) {
            List<Point> points = details.getPointsForPath(path);
            List<List<Point>> adjacentPoints = groupAdjacentPoints(points);

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

        GeneCollection collection = FindSuitableGeneCollectionFor(cannons, archers, strategy);
        Logger.println("Will get gene from " + collection.getResourceName() + " (we have " + cannons.size() + " cannons and " + archers.size() + " archers now)");
        return collection.getRecipe(cannons.stream().mapToInt(Integer::intValue).toArray(), archers.stream().mapToInt(Integer::intValue).toArray());
    }

    @SuppressWarnings("Duplicates")
    private GeneCollection FindSuitableGeneCollectionFor(List<Integer> cannons, List<Integer> archers, Strategy strategy) {
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

        if (totalCount <= 1) {
            if (maxPosition < SingleTowerMaxLength) {
                if (strategy == Strategy.Explore)
                    return SingleTowerExplore150;
                else if (strategy == Strategy.Damage)
                    return SingleTowerDamage200;
                else
                    return SingleTowerDamage500;
            } else {
                // TODO: ????
            }
        } else if (totalCount == 2) {
            if (maxPosition < TwoTowersMaxLength) {
                if (strategy == Strategy.Explore)
                    return TwoTowersExplore200;
                else if (strategy == Strategy.Damage)
                    return TwoTowersDamage275;
                else
                    return TwoTowersDamage500;
            } else {
                // TODO: ????
            }
        } else if (totalCount == 3) {
            if (maxPosition < ThreeTowersMaxLength) {
                if (strategy == Strategy.Explore)
                    return ThreeTowersExplore200;
                else if (strategy == Strategy.Damage)
                    return ThreeTowersDamage400;
                else
                    return ThreeTowersDamage600;
            } else {
                // TODO: ????
            }
        } else if (totalCount == 4) {
            if (maxPosition < FourTowersMaxLength) {
                if (strategy == Strategy.Explore)
                    return FourTowersExplore200;
                else if (strategy == Strategy.Damage)
                    return FourTowersDamage500;
                else
                    return FourTowersDamage1200;
            } else {
                // TODO: ????
            }
        } else if (totalCount < RandomMaxCount) {
            if (strategy == Strategy.Explore)
                return RandomExplore1000;
            else
                return RandomDamage2500;
        } else {
            // TODO: ????
        }
        return null;
    }

    private List<List<Point>> groupAdjacentPoints(List<Point> points) {
        List<List<Point>> adjacentPoints = new ArrayList<>();

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

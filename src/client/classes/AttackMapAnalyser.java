package client.classes;

import client.Util;
import client.model.*;
import java.util.*;

public class AttackMapAnalyser {

    private static HashMap<Point, Set<Path>> pointToPaths;
    private static HashMap<Integer, Set<Path>> towerToPaths;
    private static HashMap<Path, Set<TowerDetails>> pathToTowers;
    private static HashSet<TowerDetails> towerDetails;

    private static int lastUpdatedTurn = -1;

    //Singleton class
    private AttackMapAnalyser() { }

    static {
        towerToPaths = new HashMap<>();
        pathToTowers = new HashMap<>();
        towerDetails = new HashSet<>();
    }

    public static Set<Path> pathsContainingThisRoadCell(World game, Cell c) {
        initializePointPaths(game);

        if (pointToPaths.containsKey(c.getLocation()))
            return pointToPaths.get(c.getLocation());
        else
            return null;
    }

    private static void initializePointPaths(World game) {
        if (pointToPaths != null)
            return;

        pointToPaths = new HashMap<>();

        for (Path p : game.getAttackMapPaths()) {
            for (Cell c : p.getRoad()) {
                if (!pointToPaths.containsKey(c.getLocation()))
                    pointToPaths.put(c.getLocation(), new HashSet<>());
                pointToPaths.get(c.getLocation()).add(p);
            }
        }
    }

    public static List<Tower> getVisibleTowersForPath(World game, Path path) {
        updateNewTowers(game);
        removeDestroyedTowers(game);

        List<Tower> towers = new ArrayList<>();
        for (TowerDetails td : pathToTowers.get(path)) {
            towers.add(getEnemyTowerById(game, td.getId()));
        }
        return towers;
    }

    public static Set<TowerDetails> getVisibleTowerDetailsForPath(World game, Path path) {
        updateNewTowers(game);
        removeDestroyedTowers(game);

        if (pathToTowers.containsKey(path))
            return pathToTowers.get(path);
        else
            return new HashSet<>();
    }

    private static void removeDestroyedTowers(World game) {
        //TODO
    }

    private static void updateNewTowers(World game) {
        if (game.getCurrentTurn() == lastUpdatedTurn)
            return;

        for (Tower t : game.getVisibleEnemyTowers()) {
            if (!towerToPaths.containsKey(t.getId())) {
                TowerDetails details = findPathsForTower(game, t);
                towerToPaths.put(t.getId(), details.getPaths());

                for (Path p : details.getPaths()) {
                    if (!pathToTowers.containsKey(p))
                        pathToTowers.put(p, new HashSet<TowerDetails>());

                    pathToTowers.get(p).add(details);
                }
            }
        }

        lastUpdatedTurn = game.getCurrentTurn();
    }

    private static TowerDetails findPathsForTower(World game, Tower t) {
        initializePointPaths(game);

        TowerDetails td = getTowerDetails(t);

        if (td == null) {
            td = new TowerDetails(t.getId(), t instanceof ArcherTower);
            towerDetails.add(td);
        }

        List<Cell> potentialCells = Util.radialCells(t.getLocation(), 2, game.getAttackMap());

        for (Path path : game.getAttackMapPaths()) {
            List<Point> confluencePoints = new ArrayList<>();

            for (Cell roadCell : path.getRoad()) {
                for (Cell potentialCell : potentialCells) {
                    if (potentialCell.getLocation().equals(roadCell.getLocation())) {
                        confluencePoints.add(roadCell.getLocation());
                    }
                }
            }

            if (confluencePoints.size() > 0)
                td.setConfluenceForPath(path, confluencePoints);
        }

        return td;
    }

    public static TowerDetails getTowerDetails(Tower tower) {
        return getTowerDetails(tower.getId());
    }

    public static TowerDetails getTowerDetails(int id) {
        return towerDetails.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    private static Tower getEnemyTowerById(World game, int id) {
        for (Tower t : game.getVisibleEnemyTowers())
            if (t.getId() == id)
                return t;
        return null;
    }

}

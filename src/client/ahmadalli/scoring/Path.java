package client.ahmadalli.scoring;

import client.Util;
import client.model.GrassCell;
import client.model.Map;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class Path {

    private static HashMap<client.model.Path, Double> towerCoverageScoreCache;

    public static void cacheTowerCoverageScore(ArrayList<client.model.Path> paths, Map map) {
        towerCoverageScoreCache = new HashMap<>();
        for (client.model.Path path : paths) {
            towerCoverageScoreCache.put(path, towerCoverageScore(path, map));
        }
    }

    public static double towerCoverageScoreCached(client.model.Path path) {
        return towerCoverageScoreCache.get(path);
    }

    public static double towerCoverageScore(client.model.Path path, Map map) {
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
}

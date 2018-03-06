package client.ahmadalli.scoring;

import client.Util;
import client.model.GrassCell;
import client.model.Map;

public class Path {
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

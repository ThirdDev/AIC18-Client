package client.ahmadalli.scoring;

import client.Util;
import client.model.*;
import client.model.Path;

import java.util.ArrayList;

public class Cell {
    public static double defendScore(client.model.Cell cell, Map map, ArrayList<Path> paths) {
        double nearbyCellsScore = client.ahmadalli.utils.Cell.getNearbyRoadCells(cell, map).
                mapToDouble(x -> client.ahmadalli.scoring.RoadCell.unitScore(x) + 1).sum();

        double towerScore = 0;
        if (cell instanceof GrassCell) {
            Tower tower = ((GrassCell) cell).getTower();
            if (tower != null) {
                towerScore = (-tower.getLevel() * 30);
            }
        }

        double pathScore = 0;

        /**/
        pathScore = client.ahmadalli.utils.Cell.getNearbyPaths(cell, 2, paths, map)
                .mapToDouble(path -> client.ahmadalli.scoring.Path.towerCoverageScore(path, map))
                .sum();
        /**/

        double finalScore = nearbyCellsScore + towerScore + pathScore;

        return finalScore;
    }


    public static int stormCoveredDamageScore(client.model.Cell cell, Map map, int stormRange) {
        return Util.radialCells(cell, stormRange, map).stream()
                .filter(x -> x instanceof client.model.RoadCell)
                .map(x -> (client.model.RoadCell) x)
                .mapToInt(x -> RoadCell.damageToBaseScore(x))
                .sum();
    }
}

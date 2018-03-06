package client.ahmadalli.utils;

import client.Util;
import client.model.*;
import client.model.Cell;

import java.util.ArrayList;

public class RoadCell {
    public static int creepCount(client.model.RoadCell roadCell) {
        return (int) roadCell.getUnits().stream().filter(x -> x instanceof LightUnit).count();
    }

    public static int heroCount(client.model.RoadCell roadCell) {
        return (int) roadCell.getUnits().stream().filter(x -> x instanceof HeavyUnit).count();
    }

    public static Cell centerOfMostDangerousAreaContainingRoadCell(client.model.RoadCell roadCell, Map map, int stormRange) {
        return Util.radialCells(roadCell, stormRange, map).stream()
                .sorted(client.ahmadalli.comparator.Cell.byStormTotalPreventedDanger(map, stormRange))
                .findFirst()
                .get();
    }

    public static client.model.RoadCell[] orderByDamageScoreAscending(ArrayList<Path> paths, double portion, int minCount, int maxCount) {
        return paths.stream()
                .flatMap(x -> endingRoadCells(x, portion, minCount, maxCount).stream())
                .filter(x -> client.ahmadalli.scoring.RoadCell.damageToBaseScore(x) > 0)
                .sorted(client.ahmadalli.comparator.RoadCell.byDamageToBaseScore())
                .toArray(client.model.RoadCell[]::new);
    }

    public static ArrayList<client.model.RoadCell> endingRoadCells(Path path, double portion, int minCount, int maxCount) {
        ArrayList<client.model.RoadCell> roeadCells = path.getRoad();

        int portionCount = (int) (roeadCells.size() * portion);
        int count = Math.max(minCount, portionCount);
        count = Math.min(maxCount, count);
        count = Math.min(roeadCells.size(), count);

        ArrayList<client.model.RoadCell> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(roeadCells.get(roeadCells.size() - 1 - i));
        }

        return result;

    }
}

package client.ahmadalli.comparator;

import client.model.Map;
import client.model.Path;

import java.util.ArrayList;
import java.util.Comparator;

public class Cell {
    public static Comparator<client.model.Cell> byDefendScore(Map map, ArrayList<Path> paths) {
        return Comparator.comparingDouble(o -> -client.ahmadalli.scoring.Cell.defendScore(o, map, paths));
    }

    public static Comparator<client.model.Cell> byStormTotalPreventedDanger(Map map, int stormRange) {
        return Comparator.comparingDouble(o ->
                -client.ahmadalli.scoring.Cell.stormCoveredDamageScore(o, map, stormRange)
        );
    }
}

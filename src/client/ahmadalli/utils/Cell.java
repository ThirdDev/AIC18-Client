package client.ahmadalli.utils;

import client.Util;
import client.model.*;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Cell {
    public static boolean hasTowerBesideOfIt(client.model.Cell cell, World world) {
        return Util.radialCells(cell, 1, world.getDefenceMap()).stream()
                .filter(x -> x instanceof GrassCell)
                .map(x -> (GrassCell) x)
                .anyMatch(x -> !x.isEmpty());
    }

    public static Stream<client.model.RoadCell> getNearbyRoadCells(client.model.Cell cell, Map map) {
        return Util.radialCells(cell, 2, map).stream()
                .filter(x -> x instanceof client.model.RoadCell)
                .map(x -> (client.model.RoadCell) x);
    }

    public static Stream<client.model.Path> getNearbyPaths(client.model.Cell cell, int range, ArrayList<client.model.Path> paths, Map map) {
        return paths.stream()
                .filter(x -> Stream.concat(x.getRoad().stream()
                                .flatMap(y -> Util.radialCells(y, range, map).stream()),
                        x.getRoad().stream())
                        .anyMatch(y -> y.equals(cell)));
    }
}

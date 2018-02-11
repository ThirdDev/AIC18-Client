package client.classes;

import client.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RoadInfo {
    ArrayList<RoadCell> roadCells;
    int[] heatMap;
    HashMap<Integer, Integer> units = new HashMap<>();

    public RoadInfo(Path path) {
        roadCells = path.getRoad();
        heatMap = new int[roadCells.size()];

    }

    public void update(Path path) {
        System.out.println();
        System.out.println();

        System.out.print("heatMap: ");
        for (int x : heatMap)
            System.out.print(x + ", ");
        System.out.println();


        System.out.print("road: ");
        for (RoadCell x : path.getRoad())
            System.out.print(x.getUnits().size() + ", ");
        System.out.println();
        System.out.println();
        System.out.println();

        // Add new units
        for (Unit u : path.getRoad().get(0).getUnits()) {
            if (!units.containsKey(u.getId())) {
                units.put(u.getId(), 0);
            }
        }

        HashSet<Integer> presentUnits = new HashSet<>();
        for (int i = 0; i < path.getRoad().size(); i++) {
            RoadCell cell = path.getRoad().get(i);
            for (Unit u : cell.getUnits()) {
                presentUnits.add(u.getId());

                if (units.containsKey(u.getId())) {
                    units.put(u.getId(), i);
                }
                else {
                    System.out.println("Wtf! soldiers reproduced during this turn :|");
                }
            }
        }

        ArrayList<Integer> keysToRemove = new ArrayList<>();
        for (Integer key : units.keySet()) {
            if (!presentUnits.contains(key)) {
                System.out.println("unit " + key + " has been killed. it's life span was " + units.get(key) + "... Rest in peace!");
                heatMap[units.get(key)]++;
                keysToRemove.add(key);
            }
        }

        for (Integer key : keysToRemove) {
            units.remove(key);
        }
    }

    // 2 3 4
    // 1 2 3 3
    // 1 1 2 2 0

    public double getHeatMapScore() {
        int sum = 0;
        int total = 0;
        for (int i = 0; i < heatMap.length; i++) {
            total += (i + 1) * heatMap[i];
            sum += heatMap[i];
        }

        return (((double)total) / ((double)sum)) / ((double)heatMap.length);
    }
}

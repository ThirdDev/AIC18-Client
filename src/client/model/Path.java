package client.model;

import client.Util;
import javafx.geometry.Side;

import java.util.ArrayList;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Path {

    private ArrayList<RoadCell> road;
    private ArrayList<SideWayCell> sideWayCells;

    public Path(ArrayList<RoadCell> road) {
        this.road = road;
    }

    public ArrayList<RoadCell> getRoad() {
        return road;
    }

    public ArrayList<SideWayCell> getSideWayCells() {
        return sideWayCells;
    }

    public void addSideWayCell(SideWayCell sideWayCell) {
        this.sideWayCells.add(sideWayCell);
    }

    @Override
    public String toString() {
        String result = "Path: ";

        for (int i = 0; i < road.size(); i++) {
            result += "(" + road.get(i).getLocation().getX() + "," + road.get(i).getLocation().getY() + ")" + " ";
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public int getPointIndex(Point p) {
        for (int i = 0; i < road.size(); i++)
            if (road.get(i).getLocation().equals(p))
                return i;
        return -1;
    }
}

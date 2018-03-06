package client.model;

import java.util.ArrayList;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class RoadCell extends Cell {

    private ArrayList<Unit> units = new ArrayList<>();

    public RoadCell(int x, int y) {
        super(x, y);
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    @Override
    public String toString() {
        return super.toString() + " r ";
    }
}

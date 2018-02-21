package client.model;

import java.util.ArrayList;

public class SideWayCell extends Cell {

    ArrayList<Path> paths;
    ArrayList<RoadCell> roadCells;

    public SideWayCell(int x, int y) {
        super(x, y);
        paths = new ArrayList<>();
        roadCells = new ArrayList<>();
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public ArrayList<RoadCell> getRoadCells() {
        return roadCells;
    }

    public void addPaths(Path path) {
        this.paths.add(path);
    }

    public void addRoadCells(RoadCell roadCell) {
        this.roadCells.add(roadCell);
    }
}

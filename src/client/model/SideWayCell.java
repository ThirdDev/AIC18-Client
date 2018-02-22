package client.model;

import java.util.ArrayList;

public class SideWayCell extends GrassCell implements Comparable {

    ArrayList<Path> paths;
    ArrayList<RoadCell> roadCells;

    public SideWayCell(int x, int y, Tower tower) {
        super(x, y, tower);
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

    @Override
    public int compareTo(Object o) {
        SideWayCell oo = (SideWayCell) o;
        int ans = roadCells.size() - oo.getRoadCells().size();
        if(ans == 0) ans = paths.size() - oo.getPaths().size();
        return ans;
    }


}

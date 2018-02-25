package client.model;

import java.util.ArrayList;

public class SideWayCell extends GrassCell implements Comparable {

    private ArrayList<Path> paths;
    private ArrayList<RoadCell> roadCells;
    private int color;

    public SideWayCell(int x, int y, Tower tower) {
        super(x, y, tower);
        paths = new ArrayList<>();
        roadCells = new ArrayList<>();
        color = -1;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public ArrayList<RoadCell> getRoadCells() {
        return roadCells;
    }

    public void addPath(Path path) {
        this.paths.add(path);
    }

    public void addRoadCell(RoadCell roadCell) {
        this.roadCells.add(roadCell);
    }

    @Override
    public int compareTo(Object o) {
        SideWayCell oo = (SideWayCell) o;
        int ans = oo.getRoadCells().size()-roadCells.size() ;
        if(ans == 0) ans = paths.size() - oo.getPaths().size();
        if(ans == 0){
            Tower oTower = oo.getTower();
            Tower ourTower = this.getTower();
            int oTowerLevel = (oTower == null) ? 0 : oTower.getLevel();
            int ourLevel = (ourTower == null) ? 0 : ourTower.getLevel();
            ans = ourLevel - oTowerLevel;
        }
        return ans;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

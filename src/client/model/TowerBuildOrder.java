package client.model;

public class TowerBuildOrder {
    private Point point;
    private int level;

    public TowerBuildOrder(Point point, int level) {
        this.point = point;
        this.level = level;
    }

    public Point getPoint() {
        return point;
    }

    public int getLevel() {
        return level;
    }

    public void addLevel(){
        level++;
    }
}

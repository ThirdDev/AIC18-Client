package client.model;



public class TowerBuildOrder {

    public enum TowerType{
        Cannon, Archer
    }

    private Point point;
    private int level;
    private TowerType towerType;

    public TowerBuildOrder(Point point, int level, TowerType towerType) {
        this.point = point;
        this.level = level;
        this.towerType = towerType;
    }

    public Point getPoint() {
        return point;
    }

    public TowerType getTowerType() {
        return towerType;
    }

    public int getLevel() {
        return level;
    }

    public void addLevel(){
        level++;
    }
}

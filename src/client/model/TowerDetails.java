package client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

//This should be unique across turns
public class TowerDetails {
    int id;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    boolean isArcher;


    public TowerDetails(int id, boolean isArcher) {
        this.id = id;

        if (isArcher)
            this.isArcher = true;
        else
            this.isArcher = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TowerDetails))
            return false;

        return ((TowerDetails)obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    public boolean isArcher() {
        return isArcher;
    }

    public boolean isCannon() {
        return !isArcher;
    }

    HashMap<Path, List<Point>> pathPoints = new HashMap<>();

    public List<Point> getPointsForPath(Path p) {
        if (!pathPoints.containsKey(p))
            return null;

        return pathPoints.get(p);
    }

    public void setConfluenceForPath(Path p, List<Point> points) {
        pathPoints.put(p, points);
    }

    public Set<Path> getPaths() {
        return pathPoints.keySet();
    }
}

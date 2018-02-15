package client.model;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Cell {

    private Point location;

    public Cell(int x, int y) {
        location = new Point(x, y);

    }
    public Point getLocation() {
        return location;
    }

}

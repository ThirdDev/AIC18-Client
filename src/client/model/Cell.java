package client.model;

import java.util.Objects;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Cell {

    private Point location;

    public Cell(int x, int y) {
        location = new Point(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return getLocation().hashCode();
    }


    public Point getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "location=" + location +
                '}';
    }
}

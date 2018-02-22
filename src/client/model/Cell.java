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
        Cell cell = (Cell) o;
        return (location.getX() == cell.getLocation().getX() &
            location.getY() == cell.getLocation().getY());
    }


    public Point getLocation() {
        return location;
    }

}

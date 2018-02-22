package client.model;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point))
            return false;

        return (((Point)obj).getX() == getX()) && (((Point)obj).getY() == getY());
    }
}

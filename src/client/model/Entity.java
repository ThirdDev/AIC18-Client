package client.model;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Entity {

    private Point location;
    private Owner owner;
    private int id;

    public Entity(int x, int y, Owner owner, int id) {

        location = new Point(x, y);
        this.owner = owner;
        this.id = id;

    }

    public Point getLocation() {
        return location;
    }

    public Owner getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return " @ x:" + location.getX() + " y:" + location.getY() +
                " Owner: " + owner.toString() + " Id:" + id;
    }
}

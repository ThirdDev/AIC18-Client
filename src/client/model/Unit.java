package client.model;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Unit extends Entity {

    private int health;
    private int level;

    private Path path;

    public Unit(int x, int y, Owner owner, int level, int id, int health, Path path) {
        super(x, y, owner, id);
        this.level = level;
        this.health = health;
        this.path = path;
    }

    public int getMoveSpeed() {
        return 0;
    }

    public int getPrice(int level) {
        return 0;
    }

    public int getPrice() {
        return 0;
    }

    public int getBounty(int level) {
        return 0;
    }

    public int getBounty() {
        return 0;
    }

    public int getDamage() {
        return 0;
    }

    public int getVisionRange() {
        return 0;
    }

    public int getLevel() {
        return this.level;
    }

    public int getHealth() {
        return this.health;
    }

    public Path getPath() {
        return this.path;
    }

    public int getAddedIncome() {
        return 0;
    }


    @Override
    public String toString() {
        return "Unit:: " + "health:" + health + " price:" + getPrice() + " damage:" + getDamage()
                + " moveSpeed:" + getMoveSpeed() + " visionRange:" + getVisionRange()
                + " Income:" + getAddedIncome() + " bounty:" + getBounty() + super.toString();
    }
}

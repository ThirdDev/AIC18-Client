package client.model;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class LightUnit extends Unit {

    public static int INITIAL_HEALTH;
    public static double HEALTH_COEFF;
    public static int INITIAL_PRICE;
    public static int PRICE_INCREASE;
    public static int INITIAL_BOUNTY;
    public static int BOUNTY_INCREASE;
    public static int MOVE_SPEED;
    public static int DAMAGE;
    public static int VISION_RANGE;
    public static int LEVEL_UP_THRESHOLD;
    public static int ADDED_INCOME;

    public LightUnit(int x, int y, Owner owner, int level, int id,int health,Path path) {
        super(x, y, owner, level, id,health,path);
    }

    public int getMoveSpeed() {
        return MOVE_SPEED;
    }

    public int getPrice(int level) {
        return PRICE_INCREASE * (level - 1) + INITIAL_PRICE;
    }

    public int getPrice() {
        return this.getPrice(this.getLevel());
    }

    public int getBounty(int level) {
        return INITIAL_BOUNTY + BOUNTY_INCREASE * (level - 1);
    }

    public int getBounty() {
        return this.getBounty(this.getLevel());
    }

    public int getDamage() {
        return DAMAGE;
    }

    public int getVisionRange() {
        return VISION_RANGE;
    }

    public int getAddedIncome() {
        return ADDED_INCOME;
    }

    @Override
    public String toString() {
        return "Light" + super.toString();
    }
}

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

    public LightUnit(int x, int y, Owner owner, int level, int id, int health, Path path) {
        super(x, y, owner, level, id, health, path);
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

    public int getMaxHealth(){ return (int)(Math.floor(((double)INITIAL_HEALTH) * Math.pow(HEALTH_COEFF,(double)getLevel()-1)));}

    @Override
    public String toString() {
        return "Light" + super.toString();
    }


    static int createdUnits = 0;

    public static int getCurrentPrice() {
        return INITIAL_PRICE + PRICE_INCREASE * (int)(Math.floor(createdUnits / 60));
    }

    public static int getCurrentPrice(int count) {
        int totalPrice = 0;
        int createdUnitsTemp = createdUnits;

        for (int i = 0; i < count; i++) {
            totalPrice += INITIAL_PRICE + PRICE_INCREASE * (int)(Math.floor(createdUnitsTemp / 60));
            createdUnitsTemp++;
        }

        return totalPrice;
    }

    public static void createdUnit() {
        createdUnits++;
    }
}

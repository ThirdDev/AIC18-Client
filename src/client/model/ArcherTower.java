package client.model;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class ArcherTower extends Tower {

    public static int INITIAL_PRICE;
    public static int INITIAL_LEVEL_UP_PRICE;
    public static double PRICE_COEFF;
    public static int INITIAL_DAMAGE;
    public static double DAMAGE_COEFF;
    public static int ATTACK_SPEED;
    public static int ATTACK_RANGE;

    public ArcherTower(int x, int y, Owner owner, int level, int id) {
        super(x, y, owner, level, id);
    }

    public int getDamage(int level) {
        return (int) (INITIAL_DAMAGE * Math.pow(DAMAGE_COEFF, level - 1));
    }

    public int getDamage() {
        return this.getDamage(this.getLevel());
    }

    public int getPrice(int level) {
        int result = INITIAL_PRICE;
        for (int i = 2; i <= level; i++)
            result += INITIAL_LEVEL_UP_PRICE * Math.pow(PRICE_COEFF, i - 2);
        return result;
    }

    public int getPrice() {
        return this.getPrice(this.getLevel());
    }

    public int getAttackRange() {
        return ATTACK_RANGE;
    }

    public int getAttackSpeed() {
        return ATTACK_SPEED;
    }

    @Override
    public String toString() {
        return "Archer" + super.toString();
    }
}

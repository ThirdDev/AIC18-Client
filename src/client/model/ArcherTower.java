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
    public static int INITIAL_PRICE_INCREASE;
    public static int numberOfTowers = 0;

    public ArcherTower(int x, int y, Owner owner, int level, int id,int price) {
        super(x, y, owner, level, id,price);
    }

    public int getDamage(int level) {
        return getTowerDamage(level);
    }

    public static int getTowerDamage(int level) {
        return (int) (INITIAL_DAMAGE * Math.pow(DAMAGE_COEFF, level - 1));
    }

    public int getDamage() {
        return this.getDamage(this.getLevel());
    }

    public static int getPrice(int level) {
        int result = INITIAL_PRICE + (5 * numberOfTowers);
        for (int i = 2; i <= level; i++)
            result += INITIAL_LEVEL_UP_PRICE * Math.pow(PRICE_COEFF, i - 2);
        return result;
    }

    public int getPrice() {
        return ArcherTower.getPrice(this.getLevel());
    }

    public int getAttackRange() {
        return ATTACK_RANGE;
    }

    public int getAttackSpeed() {
        return ATTACK_SPEED;
    }

    public static void addTower(){
        numberOfTowers++;
    }

    @Override
    public String toString() {
        return "Archer" + super.toString();
    }
}

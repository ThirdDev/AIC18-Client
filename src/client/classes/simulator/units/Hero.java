package client.classes.simulator.units;

public class Hero extends Unit {

    @Override
    public int getMoveCycle() {
        return 3;
    }

    int health = 240;

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getDamageByCannon() {
        return 10;
    }

    @Override
    public int getDamageByArcher() {
        return 40;
    }

    @Override
    public int getPrice() {
        return 180;
    }

    @Override
    public int getDamageToEnemyBase() {
        return 5;
    }
}

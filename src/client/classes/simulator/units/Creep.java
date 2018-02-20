package client.classes.simulator.units;

public class Creep extends Unit {
    @Override
    public int getMoveCycle() {
        return 2;
    }

    int health = 32;

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
        return 40;
    }

    @Override
    public int getDamageToEnemyBase() {
        return 1;
    }
}

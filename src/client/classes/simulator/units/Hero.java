package client.classes.simulator.units;

import client.model.ArcherTower;
import client.model.CannonTower;

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
        return CannonTower.INITIAL_DAMAGE;
    }

    @Override
    public int getDamageByArcher() {
        return ArcherTower.INITIAL_DAMAGE;
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

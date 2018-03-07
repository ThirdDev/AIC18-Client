package client.classes.simulator.towers;

import client.model.ArcherTower;

public class Archer extends Tower {

    int[] rangeDelta;

    @Override
    public int getRechargeTime() {
        return ArcherTower.ATTACK_SPEED;
    }

    @Override
    public int[] getRangeDelta() {
        return rangeDelta;
    }

    public Archer(int position, int[] rangeDelta) {
        setPosition(position);
        this.rangeDelta = rangeDelta;
    }
}

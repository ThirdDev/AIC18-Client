package client.classes.simulator.towers;

import client.model.CannonTower;

public class Cannon extends Tower {

    int[] rangeDelta;

    @Override
    public int getRechargeTime() {
        return CannonTower.ATTACK_SPEED;
    }

    @Override
    public int[] getRangeDelta() {
        return rangeDelta;
    }

    public Cannon(int position, int[] rangeDelta) {
        setPosition(position);
        this.rangeDelta = rangeDelta;
    }
}

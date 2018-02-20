package client.classes.simulator.towers;

public class Cannon extends Tower {

    int[] rangeDelta;

    @Override
    public int getRechargeTime() {
        return 4;
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

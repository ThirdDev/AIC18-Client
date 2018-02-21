package client.classes.simulator.towers;

public class Archer extends Tower {

    int[] rangeDelta;

    @Override
    public int getRechargeTime() {
        return 3;
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

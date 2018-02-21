package client.classes.simulator.towers;

public abstract class Tower {

    public abstract int getRechargeTime();

    public abstract int[] getRangeDelta();

    int rechargeCounter = -1;

    public int getRechargeCounter() {
        return rechargeCounter;
    }

    private void setRechargeCounter(int c) {
        this.rechargeCounter = c;
    }


    int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public boolean canAttack() {
        return (getRechargeCounter() == -1) || (getRechargeCounter() % getRechargeTime() == 0);
    }

    public void attack() {
        setRechargeCounter(0);
    }

    public void turnPassed() {
        if (getRechargeCounter() == -1)
            return;

        setRechargeCounter(getRechargeCounter() + 1);
    }

    public boolean isInRange(int position) {
        for (int i : getRangeDelta())
            if (position == (getPosition() + i))
                return true;

        return false;
    }

    public void reset() {
        setRechargeCounter(-1);
    }
}

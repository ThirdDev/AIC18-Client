package client.classes.simulator.units;

public abstract class Unit {
    public int turn;
    public int position;

    //abstract
    public abstract int getMoveCycle();

    public abstract int getHealth();

    public abstract void setHealth(int health);

    public abstract int getDamageByCannon();

    public abstract int getDamageByArcher();

    public abstract int getPrice();

    public abstract int getDamageToEnemyBase();


    /// <summary>
    /// Must be called each turn
    /// </summary>
    /// <returns>New position</returns>
    public int goForward() {
        turn++;
        if (turn % getMoveCycle() == 0)
            position++;
        return position;
    }

    public void getAttackedByCannon() {
        setHealth(getHealth() - getDamageByCannon());
    }

    public void getAttackedByArcher() {
        setHealth(getHealth() - getDamageByArcher());
    }
}
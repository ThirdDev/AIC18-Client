package client.classes.simulator;

public class AttackAction {
    int countOfCreeps;
    int countOfHeros;

    public int getCountOfCreeps() {
        return countOfCreeps;
    }

    public int getCountOfHeros() {
        return countOfHeros;
    }

    public AttackAction(int countOfCreeps, int countOfHeros) {
        this.countOfCreeps = countOfCreeps;
        this.countOfHeros = countOfHeros;
    }
}

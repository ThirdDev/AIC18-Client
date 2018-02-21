package client.classes.genes;

public class Recipe {
    byte[] creeps, archers;

    public Recipe(byte[] creeps, byte[] archers) {
        this.creeps = creeps;
        this.archers = archers;
    }

    public byte[] getCreeps() {
        return creeps;
    }

    public byte[] getArchers() {
        return archers;
    }
}

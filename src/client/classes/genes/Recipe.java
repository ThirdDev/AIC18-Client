package client.classes.genes;

public class Recipe {
    byte[] creeps, heros;

    public Recipe(byte[] creeps, byte[] heros) {
        this.creeps = creeps;
        this.heros = heros;
    }

    public byte[] getCreeps() {
        return creeps;
    }

    public byte[] getHeros() {
        return heros;
    }
}

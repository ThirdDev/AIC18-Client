package client.classes.genes;

import client.model.HeavyUnit;
import client.model.LightUnit;

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

    public int getTotalCost() {
        int totalPrice = 0;

        for (byte b : creeps)
            totalPrice += LightUnit.getCurrentPrice(b);
        for (byte b : heros)
            totalPrice += HeavyUnit.getCurrentPrice(b);

        return totalPrice;
    }
}

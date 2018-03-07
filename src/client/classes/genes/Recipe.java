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

    public void repeat(int times) {
        byte[] newCreeps = new byte[creeps.length * times];
        byte[] newHeros = new byte[heros.length * times];

        for (int i = 0; i < creeps.length; i++)
            for (int j = 0; j < times; j++)
                newCreeps[i + j * creeps.length] = creeps[i];

        for (int i = 0; i < heros.length; i++)
            for (int j = 0; j < times; j++)
                newHeros[i + j * heros.length] = heros[i];

        creeps = newCreeps;
        heros = newHeros;
    }
}

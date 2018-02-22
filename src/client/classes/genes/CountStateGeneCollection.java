package client.classes.genes;

import client.classes.Logger;
import client.model.ArcherTower;
import client.model.CannonTower;
import client.model.Tower;

import java.util.Arrays;
import java.util.List;

public class CountStateGeneCollection extends GeneCollection {

    public CountStateGeneCollection(String resourceName) {
        super(resourceName);
    }

    @Override
    public Recipe getRecipe(int[] cannons, int[] archers) {
        return getRecipe(cannons.length, archers.length);
    }

    private Recipe getRecipe(int cannonsCount, int archersCount) {
        String key = cannonsCount + "," + archersCount;

        if (!data.containsKey(key)) {
            Logger.error("Can't find key " + key + " in CountStateGeneCollection of " + resourceName);
            return null;
        }

        byte[][] item = data.get(key);
        return new Recipe(item[0], item[1]);
    }
}

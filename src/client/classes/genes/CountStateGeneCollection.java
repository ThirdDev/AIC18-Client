package client.classes.genes;

import java.util.Arrays;

public class CountStateGeneCollection extends GeneCollection {

    public CountStateGeneCollection(String resourceName) {
        super(resourceName);
    }

    public Recipe getRecipe(int creepsCount, int archersCount) {
        String key = creepsCount + "," + archersCount;

        if (!data.containsKey(key))
            return null;

        byte[][] item = data.get(key);
        return new Recipe(item[0], item[1]);
    }
}

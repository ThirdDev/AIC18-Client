package client.classes.genes;

import java.util.Arrays;

public class FullStateGeneCollection extends GeneCollection {

    public FullStateGeneCollection(String resourceName) {
        super(resourceName);
    }

    public Recipe getRecipe(int[] creeps, int[] archers) {
        Arrays.sort(creeps);
        Arrays.sort(archers);

        String key = String.join("-", Arrays.stream(creeps).sorted().mapToObj(String::valueOf).toArray(String[]::new))
                + "," + String.join("-", Arrays.stream(archers).sorted().mapToObj(String::valueOf).toArray(String[]::new));

        if (!data.containsKey(key))
            return null;

        byte[][] item = data.get(key);

        return new Recipe(item[0], item[1]);
    }

}

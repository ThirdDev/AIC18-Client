package client.classes.genes;

import client.classes.Logger;
import client.model.*;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FullStateGeneCollection extends GeneCollection {

    String resourceName;

    public FullStateGeneCollection(String resourceName) {
        super(resourceName);
        this.resourceName = resourceName;
    }

    @Override
    public Recipe getRecipe(int[] cannons, int[] archers) {
        Arrays.sort(cannons);
        Arrays.sort(archers);

        String key = String.join("-", Arrays.stream(cannons).sorted().mapToObj(String::valueOf).toArray(String[]::new))
                + "," + String.join("-", Arrays.stream(archers).sorted().mapToObj(String::valueOf).toArray(String[]::new));

        if (!data.containsKey(key)) {
            Logger.error("Can't find key " + key + " in CountStateGeneCollection of " + resourceName);
            return null;
        }

        byte[][] item = data.get(key);

        return new Recipe(item[0], item[1]);
    }

}

package client.classes.genes;

import client.classes.Logger;
import client.model.*;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.*;

public class FullStateGeneCollection implements GeneCollection {

    HashMap<String, byte[][]> data;
    String resourceName;
    double multiplier = 1.0;

    public FullStateGeneCollection(String resourceName) {
        try {
            Logger.println(resourceName);
            this.resourceName = resourceName;

            data = new HashMap<>();

            InputStream inputStream = this.getClass().getResourceAsStream("/datafiles/" + resourceName);
            InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
            String[] lines = new BufferedReader(streamReader).lines().toArray(String[]::new);

            for (int i = 0; i < lines.length; i += 3) {
                String key = lines[i];
                String[] creepsString = lines[i + 1].split(",");
                String[] herosString = lines[i + 2].split(",");

                byte[] creeps = new byte[creepsString.length];
                byte[] heros = new byte[herosString.length];

                if (lines[i + 1].length() > 0)
                    for (int j = 0; j < creepsString.length; j++)
                        creeps[j] = Byte.parseByte(creepsString[j]);
                if (lines[i + 2].length() > 0)
                    for (int j = 0; j < herosString.length; j++)
                        heros[j] = Byte.parseByte(herosString[j]);

                byte[][] val = new byte[][] { creeps, heros };

                data.put(key, val);
            }
        } catch (UnsupportedEncodingException e) {
            Logger.println("Can't load resource " + resourceName);
            e.printStackTrace();
        }
    }

    @Override
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void setTowerLevelAverage(double towerLevelAverage) {
        // TODO
    }

    @Override
    public void setCreepLevel(int level) {
        // TODO
    }

    @Override
    public void setHeroLevel(int level) {
        // TODO
    }

    public String getResourceName() {
        return resourceName;
    }

    public Collection<byte[][]> getAllValues() {
        return data.values();
    }

    public Recipe getRecipe(int[] cannons, int[] archers) {
        Arrays.sort(cannons);
        Arrays.sort(archers);

        String key = String.join("-", Arrays.stream(cannons).sorted().mapToObj(String::valueOf).toArray(String[]::new))
                + "," + String.join("-", Arrays.stream(archers).sorted().mapToObj(String::valueOf).toArray(String[]::new));

        Logger.println("looking for key " + key);
        if (!data.containsKey(key)) {
            Logger.error("Can't find key " + key + " in CountStateGeneCollection of " + resourceName);
            return null;
        }

        byte[][] item = data.get(key);

        byte[] creeps = new byte[item[0].length];
        byte[] heros = new byte[item[1].length];

        for (int i = 0; i < creeps.length; i++)
            creeps[i] = (byte)(item[0][i] * multiplier);
        for (int i = 0; i < creeps.length; i++)
            heros[i] = (byte)(item[1][i] * multiplier);

        return new Recipe(creeps, heros);
    }

}

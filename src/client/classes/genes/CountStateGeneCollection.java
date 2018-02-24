package client.classes.genes;

import client.classes.Logger;
import client.classes.simulator.Simulator;
import client.classes.simulator.judges.AttackJudge;
import client.classes.simulator.judges.Judge;
import client.model.ArcherTower;
import client.model.CannonTower;
import client.model.Tower;

import java.io.*;
import java.util.*;

public class CountStateGeneCollection implements GeneCollection {


    HashMap<String, List<byte[][]>> data;
    String resourceName;
    Simulator simulator;
    int timeout;

    public CountStateGeneCollection(String resourceName) {
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

                if (!data.containsKey(key))
                    data.put(key, new ArrayList<>());

                data.get(key).add(val);

            }
        } catch (UnsupportedEncodingException e) {
            Logger.println("Can't load resource " + resourceName);
            e.printStackTrace();
        }
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Recipe getRecipe(int[] cannons, int[] archers) {
        return getRecipe(cannons.length, archers.length);
    }


    private Recipe getRecipe(int cannonsCount, int archersCount) {
        String key = cannonsCount + "," + archersCount;

        if (!data.containsKey(key)) {
            Logger.error("Can't find key " + key + " in CountStateGeneCollection of " + resourceName);
            return null;
        }

        //Logger.println("Simulating...");
        List<byte[][]> genes = data.get(key);
        Judge judge = new AttackJudge();
        byte[][] bestGene = Simulator.findBestGene(genes, simulator, judge, timeout);

        return new Recipe(bestGene[0], bestGene[1]);
    }
}

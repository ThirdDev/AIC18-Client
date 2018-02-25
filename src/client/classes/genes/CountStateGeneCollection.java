package client.classes.genes;

import client.classes.Logger;
import client.classes.simulator.Simulator;
import client.classes.simulator.judges.AttackJudge;
import client.classes.simulator.judges.Judge;

import java.io.*;
import java.util.*;

public class CountStateGeneCollection implements GeneCollection {
    final double TOWER_DAMAGE_INCREASE = 1.4;
    final double SOLDIER_HEALTH_INCREASE = 1.3;

    HashMap<String, List<byte[][]>> data;
    String resourceName;
    Simulator simulator;
    int timeout;
    double multiplier = 1.0;
    double towerLevelAverage = 1.0;
    int creepLevel = 1;
    int heroLevel = 1;

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

    @Override
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void setTowerLevelAverage(double towerLevelAverage) {
        this.towerLevelAverage = towerLevelAverage;
    }

    @Override
    public void setCreepLevel(int level) {
        this.creepLevel = level;
    }

    @Override
    public void setHeroLevel(int level) {
        this.heroLevel = level;
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
        double countMultiplier = Math.pow(TOWER_DAMAGE_INCREASE, towerLevelAverage - 1) / Math.pow(SOLDIER_HEALTH_INCREASE, Math.min(creepLevel, heroLevel) - 1);

        int cannonsCount = (int)Math.max(cannons.length * countMultiplier, 1);
        int archersCount = (int)Math.max(archers.length * countMultiplier, 1);

        return getRecipe(cannonsCount, archersCount);
    }


    private Recipe getRecipe(int cannonsCount, int archersCount) {
        String key = cannonsCount + "," + archersCount;

        double multiplier = 1.0;

        while (!data.containsKey(key)) {
            Logger.error("Can't find key " + key + " in CountStateGeneCollection of " + resourceName);

            multiplier *= 1.5;

            key = (int)(cannonsCount / multiplier) + "," + (int)(archersCount / multiplier);
        }

        //Logger.println("Simulating...");
        List<byte[][]> genes = data.get(key);
        List<byte[][]> multipliedGenes = new ArrayList<>();

        for (byte[][] gene : genes) {
            byte[] creeps = new byte[gene[0].length];
            byte[] heros = new byte[gene[1].length];

            for (int i = 0; i < creeps.length; i++)
                creeps[i] = (byte)(gene[0][i] * multiplier * this.multiplier);
            for (int i = 0; i < creeps.length; i++)
                heros[i] = (byte)(gene[1][i] * multiplier * this.multiplier);

            byte[][] multipliedGene = new byte[][] { creeps, heros };
            multipliedGenes.add(multipliedGene);
        }

        Judge judge = new AttackJudge();
        byte[][] bestGene = Simulator.findBestGene(multipliedGenes, simulator, judge, timeout);

        if (bestGene == null) { //Simulator failed
            Logger.error("Simulator failed. Will send first gene.");
            bestGene = multipliedGenes.get(0);
        }

        return new Recipe(bestGene[0], bestGene[1]);
    }
}

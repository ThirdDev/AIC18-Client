package client.classes.genes;

public interface GeneCollection {
    public String getResourceName();
    public abstract Recipe getRecipe(int[] cannons, int[] archers);
    public abstract void setMultiplier(double multiplier);

    void setTowerLevelAverage(double towerLevelAverage);
    void setCreepLevel(int level);
    void setHeroLevel(int level);
}

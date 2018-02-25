package client.classes.genes;

public interface GeneCollection {
    public String getResourceName();
    public abstract Recipe getRecipe(int[] cannons, int[] archers);
    public abstract void setMultiplier(double multiplier);
}

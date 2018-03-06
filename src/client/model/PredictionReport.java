package client.model;

public class PredictionReport {
    private int creepDamageToBase;
    private int indexOfFirstPassingCreep;
    private int heroDamageToBase;
    private int indexOfFirstPassingHero;

    public PredictionReport(int creepDamageToBase, int pathIndexOfFirstPassingCreep, int heroDamageToBase, int pathIndexOfFirstPassingHero) {
        this.creepDamageToBase = creepDamageToBase;
        this.indexOfFirstPassingCreep = pathIndexOfFirstPassingCreep;
        this.heroDamageToBase = heroDamageToBase;
        this.indexOfFirstPassingHero = pathIndexOfFirstPassingHero;
    }

    public int getCreepDamageToBase() {
        return creepDamageToBase;
    }

    public int getIndexOfFirstPassingCreep() {
        return indexOfFirstPassingCreep;
    }

    public int getHeroDamageToBase() {
        return heroDamageToBase;
    }

    public int getIndexOfFirstPassingHero() {
        return indexOfFirstPassingHero;
    }

    @Override
    public String toString() {
        return "PredictionReport{" +
                "creepDamageToBase=" + creepDamageToBase +
                ", pathIndexOfFirstPassingCreep=" + indexOfFirstPassingCreep +
                ", heroDamageToBase=" + heroDamageToBase +
                ", pathIndexOfFirstPassingHero=" + indexOfFirstPassingHero +
                '}';
    }
}

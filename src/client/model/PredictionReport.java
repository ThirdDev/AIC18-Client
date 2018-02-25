package client.model;

public class PredictionReport {
    private int creepDamageToBase;
    private int pathIndexOfFirstPassingCreep;
    private int heroDamageToBase;
    private int pathIndexOfFirstPassingHero;

    public PredictionReport(int creepDamageToBase, int pathIndexOfFirstPassingCreep, int heroDamageToBase, int pathIndexOfFirstPassingHero) {
        this.creepDamageToBase = creepDamageToBase;
        this.pathIndexOfFirstPassingCreep = pathIndexOfFirstPassingCreep;
        this.heroDamageToBase = heroDamageToBase;
        this.pathIndexOfFirstPassingHero = pathIndexOfFirstPassingHero;
    }

    public int getCreepDamageToBase() {
        return creepDamageToBase;
    }

    public int getPathIndexOfFirstPassingCreep() {
        return pathIndexOfFirstPassingCreep;
    }

    public int getHeroDamageToBase() {
        return heroDamageToBase;
    }

    public int getPathIndexOfFirstPassingHero() {
        return pathIndexOfFirstPassingHero;
    }

    @Override
    public String toString() {
        return "PredictionReport{" +
                "creepDamageToBase=" + creepDamageToBase +
                ", pathIndexOfFirstPassingCreep=" + pathIndexOfFirstPassingCreep +
                ", heroDamageToBase=" + heroDamageToBase +
                ", pathIndexOfFirstPassingHero=" + pathIndexOfFirstPassingHero +
                '}';
    }
}

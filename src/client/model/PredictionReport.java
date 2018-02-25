package client.model;

public class PredictionReport {
    private int damageToBase;
    private int pathIndexOfFirstPassingUnit;

    public PredictionReport(int damageToBase, int pathIndexOfFirstPassingUnit) {
        this.damageToBase = damageToBase;
        this.pathIndexOfFirstPassingUnit = pathIndexOfFirstPassingUnit;
    }

    public int getDamageToBase() {
        return damageToBase;
    }

    public int getPathIndexOfFirstPassingUnit() {
        return pathIndexOfFirstPassingUnit;
    }
}

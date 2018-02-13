package client.IntelligentAgents;

import client.model.*;

import java.util.ArrayList;



public class GeneBasedAgent {

    final static double creepNormalizeFactor = 1.0 / 900.0;
    final static double heroNormalizeFactor = 1.0 / 1500.0;
    final static double cannonDamageNormalizeFactor = 1.0 / 100.0;
    final static double archerDamageNormalizeFactor = 1.0 / 400.0;

    World game;
    Gene gene;

    public GeneBasedAgent(World game, String geneFileAddress)
    {
        this.game = game;
        this.gene = new Gene(geneFileAddress);
    }

    public static int GetManhatanDistance(Point pointA, Point pointB) {
        return Math.abs(pointA.getX() - pointB.getX())
                + Math.abs(pointA.getY() - pointB.getY());
    }

    public static ArrayList<Double> CalculateCreepArray(Path path) {
        ArrayList<Double> state = new ArrayList<>();
        for (RoadCell cell : path.getRoad()) {
            int healthPoint = 0;
            for (Unit u : cell.getUnits()) {
                if (u instanceof LightUnit)
                    healthPoint += ((LightUnit)u).getMaximumHealth();
            }
            state.add(healthPoint * creepNormalizeFactor);
            System.out.print(healthPoint * creepNormalizeFactor + ", ");
        }
        return state;
    }

    public static ArrayList<Double> CalculateHeroArray(Path path) {
        ArrayList<Double> state = new ArrayList<>();
        for (RoadCell cell : path.getRoad()) {
            int healthPoint = 0;
            for (Unit u : cell.getUnits()) {
                if (u instanceof HeavyUnit)
                    healthPoint += ((HeavyUnit)u).getMaximumHealth();
            }
            state.add(healthPoint * heroNormalizeFactor);
            System.out.print(healthPoint * heroNormalizeFactor + ", ");
        }

        return state;
    }

    /**
     *
     * @param path
     * @param towerList: Only pass one type of tower to this method
     * @return
     */

    public static ArrayList<Double> CalculateTowerDamagePoint(Path path,
                                                              ArrayList<Tower>
                                                                      towerList) {
        ArrayList<Double> state = new ArrayList<>();
        for (RoadCell cell : path.getRoad()) {
            double totalDamage = 0;
            for (Tower tower : towerList) {
                Point towerPoint = tower.getPoint();
                int range = tower.getAttackRange();

                int distance = GetManhatanDistance(cell.getPoint(), towerPoint);

                if (distance <= range ) {
                    totalDamage += tower.getDamage() *
                            (tower instanceof CannonTower ?
                                    cannonDamageNormalizeFactor:
                                    archerDamageNormalizeFactor);
                }
            }
            state.add(totalDamage);
            System.out.print(totalDamage * cannonDamageNormalizeFactor + ", ");
        }
        return state;
    }

}

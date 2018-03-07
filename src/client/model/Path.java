package client.model;

import client.Util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Parsa on 1/22/2018 AD.
 * Became something useful by Amirhossein on 2/25/2018 AD.
 */
public class Path {

    private static final double sucsesiveCoff = 0.4d;
    private ArrayList<RoadCell> road;
    private ArrayList<SideWayCell> sideWayCells;
    private ArrayList<Double> archerData;
    private ArrayList<Double> cannonData;
    private int updateDataTurn = -1;

    public Path(ArrayList<RoadCell> road) {
        this.road = road;
        sideWayCells = new ArrayList<>();
        archerData = new ArrayList<>();
        cannonData = new ArrayList<>();
    }

    public ArrayList<RoadCell> getRoad() {
        return road;
    }

    public ArrayList<SideWayCell> getSideWayCells() {
        return sideWayCells;
    }

    public void addSideWayCell(SideWayCell sideWayCell) {
        this.sideWayCells.add(sideWayCell);
    }

    private void updateDates(Map map) {
        archerData.clear();
        cannonData.clear();
        for (int i = road.size() - 1; i >= 0; i--) {
            RoadCell roadCell = road.get(i);
            ArrayList<Cell> nearbyCells = Util.radialCells(roadCell, 2, map);
            Double archer = new Double(0);
            Double cannon = new Double(0);
            for (int j = 0; j < nearbyCells.size(); j++) {

                Cell tempCell = nearbyCells.get(j);

                if (tempCell instanceof GrassCell) {
                    Tower tower = ((GrassCell) tempCell).getTower();
                    if (tower != null) {
                        if (tower instanceof ArcherTower) {
                            archer += (double) tower.getDamage();
                        } else {
                            cannon += tower.getDamage() / (double) tower.getAttackSpeed();
                        }
                    }
                }
            }
            archerData.add(archer);
            cannonData.add(cannon);
        }
        Collections.reverse(archerData);
        Collections.reverse(cannonData);
    }


    public PredictionReport getReport(Map map) {
        updateDates(map);
        int heroDamageToBase = 0;
        int creepDamageToBase = 0;
        double damageToHero = 0;
        double damageToCreep = 0;
        int firstPassingHero = -1;
        int firstPassingCreep = -1;
        for (int i = road.size() - 1; i >= 0; i--) {
            damageToHero += archerData.get(i);
            damageToCreep += cannonData.get(i);
            RoadCell roadCell = road.get(i);
            ArrayList<Unit> units = roadCell.getUnits();
            if (units.size() > 0) {
                int sumHealth = 0;
                int maxHeroHealth = 0;
                int maxCreepHealth = 0;
                for (Unit tmpUnit : units) {
                    if (tmpUnit instanceof HeavyUnit) {
                        sumHealth += tmpUnit.getMaxHealth();
                        maxHeroHealth = Math.max(maxHeroHealth, tmpUnit.getMaxHealth());
                    } else {
                        maxCreepHealth = Math.max(maxCreepHealth, tmpUnit.getMaxHealth());
                    }
                }
                if (sumHealth > damageToHero) {
                    heroDamageToBase += ((sumHealth - damageToHero + maxHeroHealth - 1) / maxHeroHealth) * HeavyUnit.DAMAGE;
                    firstPassingHero = Math.max(firstPassingHero, i);
                } else {
                    if (i != road.size() - 1 &&
                            road.get(i + 1).getUnits().size() > 0 &&
                            sumHealth > damageToHero * sucsesiveCoff) {
                        heroDamageToBase += ((int) (sumHealth - (damageToHero * sucsesiveCoff) + maxHeroHealth - 1) / maxHeroHealth) * HeavyUnit.DAMAGE;
                        firstPassingHero = Math.max(firstPassingHero, i);
                    }
                }
                if (maxCreepHealth > damageToCreep) {
                    creepDamageToBase += units.size() * LightUnit.DAMAGE;
                    firstPassingCreep = Math.max(firstPassingCreep, i);
                } else {
                    if (i != road.size() - 1 &&
                            road.get(i + 1).getUnits().size() > 0 &&
                            maxCreepHealth > damageToCreep * sucsesiveCoff) {
                        creepDamageToBase += units.size();
                        firstPassingCreep = Math.max(firstPassingCreep, i);
                    }
                }
            }
        }
        return new PredictionReport
                (creepDamageToBase, firstPassingCreep,
                        heroDamageToBase, firstPassingHero);
    }

    private String toStringCache = null;

    @Override
    public String toString() {
        if (toStringCache == null) {
            toStringCache = "Path: ";

            for (int i = 0; i < road.size(); i++) {
                toStringCache += "(" + road.get(i).getLocation().getX() + "," + road.get(i).getLocation().getY() + ")" + " ";
            }
        }

        return toStringCache;
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public int getPointIndex(Point p) {
        for (int i = 0; i < road.size(); i++)
            if (road.get(i).getLocation().equals(p))
                return i;
        return -1;
    }
}

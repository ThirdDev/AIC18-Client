package client.model;

import client.Util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Parsa on 1/22/2018 AD.
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

    private void updateDates(int lastModifiedTurn, Map map, int currentTurn){
        if(updateDataTurn >= lastModifiedTurn) return;
        archerData.clear();
        cannonData.clear();
        for (int i = road.size()-1; i >= 0 ; i--) {
            RoadCell roadCell = road.get(i);
            ArrayList<Cell> nearbyCells = Util.radialCells(roadCell,2,map);
            Double archer = new Double(0);
            Double cannon = new Double(0);
            for (int j = 0; j < nearbyCells.size(); j++) {

                Cell tempCell = nearbyCells.get(j);

                if(tempCell instanceof GrassCell){
                    Tower tower = ((GrassCell) tempCell).getTower();
                    if(tower != null)
                    {
                        if(tower instanceof ArcherTower){
                            archer += (double)tower.getDamage();
                        }
                        else{
                            cannon += tower.getDamage()/(double)tower.getAttackSpeed();
                        }
                    }
                }
            }
            archerData.add(archer);
            cannonData.add(cannon);
        }
        Collections.reverse(archerData);
        Collections.reverse(cannonData);
        updateDataTurn = currentTurn;
    }


    public PredictionReport getCreepsDamage(int level, Map map, int currentTurn, int lastModifiedTurn){
        updateDates(lastModifiedTurn,map,currentTurn);
        int damageToBase = 0;
        double damageToCreep = 0;
        int firstPassingUnit = -1;
        for (int i = road.size() - 1; i >= 0 ; i--) {
            damageToCreep += cannonData.get(i);
            RoadCell roadCell = road.get(i);
            ArrayList<Unit> units = roadCell.getUnits();
            if(units.size()>0){
                int maxHealth = 0;
                for(Unit tmpUnit:units){
                    if(tmpUnit instanceof LightUnit){
                        maxHealth = Math.max(maxHealth,tmpUnit.getMaxHealth());
                    }
                }
                if(maxHealth > damageToCreep){
                    damageToBase += units.size() * LightUnit.DAMAGE;
                    firstPassingUnit = i;
                }
                else{
                    if(i != road.size() - 1 &&
                            road.get(i+1).getUnits().size() > 0 &&
                            maxHealth > damageToCreep*sucsesiveCoff ){
                        damageToBase += units.size();
                        firstPassingUnit = i;
                    }
                }
            }
        }
        return new PredictionReport(damageToBase,firstPassingUnit);
    }

    public PredictionReport getHeroDamage(int level, Map map, int currentTurn, int lastModifiedTurn){
        updateDates(lastModifiedTurn,map,currentTurn);
        int damageToBase = 0;
        double damageToHero = 0;
        int firstPassingUnit = -1;
        for (int i = road.size() - 1; i >= 0 ; i--) {
            damageToHero += archerData.get(i);
            RoadCell roadCell = road.get(i);
            ArrayList<Unit> units = roadCell.getUnits();
            if(units.size()>0){
                int sumHealth = 0;
                int maxHealth = 0;
                for(Unit tmpUnit:units){
                    if(tmpUnit instanceof HeavyUnit){
                        sumHealth += tmpUnit.getMaxHealth();
                        maxHealth = Math.max(maxHealth, tmpUnit.getMaxHealth());
                    }
                }
                if(sumHealth > damageToHero){
                    damageToBase += ((sumHealth-damageToHero+maxHealth-1)/maxHealth) * HeavyUnit.DAMAGE;
                    firstPassingUnit = i;
                }
                else{
                    if(i != road.size() - 1 &&
                            road.get(i+1).getUnits().size() > 0 &&
                            sumHealth > damageToHero*sucsesiveCoff ){
                        damageToBase += ((int)(sumHealth-(damageToHero*sucsesiveCoff)+maxHealth-1)/maxHealth) * HeavyUnit.DAMAGE;
                        firstPassingUnit = i;
                    }
                }
            }
        }
        return new PredictionReport(damageToBase,firstPassingUnit);
    }

    @Override
    public String toString() {
        String result = "Path: ";

        for (int i = 0; i < road.size(); i++) {
            result += "(" + road.get(i).getLocation().getX() + "," + road.get(i).getLocation().getY() + ")" + " ";
        }
        return result;
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

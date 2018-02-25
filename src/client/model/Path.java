package client.model;

import client.Util;

import java.util.ArrayList;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Path {

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

    private void updateDates(int lastModifiedTurn, World game){
        if(updateDataTurn >= lastModifiedTurn) return;
        archerData.clear();
        cannonData.clear();
        for (int i = road.size()-1; i >= 0 ; i--) {
            RoadCell roadCell = road.get(i);
            ArrayList<Cell> nearbyCells = Util.radialCells(roadCell,2,game.getDefenceMap());
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
        updateDataTurn = game.getCurrentTurn();
    }


    public int getCreepsDamage(int level, World game, int lastModifiedTurn){
        updateDates(lastModifiedTurn,game);

        return 0;
    }

    public int getHeroDamage(int level, World game, int lastModifiedTurn){
        updateDates(lastModifiedTurn,game);

        return 0;
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

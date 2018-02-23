package client;

import client.classes.BankAccount;
import client.model.*;
import client.model.Map;
import javafx.geometry.Side;

import java.util.*;

public class Defence {
    BankAccount bankAccount;
    World game;
    Map map;
    ArrayList<Path> paths;
    HashMap<Point,SideWayCell> sideWayCells;
    int[] colorPoint;
    int[] tempColorPoint;

    public Defence(BankAccount bankAccount, World game) {
        this.bankAccount = bankAccount;
        this.game = game;
        map = game.getDefenceMap();
        paths = game.getDefenceMapPaths();
        sideWayCells = new HashMap<>();
        //TODO: Need to change as a part of better coloring schema
        colorPoint = new int[2];
        tempColorPoint = new int[2];
        init();
    }


    public void init(){

        for (int i = 0; i < paths.size(); i++) {
            Path tempPath = paths.get(i);
            ArrayList<RoadCell> tempRoad = tempPath.getRoad();
            for (int j = 0; j < tempRoad.size(); j++) {
                RoadCell tempRoadCell = tempRoad.get(j);
                ArrayList<Cell> candidates = Util.radialCells(tempRoadCell,2,map);
                for (int k = 0; k < candidates.size(); k++) {
                    Cell candidateCell = candidates.get(k);
                    if(!(candidateCell instanceof GrassCell)) continue;
                    SideWayCell tempSideCell = sideWayCells.get(candidateCell.getLocation());
                    if (tempSideCell == null){
                        Point point = candidateCell.getLocation();
                        SideWayCell newSideWayCell = new SideWayCell(point.getX(),point.getY(),
                                ((GrassCell) candidateCell).getTower());
                        newSideWayCell.addPath(tempPath);
                        newSideWayCell.addRoadCell(tempRoadCell);
                        sideWayCells.put(candidateCell.getLocation(),newSideWayCell);
                        tempPath.addSideWayCell(newSideWayCell);
                    }
                    else{
                        if(!tempSideCell.getPaths().contains(tempPath)){
                            tempSideCell.addPath(tempPath);
                            tempPath.addSideWayCell(tempSideCell);
                        }
                        if(!tempSideCell.getRoadCells().contains(tempRoadCell)){
                            tempSideCell.addRoadCell(tempRoadCell);
                        }
                    }
                }
            }
        }
    }

    public void reColor(){
        Set<Point> keyset = sideWayCells.keySet();
        Iterator<Point> iterator = keyset.iterator();
        while (iterator.hasNext()){
            sideWayCells.get(iterator.next()).setColor(-1);
        }
        colorPoint[0] = 0;
        colorPoint[1] = 0;
        iterator = keyset.iterator();
        while (iterator.hasNext()){
            Point nextPoint = iterator.next();
            if(sideWayCells.get(nextPoint).getColor() == -1){
                tempColorPoint[0] = 0;
                tempColorPoint[1] = 0;
                color(nextPoint,0);
                if(tempColorPoint[0] > tempColorPoint[1]){
                    //Always the color of zero is better
                    colorPoint[0] += tempColorPoint[0];
                    colorPoint[1] += tempColorPoint[1];
                }
                else{
                    colorPoint[0] += tempColorPoint[1];
                    colorPoint[1] += tempColorPoint[0];
                }
            }
        }

    }

    private void color(Point point,int cl){
        SideWayCell sideWayCell = sideWayCells.get(point);
        if(sideWayCell == null) return;
        if(sideWayCell.getColor() != cl){
            System.out.println("WTF IN COLORING");
        }
        sideWayCell.setColor(cl);
        tempColorPoint[cl] += sideWayCell.getRoadCells().size();
        ArrayList<Cell> cells = Util.radiusCells(sideWayCell,1,map);
        //TODO: Better coloring schema to include more than two colors
        int nextCl = (cl == 0) ? 1 : 0;
        for (int i = 0; i < cells.size(); i++) {
            color(cells.get(i).getLocation(),nextCl);
        }
    }

    public void run(){
        //TODO: update coloring in event of beam
        //This could be time consuming and could go to the first heavy turn
        ArrayList<BeanEvent> beans = game.getBeansInThisTurn();
        for (int i = 0; i < beans.size(); i++) {
            BeanEvent tempBean = beans.get(i);
            sideWayCells.remove(tempBean.getPoint());
        }
        if(beans.size() > 0) reColor();
        ArrayList<SideWayCell> sideWayCellArrayList = new ArrayList<> (sideWayCells.values());
        Collections.sort(sideWayCellArrayList);
        Random random = new Random();
        if(sideWayCellArrayList.size()>0){
            SideWayCell candidate = sideWayCellArrayList.get(0);
            //System.out.print("***RoadCells:");
            //System.out.println(candidate.getRoadCells().size());
            if(game.isTowerConstructable(candidate)){
                Point location = candidate.getLocation();
                if(random.nextDouble()<0.5){
                    game.createArcherTower(2,location.getX(),location.getY());
                }
                else{
                    game.createCannonTower(2,location.getX(),location.getY());
                }
            }
            else{
                if (candidate.getTower() != null){
                    game.upgradeTower(candidate.getTower());
                }
            }
        }
    }
}

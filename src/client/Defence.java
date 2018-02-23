package client;

import client.classes.BankAccount;
import client.model.*;
import client.model.Map;
import javafx.geometry.Side;

import java.util.*;

public class Defence {
    BankAccount bankAccount;
    World game;

    public Defence(BankAccount bankAccount, World game) {
        this.bankAccount = bankAccount;
        this.game = game;
    }

    public void run(){
        Map map = game.getDefenceMap();
        ArrayList<Path> paths = game.getDefenceMapPaths();
        HashMap<Point,SideWayCell> sideWayCells = new HashMap<>();
        for (int i = 0; i < paths.size(); i++) {
            Path tempPath = paths.get(i);
            ArrayList<RoadCell> tempRoad = tempPath.getRoad();
            for (int j = 0; j < tempRoad.size(); j++) {
                RoadCell tempRoadCell = tempRoad.get(j);
                ArrayList<Cell> candidates = Util.radialCells(tempRoadCell,2,map);
                for (int k = 0; k < candidates.size(); k++) {
                    Cell candidateCell = candidates.get(k);
                    if(!(candidateCell instanceof GrassCell)) continue;
                    SideWayCell tempSideCell = sideWayCells.get(candidateCell);
                    if (tempSideCell == null){
                        Point point = candidateCell.getLocation();
                        SideWayCell newSideWayCell = new SideWayCell(point.getX(),point.getY(),
                                ((GrassCell) candidateCell).getTower());
                        newSideWayCell.addPaths(tempPath);
                        newSideWayCell.addRoadCells(tempRoadCell);
                        sideWayCells.put(candidateCell.getLocation(),newSideWayCell);
                    }
                    else{
                        if(!tempSideCell.getPaths().contains(tempPath)){
                            tempSideCell.addPaths(tempPath);
                        }
                        if(!tempSideCell.getRoadCells().contains(tempRoadCell)){
                            tempSideCell.addRoadCells(tempRoadCell);
                        }
                    }
                }
            }
        }
        ArrayList<SideWayCell> sideWayCellArrayList = new ArrayList<> (sideWayCells.values());
        Collections.sort(sideWayCellArrayList);
        Random random = new Random();
        if(sideWayCellArrayList.size()>0){
            SideWayCell candidate = sideWayCellArrayList.get(0);
            if(game.isTowerConstructable(candidate)){
                Point location = candidate.getLocation();
                if(random.nextDouble()<0.5){
                    game.createArcherTower(4,location.getX(),location.getY());
                }
                else{
                    game.createCannonTower(4,location.getX(),location.getY());
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

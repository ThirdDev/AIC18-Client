package client;

import client.classes.BankAccount;
import client.model.*;
import client.model.Map;
import javafx.geometry.Side;

import java.util.*;

public class Defence {
    private BankAccount bankAccount;
    private World game;
    private Map map;
    private ArrayList<Path> paths;
    private HashMap<Point,SideWayCell> sideWayCells;
    private ArrayList<SideWayCell> buildable;
    private ArrayList<SideWayCell> tempArray0;
    private ArrayList<SideWayCell> tempArray1;
    private int[] tempColorPoint;

    public Defence(BankAccount bankAccount, World game) {
        this.bankAccount = bankAccount;
        this.game = game;
        map = game.getDefenceMap();
        paths = game.getDefenceMapPaths();
        sideWayCells = new HashMap<>();
        tempColorPoint = new int[2];
        tempArray0 = new ArrayList<>();
        tempArray1 = new ArrayList<>();
        buildable = new ArrayList<>();
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

        buildable.clear();
        Set<Point> keyset = sideWayCells.keySet();
        Iterator<Point> iterator = keyset.iterator();
        while (iterator.hasNext()){
            SideWayCell tmp = sideWayCells.get(iterator.next());
            tmp.setColor(-1);
        }
        iterator = keyset.iterator();
        while (iterator.hasNext()){
            Point nextPoint = iterator.next();
            if(sideWayCells.get(nextPoint).getColor() == -1){
                tempColorPoint[0] = 0;
                tempColorPoint[1] = 0;
                tempArray0.clear();
                tempArray1.clear();
                color(nextPoint,0);
                if(tempColorPoint[0] > tempColorPoint[1]){
                    //Always the color of zero is better
                    for(int i = 0 ; i < tempArray0.size(); i++){
                        buildable.add(tempArray0.get(i));
                    }
                }
                else{
                    for(int i = 0 ; i < tempArray1.size(); i++){
                        buildable.add(tempArray1.get(i));
                    }
                }
            }
        }
        System.out.println("Buildables: ");
        for (int i = 0; i < buildable.size(); i++) {
            System.out.println(buildable.get(i).getLocation());
        }
        System.out.print("****************************");
    }

    private void color(Point point,int cl){
        SideWayCell sideWayCell = sideWayCells.get(point);
        if(sideWayCell == null || sideWayCell.getColor() != -1) return;
        if(sideWayCell.getColor() != -1 && sideWayCell.getColor() != cl){
            System.out.println("WTF IN COLORING");
            System.out.println(point);
        }
        sideWayCell.setColor(cl);
        tempColorPoint[cl] += sideWayCell.getRoadCells().size();
        if(cl == 0){
            tempArray0.add(sideWayCell);
        }
        else{
            tempArray1.add(sideWayCell);
        }
        ArrayList<Cell> cells = Util.radiusCells(sideWayCell,1,map);
        int nextCl = (cl == 0) ? 1 : 0;
        for (int i = 0; i < cells.size(); i++) {
            color(cells.get(i).getLocation(),nextCl);
        }
    }

    public void run(World game){
        //Checking beans and planing accordingly could be time
        // consuming and could go to the first heavy turn,
        // with the use of an input parameter
        this.game = game;
        map = game.getDefenceMap();
        paths = game.getDefenceMapPaths();
        ArrayList<BeanEvent> beans = game.getBeansInThisTurn();
        boolean beanRecolor = false;
        for (int i = 0; i < beans.size(); i++) {
            BeanEvent tempBean = beans.get(i);
            if(tempBean.getOwner() == Owner.ENEMY) {
                sideWayCells.remove(tempBean.getPoint());
                beanRecolor = true;
            }
        }
        if(beanRecolor || game.getCurrentTurn() == 1) reColor();
//        ArrayList<SideWayCell> sideWayCellArrayList = new ArrayList<> (sideWayCells.values());
//        Collections.sort(sideWayCellArrayList);
//        Random random = new Random();
//        if(sideWayCellArrayList.size()>0){
//            SideWayCell candidate = sideWayCellArrayList.get(0);
//            //System.out.print("***RoadCells:");
//            //System.out.println(candidate.getRoadCells().size());
//            if(game.isTowerConstructable(candidate)){
//                Point location = candidate.getLocation();
//                if(random.nextDouble()<0.5){
//                    game.createArcherTower(2,location.getX(),location.getY());
//                }
//                else{
//                    game.createCannonTower(2,location.getX(),location.getY());
//                }
//            }
//            else{
//                if (candidate.getTower() != null){
//                    game.upgradeTower(candidate.getTower());
//                }
//            }
//        }
    }
}

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

    Defence(BankAccount bankAccount, World game) {
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


    private void init(){

        for (Path tempPath : paths) {
            ArrayList<RoadCell> tempRoad = tempPath.getRoad();
            for (RoadCell tempRoadCell : tempRoad) {
                ArrayList<Cell> candidates = Util.radialCells(tempRoadCell, 2, map);
                for (Cell candidateCell : candidates) {
                    if (!(candidateCell instanceof GrassCell)) continue;
                    SideWayCell tempSideCell = sideWayCells.get(candidateCell.getLocation());
                    if (tempSideCell == null) {
                        Point point = candidateCell.getLocation();
                        SideWayCell newSideWayCell = new SideWayCell(point.getX(), point.getY(),
                                ((GrassCell) candidateCell).getTower());
                        newSideWayCell.addPath(tempPath);
                        newSideWayCell.addRoadCell(tempRoadCell);
                        sideWayCells.put(candidateCell.getLocation(), newSideWayCell);
                        tempPath.addSideWayCell(newSideWayCell);
                    } else {
                        if (!tempSideCell.getPaths().contains(tempPath)) {
                            tempSideCell.addPath(tempPath);
                            tempPath.addSideWayCell(tempSideCell);
                        }
                        if (!tempSideCell.getRoadCells().contains(tempRoadCell)) {
                            tempSideCell.addRoadCell(tempRoadCell);
                        }
                    }
                }
            }
        }
    }

    private void reColor(){

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
                System.out.println("Color point0:");
                System.out.println(tempColorPoint[0]);
                System.out.println("Color point1:");
                System.out.println(tempColorPoint[1]);
                if(tempColorPoint[0] > tempColorPoint[1]){
                    //Always the color of zero is better
                    buildable.addAll(tempArray0);
                }
                else{
                    buildable.addAll(tempArray1);
                }
            }
        }

        /**
        System.out.println("Buildables: ");
        for (int i = 0; i < buildable.size(); i++) {
            System.out.println(buildable.get(i).getLocation());
        }
        System.out.print("****************************");
         /**/
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
        for (Cell cell : cells) {
            color(cell.getLocation(), nextCl);
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
        for (BeanEvent tempBean : beans) {
            if (tempBean.getOwner() == Owner.ENEMY) {
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

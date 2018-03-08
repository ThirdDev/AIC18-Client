package client;

import client.classes.Bank;
import client.classes.BankAccount;
import client.classes.Logger;
import client.classes.simulator.towers.Archer;
import client.classes.simulator.towers.Cannon;
import client.model.*;
import client.model.Map;
import common.util.Log;
import javafx.geometry.Side;

import java.util.*;

public class Defence {

    private final int levelDeference = 2;

    private BankAccount bankAccount;
    private World game;
    private Map map;
    private ArrayList<Path> paths;
    private HashMap<Point, SideWayCell> sideWayCells;
    private ArrayList<SideWayCell> buildable;
    private ArrayList<SideWayCell> tempArray0;
    private ArrayList<SideWayCell> tempArray1;
    private double[] tempColorPoint;

    Defence(BankAccount bankAccount, World game) {
        this.bankAccount = bankAccount;
        this.game = game;
        map = game.getDefenceMap();
        paths = game.getDefenceMapPaths();
        sideWayCells = new HashMap<>();
        tempColorPoint = new double[2];
        tempArray0 = new ArrayList<>();
        tempArray1 = new ArrayList<>();
        buildable = new ArrayList<>();
        init();
    }


    private void init() {

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

    private void reColor() {

        buildable.clear();
        Set<Point> keyset = sideWayCells.keySet();
        Iterator<Point> iterator = keyset.iterator();
        while (iterator.hasNext()) {
            SideWayCell tmp = sideWayCells.get(iterator.next());
            tmp.setColor(-1);
        }
        iterator = keyset.iterator();
        while (iterator.hasNext()) {
            Point nextPoint = iterator.next();
            if (sideWayCells.get(nextPoint).getColor() == -1) {
                tempColorPoint[0] = 0.0d;
                tempColorPoint[1] = 0.0d;
                tempArray0.clear();
                tempArray1.clear();
                color(nextPoint, 0);
                if (tempColorPoint[0] > tempColorPoint[1]) {
                    //Always the color of zero is better
                    buildable.addAll(tempArray0);
                } else {
                    buildable.addAll(tempArray1);
                }
            }
        }
    }

    private void color(Point point, int cl) {
        SideWayCell sideWayCell = sideWayCells.get(point);
        if (sideWayCell == null || sideWayCell.getColor() != -1) return;
        sideWayCell.setColor(cl);
        if (game.isTowerConstructable(sideWayCell)) {
            tempColorPoint[cl] += sideWayCell.getRoadCells().size() > 2 ?
                    sideWayCell.getRoadCells().size() + 0.000001 : sideWayCell.getRoadCells().size();
        }
        if (cl == 0) {
            tempArray0.add(sideWayCell);
        } else {
            tempArray1.add(sideWayCell);
        }
        ArrayList<Cell> cells = Util.radiusCells(sideWayCell, 1, map);
        int nextCl = (cl == 0) ? 1 : 0;
        for (Cell cell : cells) {
            color(cell.getLocation(), nextCl);
        }
    }

    public void run(World game) {
        //Checking beans and planing accordingly could be time
        // consuming and could go to the first heavy turn,
        // with the use of an input parameter
        this.game = game;
        map = game.getDefenceMap();
        paths = game.getDefenceMapPaths();
        bankAccount = Bank.getAccount(BankController.BANK_ACCOUNT_DEFENCE);
        ArrayList<BeanEvent> beans = game.getBeansInThisTurn();
        boolean beanRecolor = false;
        for (BeanEvent tempBean : beans) {
            if (tempBean.getOwner().equals(Owner.ENEMY) && sideWayCells.containsKey(tempBean.getPoint())) {
                SideWayCell beanedCell = sideWayCells.remove(tempBean.getPoint());
                Logger.println("Our cell beaned : " + beanedCell);
                beanRecolor = true;
            }
        }
        if (beanRecolor || game.getCurrentTurn() == 1) {
            reColor();
        }

        HashMap<Point, TowerBuildOrder> orders = new HashMap<>();

        //TODO: Loop will start from here
        do {
            Set<Point> keyset = orders.keySet();
            Iterator<Point> iterator = keyset.iterator();
            while (iterator.hasNext()) {
                TowerBuildOrder order = orders.get(iterator.next());
                Logger.println("Order at " + order.getPoint() + " for tower type " + order.getTowerType());
                iterator.remove();
                if (order.getLevel() == -1) {
                    Util.upgradeTower(game, order.getPoint());
                    Logger.println("Upgrade at " + order.getPoint());
                } else {
                    if (order.getTowerType().equals(
                            TowerBuildOrder.TowerType.Cannon)) {
                        game.createCannonTower(order.getLevel(),
                                order.getPoint().getX(),
                                order.getPoint().getY());
                        Logger.println("Cannon at " + order.getPoint());
                    }
                    if (order.getTowerType().equals(
                            TowerBuildOrder.TowerType.Archer)) {
                        game.createArcherTower(order.getLevel(),
                                order.getPoint().getX(),
                                order.getPoint().getY());
                        Logger.println("Archer at " + order.getPoint());
                    }
                }
            }

            Path mvp = null;
            PredictionReport mvpReport = new PredictionReport(0, 0,
                    0, 0);
            for (Path path : paths) {
                int lastSum = mvpReport.getCreepDamageToBase() + mvpReport.getHeroDamageToBase();
                PredictionReport tmpReport = path.getReport(map);
                //Logger.println(tmpReport.toString());
                int sum = tmpReport.getCreepDamageToBase() + tmpReport.getHeroDamageToBase();
                if (sum > lastSum || mvp == null) {
                    mvp = path;
                    mvpReport = tmpReport;
                } else {
                    if (sum == lastSum) {
                        int minLastRush = Math.min(
                                mvp.getRoad().size() - mvpReport.getIndexOfFirstPassingCreep(),
                                mvp.getRoad().size() - mvpReport.getIndexOfFirstPassingHero()
                        );
                        int minRush = Math.min(
                                path.getRoad().size() - tmpReport.getIndexOfFirstPassingCreep(),
                                path.getRoad().size() - tmpReport.getIndexOfFirstPassingHero()
                        );
                        if (minRush < minLastRush) {
                            mvp = path;
                            mvpReport = tmpReport;
                        }
                    }
                }
            }
            if (mvpReport.getCreepDamageToBase() == 0 &&
                    mvpReport.getHeroDamageToBase() == 0) {
                Logger.println("No mvp path");

                break;
            }
            ArrayList<SideWayCell> buildCells = new ArrayList<>();

            int startIndex = (mvpReport.getHeroDamageToBase() > mvpReport.getCreepDamageToBase()) ?
                    mvpReport.getIndexOfFirstPassingHero() : mvpReport.getIndexOfFirstPassingCreep();
            if (startIndex == -1) break;
            ArrayList<RoadCell> roadCells = mvp.getRoad();
            for (int i = Math.max(startIndex - 1 , 0) ; i < Math.min(mvp.getRoad().size() , startIndex + 8); i++) {
                RoadCell roadCell = roadCells.get(i);
                ArrayList<Cell> cells = Util.radialCells(roadCell, 2, map);
                for (Cell cell : cells) {
                    SideWayCell candidateCell = sideWayCells.get(cell.getLocation());
                    if (candidateCell != null && buildable.contains(candidateCell)) {
                        buildCells.add(candidateCell);
                    }
                }
            }
            //Todo:Good sorting of the cells in the buildCells array

            Collections.sort(buildCells);

            if (mvpReport.getHeroDamageToBase() > mvpReport.getCreepDamageToBase()) {
                //Issues archer order
                ArrayList<SideWayCell> buildArcherCells = new ArrayList<>();
                for (SideWayCell swc : buildCells) {
                    if (swc.isEmpty() || swc.getTower() instanceof ArcherTower) {
                        buildArcherCells.add(swc);
                    }
                }

                for (int i = 0; i < buildArcherCells.size() - 1; i++) {
                    SideWayCell me = buildArcherCells.get(i);
                    SideWayCell next = buildArcherCells.get(i + 1);
                    if (me.getRoadCells().size() ==
                            next.getRoadCells().size()) continue;
                    Tower tower = me.getTower();
                    Tower nextTower = next.getTower();
                    int towerLevel = (tower == null) ? 0 : tower.getLevel();
                    int nextTowerLevel = (nextTower == null) ? 0 : nextTower.getLevel();
                    if (towerLevel - nextTowerLevel == levelDeference) {
                        if (i < buildArcherCells.size() - 2) continue;
                        me = next;
                        tower = nextTower;
                        towerLevel = nextTowerLevel;
                    }
                    TowerBuildOrder order = orders.get(me.getLocation());

                    if (order == null) {
                        if (towerLevel == 0) {
                            int money = ArcherTower.getPrice(1);
                            if (bankAccount.canSpend(money)) {
                                order = new TowerBuildOrder(me.getLocation(), 1,
                                        TowerBuildOrder.TowerType.Archer);
                                bankAccount.retrieveMoney(money);
                            }
                            else{
                                Logger.println("Need " + money + " for Archer level " + 1);
                                Logger.println("at " + me.getLocation() );
                            }
                        } else {
                            int money = ArcherTower.getPrice(towerLevel + 1);
                            if (bankAccount.canSpend(money)) {
                                order = new TowerBuildOrder(me.getLocation(), -1,
                                        TowerBuildOrder.TowerType.Archer);
                                bankAccount.retrieveMoney(money);
                            }
                            else{
                                Logger.println("Need " + money + " for Archer level " + (towerLevel+1));
                                Logger.println("at " + me.getLocation() );
                            }
                        }

                    } else {
                        if (order.getTowerType() == TowerBuildOrder.TowerType.Archer) {

                            int level = order.getLevel();
                            int money = ArcherTower.getPrice(level + 1)
                                    - ArcherTower.getPrice(level);
                            if (bankAccount.canSpend(money)) {
                                orders.remove(me.getLocation());
                                order = new TowerBuildOrder(me.getLocation(), level + 1,
                                        TowerBuildOrder.TowerType.Archer);
                                bankAccount.retrieveMoney(money);
                            }
                            else{
                                Logger.println("Need " + money + " for Archer upgrade");
                                Logger.println("at " + me.getLocation() );
                            }
                        }
                    }
                    if (order != null) {
                        orders.put(order.getPoint(), order);
                        Logger.println("Issuing order at" + order.getPoint());
                        Logger.println("For a archer tower (upgrade or build)");
                        Tower tower1 = new ArcherTower(me.getLocation().getX(),
                                me.getLocation().getY(), Owner.ME, order.getLevel(), -1 , Integer.MIN_VALUE +1);
                        me.setTower(tower1);
                        GrassCell grassCell = (GrassCell) map.getCell(me.getLocation().getX(), me.getLocation().getY());
                        grassCell.setTower(tower1);
                        break;
                    }
                }
            } else {
                //Issues cannon order
                ArrayList<SideWayCell> buildCannonCells = new ArrayList<>();
                for (SideWayCell swc : buildCells) {
                    if (swc.isEmpty() || swc.getTower() instanceof CannonTower) {
                        buildCannonCells.add(swc);
                    }
                }
                for (int i = 0; i < buildCannonCells.size() - 1; i++) {
                    SideWayCell me = buildCannonCells.get(i);
                    SideWayCell next = buildCannonCells.get(i + 1);
                    if (me.getRoadCells().size() ==
                            next.getRoadCells().size()) continue;
                    Tower tower = me.getTower();
                    Tower nextTower = next.getTower();
                    int towerLevel = (tower == null) ? 0 : tower.getLevel();
                    int nextTowerLevel = (nextTower == null) ? 0 : nextTower.getLevel();
                    if (towerLevel - nextTowerLevel == levelDeference) {
                        if (i < buildCannonCells.size() - 2) continue;
                        me = next;
                        tower = nextTower;
                        towerLevel = nextTowerLevel;
                    }
                    TowerBuildOrder order = orders.get(me.getLocation());

                    if (order == null) {
                        if (towerLevel == 0) {
                            int money = CannonTower.getPrice(1);
                            if (bankAccount.canSpend(money)) {
                                order = new TowerBuildOrder(me.getLocation(), 1,
                                        TowerBuildOrder.TowerType.Cannon);
                                bankAccount.retrieveMoney(money);
                            }
                            else{
                                Logger.println("Need " + money + " for Cannon level " + 1);
                                Logger.println("at " + me.getLocation() );
                            }
                        } else {
                            int money = CannonTower.getPrice(towerLevel + 1);
                            if (bankAccount.canSpend(money)) {
                                order = new TowerBuildOrder(me.getLocation(), -1,
                                        TowerBuildOrder.TowerType.Cannon);
                                bankAccount.retrieveMoney(money);
                            }
                            else{
                                Logger.println("Need " + money + " for Cannon level " + (towerLevel+1));
                                Logger.println("at " + me.getLocation() );
                            }
                        }

                    } else {
                        if (order.getTowerType() == TowerBuildOrder.TowerType.Cannon) {
                            int level = order.getLevel();
                            int money = CannonTower.getPrice(level + 1) - CannonTower.getPrice(level);
                            if (bankAccount.canSpend(money)) {
                                orders.remove(me.getLocation());
                                order = new TowerBuildOrder(me.getLocation(), level + 1,
                                        TowerBuildOrder.TowerType.Cannon);
                                bankAccount.retrieveMoney(money);
                            }
                            else{
                                Logger.println("Need " + money + " for Cannon upgrade");
                                Logger.println("at " + me.getLocation() );
                            }

                        }
                    }
                    if (order != null) {
                        orders.put(order.getPoint(), order);
                        Logger.println("Issuing order at" + order.getPoint());
                        Logger.println("For a cannon tower (upgrade or build)");
                        Tower tower1 = new CannonTower(me.getLocation().getX(),
                                me.getLocation().getY(), Owner.ME, order.getLevel(), -1, Integer.MIN_VALUE +1 );
                        me.setTower(tower1);
                        GrassCell grassCell = (GrassCell) map.getCell(me.getLocation().getX(), me.getLocation().getY());
                        grassCell.setTower(tower1);
                        break;
                    }
                }
            }
        }
        while (orders.keySet().size() > 0);

        //Todo:Add the tower to the cell of the map and it's respective SideWayCell
        //Todo:Do above steps again until you run out of money
    }
}

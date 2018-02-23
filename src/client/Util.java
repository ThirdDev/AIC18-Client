package client;

import client.classes.BankAccount;
import client.model.*;

import java.util.ArrayList;

public class Util {

    public static boolean inRange(Point point, Map map){
        int x = point.getX();
        int y = point.getY();
        boolean flagX = (x>=0 && x<map.getHeight());
        boolean flagY = (y>=0 && y<map.getWidth());
        return flagX & flagY;
    }

    public static boolean inRange(int x , int y, Map map){
        boolean flagX = (x>=0 && x<map.getHeight());
        boolean flagY = (y>=0 && y<map.getWidth());
        return flagX & flagY;
    }

    public static ArrayList<Cell> radiusCells(Cell cell, int range, Map map) {
        return radiusCells(cell.getLocation(), range, map);
    }

    public static ArrayList<Cell> radiusCells(Point location, int range, Map map){
        ArrayList<Cell> cells = new ArrayList<>();
        int x = location.getX();
        int y = location.getY();
        for (int i = 0; i < range; i++) {
            int xUp = x-range+i;
            int yUp = y+i;

            int xRight = x+i;
            int yRight = y+range-i;

            int xDown = x+range-i;
            int yDown = y-i;

            int xLeft = x-i;
            int yLeft = y-range+i;

            if(inRange(xUp,yUp,map)){
                cells.add(map.getCell(xUp,yUp));
            }

            if(inRange(xRight,yRight,map)){
                cells.add(map.getCell(xRight,yRight));
            }

            if(inRange(xDown,yDown,map)){
                cells.add(map.getCell(xDown,yDown));
            }

            if(inRange(xLeft,yLeft,map)){
                cells.add(map.getCell(xLeft,yLeft));
            }
        }

        return cells;
    }

    public static ArrayList<Cell> radialCells(Cell cell, int range, Map map){
        return radiusCells(cell.getLocation(), range, map);
    }

    public static ArrayList<Cell> radialCells(Point location, int range, Map map){
        ArrayList<Cell> cells = new ArrayList<>();
        int x = location.getX();
        int y = location.getY();
        for (int i = 1; i <= range; i++) {
            cells.addAll(radiusCells(location, i, map));
        }

        return cells;
    }

    public static boolean buildArcher(int level, BankAccount bankAccount,
                                      Point point, World game){
        if(bankAccount.canSpend(ArcherTower.getPrice(level))){
            game.createArcherTower(level,point.getX(),point.getY());
            bankAccount.retrieveMoney(ArcherTower.getPrice(level));
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean buildCannon(int level, BankAccount bankAccount,
                                      Point point, World game){
        if(bankAccount.canSpend(CannonTower.getPrice(level))){
            game.createCannonTower(level,point.getX(),point.getY());
            bankAccount.retrieveMoney(CannonTower.getPrice(level));
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean upgradeTower(BankAccount bankAccount, World game,
                                       Tower tower){
        int priceToPay = CannonTower.getPrice(tower.getLevel()+1) -
                CannonTower.getPrice(tower.getLevel());
        if(bankAccount.canSpend(priceToPay)){
            game.upgradeTower(tower);
            bankAccount.retrieveMoney(priceToPay);
            return true;
        }
        else{
            return false;
        }

    }

    public static boolean buildCreep(int count,BankAccount bankAccount,
                                      int pathIndex, World game){
        if(bankAccount.canSpend(LightUnit.getCurrentPrice(count))){
            for (int i = 0; i < count; i++) {
                game.createLightUnit(pathIndex);
                LightUnit.createdUnit();
            }
            bankAccount.retrieveMoney(LightUnit.getCurrentPrice(count));
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean buildHero(int count,BankAccount bankAccount,
                                     int pathIndex, World game){
        if(bankAccount.canSpend(HeavyUnit.getCurrentPrice(count))){
            for (int i = 0; i < count; i++) {
                game.createHeavyUnit(pathIndex);
                HeavyUnit.createdUnit();
            }
            bankAccount.retrieveMoney(HeavyUnit.getCurrentPrice(count));
            return true;
        }
        else{
            return false;
        }
    }




}

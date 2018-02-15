package client.model;

import java.util.ArrayList;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public interface World {

    ArrayList<Unit> getMyUnits();

    ArrayList<Unit> getEnemyUnits();

    ArrayList<Tower> getMyTowers();

    ArrayList<Tower> getVisibleEnemyTowers();

    Map getAttackMap();

    ArrayList<Path> getAttackMapPaths();

    ArrayList<Path> getDefenceMapPaths();

    Map getDefenceMap();

    Player getEnemyInformation();

    Player getMyInformation();

    void createLightUnit(int pathIndex);

    void createHeavyUnit(int pathIndex);

    void createArcherTower(int lvl, int x, int y);

    void createCannonTower(int lvl, int x, int y);

    void upgradeTower(int tid);

    void upgradeTower(Tower tower);

    void plantBean(int x, int y);

    void createStorm(int x, int y);

    ArrayList<BeanEvent> getBeansInThisTurn();

    ArrayList<StormEvent> getStormsInThisTurn();

    ArrayList<Tower> getDestroyedTowersInThisTurn();

    ArrayList<Unit> getDeadUnitsInThisTurn();

    ArrayList<Unit> getPassedUnitsInThisTurn();

    boolean isTowerConstructable(Cell cell);

    int getCurrentTurn();

}

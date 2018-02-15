package client.model;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.model.Event;
import common.network.data.Message;
import common.util.Log;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Created by Parsa on 1/21/2018 AD.
 */
public class Game implements World {
    private Map myAttackMap;
    private Map myDefenceMap;
    private Player[] players;
    private ArrayList<Path> myDefenceMapPaths;
    private ArrayList<Path> myAttackMapPaths;
    private int currentTurn;

    public static int MAX_TURNS_IN_GAME;
    public static int STORM_RANGE;
    public static int INITIAL_STRENGTH;
    public static int INITIAL_MONEY;
    public static int INITIAL_BEANS_COUNT;
    public static int INITIAL_STORMS_COUNT;

    ArrayList<StormEvent> stormsInThisCycle;
    ArrayList<BeanEvent> beansInThisCycle;
    ArrayList<Tower> destroyedTowersInThisCycle;
    ArrayList<Unit> deadUnitsInThisCycle;
    ArrayList<Unit> passedUnitsInThisCycle;

    private Consumer<Message> sender;
    private final String TAG = "GAME";

    public Game(Consumer<Message> sender) {

        this.sender = sender;
        players = new Player[2];
    }

    public Player[] getPlayers() {
        return players;
    }

    public void handleInitMessage(Message msg) {

        currentTurn = 0;
        Log.d(TAG, "init msg recived.");
        Log.d(TAG, "Initializing Maps");
        JsonObject mapObject = msg.args.get(0).getAsJsonObject().getAsJsonObject("map");
        JsonArray mapSize = mapObject.getAsJsonArray("size");

        int width = mapSize.get(0).getAsInt();
        int height = mapSize.get(1).getAsInt();

        Cell[][] cells1 = new Cell[height][width];
        Cell[][] cells2 = new Cell[height][width];

        JsonArray cellsArray = mapObject.getAsJsonArray("cells");

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                if (cellsArray.get(i).getAsString().charAt(j) == 'g') {
                    cells1[i][j] = new GrassCell(j, i,null);
                    cells2[i][j] = new GrassCell(j, i,null);
                } else if (cellsArray.get(i).getAsString().charAt(j) == 'r') {
                    cells1[i][j] = new RoadCell(j, i);
                    cells2[i][j] = new RoadCell(j, i);
                } else {
                    cells1[i][j] = new BlockCell(j, i);
                    cells2[i][j] = new BlockCell(j, i);
                }
            }
        }

        this.myAttackMap = new Map(width, height,cells1);
        this.myDefenceMap = new Map(width, height,cells2);

        Log.d(TAG, myAttackMap.toString());
        Log.d(TAG, myDefenceMap.toString());


        Log.d(TAG, "Initializing Paths");
        myDefenceMapPaths = new ArrayList<Path>();
        JsonArray defancePathArray = msg.args.get(0).getAsJsonObject().getAsJsonArray("paths");

        for (int i = 0; i < defancePathArray.size(); i++) {

            JsonObject tmpPath = defancePathArray.get(i).getAsJsonObject();

            int lenght = tmpPath.get("len").getAsInt();

            ArrayList<RoadCell> tmpRoadCells = new ArrayList<>();

            JsonArray tmpCells = tmpPath.getAsJsonArray("cells");

            for (int j = 0; j < tmpCells.size(); j++) {

                int x = tmpCells.get(j).getAsJsonObject().get("x").getAsInt();
                int y = tmpCells.get(j).getAsJsonObject().get("y").getAsInt();

                tmpRoadCells.add((RoadCell) myDefenceMap.getCell(x,y));
            }

            this.myDefenceMapPaths.add(new Path(tmpRoadCells));
        }

        for (int i = 0; i < myDefenceMapPaths.size(); i++) {
            Log.d(TAG, i + ":" + myDefenceMapPaths.get(i));
        }

        myAttackMapPaths = new ArrayList<Path>();
        JsonArray attackPathArray = msg.args.get(0).getAsJsonObject().getAsJsonArray("paths");

        for (int i = 0; i < attackPathArray.size(); i++) {

            JsonObject tmpPath = attackPathArray.get(i).getAsJsonObject();

            int lenght = tmpPath.get("len").getAsInt();

            ArrayList<RoadCell> tmpRoadCells = new ArrayList<>();

            JsonArray tmpCells = tmpPath.getAsJsonArray("cells");

            for (int j = 0; j < tmpCells.size(); j++) {

                int x = tmpCells.get(j).getAsJsonObject().get("x").getAsInt();
                int y = tmpCells.get(j).getAsJsonObject().get("y").getAsInt();

                tmpRoadCells.add((RoadCell) myAttackMap.getCell(x,y));
            }

            this.myAttackMapPaths.add(new Path(tmpRoadCells));
        }

        for (int i = 0; i < myAttackMapPaths.size(); i++) {
            Log.d(TAG, i + ":" + myAttackMapPaths.get(i));
        }
        //init params
        Log.d(TAG, "Initializing Parameters");
        JsonArray paramsArray = msg.args.get(0).getAsJsonObject().getAsJsonArray("params");

        int initHealth = paramsArray.get(0).getAsInt();
        int initMoney = paramsArray.get(1).getAsInt();
        int max_turn = paramsArray.get(2).getAsInt();
        //int unitlvlup = paramsArray.get(3).getAsInt();
        int beans = paramsArray.get(3).getAsInt();
        int storms = paramsArray.get(4).getAsInt();
        int stormRange = paramsArray.get(5).getAsInt();

        INITIAL_STRENGTH = initHealth;
        INITIAL_MONEY = initMoney;
        INITIAL_BEANS_COUNT = beans;
        INITIAL_STORMS_COUNT = storms;
        STORM_RANGE = stormRange;
        MAX_TURNS_IN_GAME = max_turn;

        players[0] = new Player(initMoney, 0, initHealth, beans, storms);
        players[1] = new Player(initMoney, 0, initHealth, beans, storms);

        Log.d(TAG, "0" + players[0]);
        Log.d(TAG, "1" + players[1]);

        JsonArray unitsDetails = paramsArray.get(6).getAsJsonArray();
        JsonArray lightDetails = unitsDetails.get(0).getAsJsonArray();
        JsonArray heavyDetails = unitsDetails.get(1).getAsJsonArray();

        LightUnit.INITIAL_PRICE = lightDetails.get(0).getAsInt();
        LightUnit.PRICE_INCREASE = lightDetails.get(1).getAsInt();
        LightUnit.INITIAL_HEALTH = lightDetails.get(2).getAsInt();
        LightUnit.HEALTH_COEFF = lightDetails.get(3).getAsDouble();
        LightUnit.INITIAL_BOUNTY = lightDetails.get(4).getAsInt();
        LightUnit.BOUNTY_INCREASE = lightDetails.get(5).getAsInt();
        LightUnit.MOVE_SPEED = lightDetails.get(6).getAsInt();
        LightUnit.DAMAGE = lightDetails.get(7).getAsInt();
        LightUnit.VISION_RANGE = lightDetails.get(8).getAsInt();
        LightUnit.LEVEL_UP_THRESHOLD = lightDetails.get(9).getAsInt();
        LightUnit.ADDED_INCOME = lightDetails.get(10).getAsInt();

        HeavyUnit.INITIAL_PRICE = heavyDetails.get(0).getAsInt();
        HeavyUnit.PRICE_INCREASE = heavyDetails.get(1).getAsInt();
        HeavyUnit.INITIAL_HEALTH = heavyDetails.get(2).getAsInt();
        HeavyUnit.HEALTH_COEFF = heavyDetails.get(3).getAsDouble();
        HeavyUnit.INITIAL_BOUNTY = heavyDetails.get(4).getAsInt();
        HeavyUnit.BOUNTY_INCREASE = heavyDetails.get(5).getAsInt();
        HeavyUnit.MOVE_SPEED = heavyDetails.get(6).getAsInt();
        HeavyUnit.DAMAGE = heavyDetails.get(7).getAsInt();
        HeavyUnit.VISION_RANGE = heavyDetails.get(8).getAsInt();
        HeavyUnit.LEVEL_UP_THRESHOLD = heavyDetails.get(9).getAsInt();
        HeavyUnit.ADDED_INCOME = heavyDetails.get(10).getAsInt();

        JsonArray towersDetails = paramsArray.get(7).getAsJsonArray();
        JsonArray archerDetails = towersDetails.get(0).getAsJsonArray();
        JsonArray canonDetails = towersDetails.get(1).getAsJsonArray();

        ArcherTower.INITIAL_PRICE = archerDetails.get(0).getAsInt();
        ArcherTower.INITIAL_LEVEL_UP_PRICE = archerDetails.get(1).getAsInt();
        ArcherTower.PRICE_COEFF = archerDetails.get(2).getAsDouble();
        ArcherTower.INITIAL_DAMAGE = archerDetails.get(3).getAsInt();
        ArcherTower.DAMAGE_COEFF = archerDetails.get(4).getAsDouble();
        ArcherTower.ATTACK_SPEED = archerDetails.get(5).getAsInt();
        ArcherTower.ATTACK_RANGE = archerDetails.get(6).getAsInt();

        CannonTower.INITIAL_PRICE = canonDetails.get(0).getAsInt();
        CannonTower.INITIAL_LEVEL_UP_PRICE = canonDetails.get(1).getAsInt();
        CannonTower.PRICE_COEFF = canonDetails.get(2).getAsDouble();
        CannonTower.INITIAL_DAMAGE = canonDetails.get(3).getAsInt();
        CannonTower.DAMAGE_COEFF = canonDetails.get(4).getAsDouble();
        CannonTower.ATTACK_SPEED = canonDetails.get(5).getAsInt();
        CannonTower.ATTACK_RANGE = canonDetails.get(6).getAsInt();

    }

    public void handleTurnMessage(Message msg) {

        Log.d(TAG, "-------------------------------------------- " + "parsing turn " + currentTurn + " started--------------------------------------------");

        ArrayList<Unit> unitsBeforeUpdate = new ArrayList<>();
        ArrayList<Tower> towersBeforeUpdate = new ArrayList<>();

        unitsBeforeUpdate.addAll(getMyUnits());
        unitsBeforeUpdate.addAll(getEnemyUnits());

        towersBeforeUpdate.addAll(getMyTowers());
        towersBeforeUpdate.addAll(getVisibleEnemyTowers());

        myAttackMap.empty();
        myDefenceMap.empty();

        Log.d(TAG, "My Units:");
        JsonArray myUnitsArray = msg.args.get(0).getAsJsonObject().getAsJsonArray("myunits");

        for (int i = 0; i < myUnitsArray.size(); i++) {

            JsonArray tmpUnit = myUnitsArray.get(i).getAsJsonArray();

            int uid = tmpUnit.get(0).getAsInt();
            int x = tmpUnit.get(4).getAsJsonObject().get("x").getAsInt();
            int y = tmpUnit.get(4).getAsJsonObject().get("y").getAsInt();
            int lvl = tmpUnit.get(2).getAsInt();
            int health = tmpUnit.get(3).getAsInt();
            int remtick = tmpUnit.get(5).getAsInt();
            int pid = tmpUnit.get(6).getAsInt();

            if (tmpUnit.get(1).getAsString().equals("l")) {

                LightUnit lightUnit = new LightUnit(x, y, Owner.ME, lvl, uid,health,myAttackMapPaths.get(pid));

                Cell tmpCell = myAttackMap.getCellsGrid()[y][x];
                if (tmpCell instanceof RoadCell)
                    ((RoadCell) tmpCell).getUnits().add(lightUnit);

                Log.d(TAG, lightUnit.toString());

            } else if (tmpUnit.get(1).getAsString().equals("h")) {

                HeavyUnit heavyUnit = new HeavyUnit(x, y, Owner.ME, lvl, uid,health,myAttackMapPaths.get(pid));

                Cell tmpCell = myAttackMap.getCellsGrid()[y][x];
                if (tmpCell instanceof RoadCell)
                    ((RoadCell) tmpCell).getUnits().add(heavyUnit);
                Log.d(TAG, heavyUnit.toString());
            }
        }

        Log.d(TAG, "Enemy Units:");
        JsonArray enemyUnitsArray = msg.args.get(0).getAsJsonObject().getAsJsonArray("enemyunits");

        for (int i = 0; i < enemyUnitsArray.size(); i++) {

            JsonArray tmpEnemyUnit = enemyUnitsArray.get(i).getAsJsonArray();

            int uid = tmpEnemyUnit.get(0).getAsInt();
            int x = tmpEnemyUnit.get(3).getAsJsonObject().get("x").getAsInt();
            int y = tmpEnemyUnit.get(3).getAsJsonObject().get("y").getAsInt();
            int lvl = tmpEnemyUnit.get(2).getAsInt();

            if (tmpEnemyUnit.get(1).getAsString().equals("l")) {

                LightUnit lightUnit = new LightUnit(x, y, Owner.ENEMY, lvl, uid,0,null);

                Cell tmpCell = myDefenceMap.getCellsGrid()[y][x];
                if (tmpCell instanceof RoadCell)
                    ((RoadCell) tmpCell).getUnits().add(lightUnit);

                Log.d(TAG, lightUnit.toString());

            } else if (tmpEnemyUnit.get(1).getAsString().equals("h")) {

                HeavyUnit heavyUnit = new HeavyUnit(x, y, Owner.ENEMY, lvl, uid,0,null);

                Cell tmpCell = myDefenceMap.getCellsGrid()[y][x];
                if (tmpCell instanceof RoadCell)
                    ((RoadCell) tmpCell).getUnits().add(heavyUnit);
                Log.d(TAG, heavyUnit.toString());
            }
        }

        Log.d(TAG, "My Towers:");
        JsonArray myTowersArray = msg.args.get(0).getAsJsonObject().getAsJsonArray("mytowers");

        for (int i = 0; i < myTowersArray.size(); i++) {

            JsonArray tmpMyTower = myTowersArray.get(i).getAsJsonArray();

            int tid = tmpMyTower.get(0).getAsInt();
            int lvl = tmpMyTower.get(2).getAsInt();
            int x = tmpMyTower.get(3).getAsJsonObject().get("x").getAsInt();
            int y = tmpMyTower.get(3).getAsJsonObject().get("y").getAsInt();

            if (tmpMyTower.get(1).getAsString().equals("a")) {

                ArcherTower archerTower = new ArcherTower(x, y, Owner.ME, lvl, tid);
                Cell tmpCell = myDefenceMap.getCellsGrid()[y][x];

                if (tmpCell instanceof GrassCell) {
                        myDefenceMap.getCellsGrid()[y][x]=new GrassCell(x,y,archerTower);
                }
                Log.d(TAG, archerTower.toString());

            } else if (tmpMyTower.get(1).getAsString().equals("c")) {

                CannonTower canonTower = new CannonTower(x, y, Owner.ME, lvl, tid);
                Cell tmpCell = myDefenceMap.getCellsGrid()[y][x];

                if (tmpCell instanceof GrassCell) {
                    myDefenceMap.getCellsGrid()[y][x]=new GrassCell(x,y,canonTower);
                }
                Log.d(TAG, canonTower.toString());
            }
        }

        Log.d(TAG, "Enemy Towers:");
        JsonArray enemyTowersArray = msg.args.get(0).getAsJsonObject().getAsJsonArray("enemytowers");

        for (int i = 0; i < enemyTowersArray.size(); i++) {

            JsonArray tmpEnemyTower = enemyTowersArray.get(i).getAsJsonArray();

            int tid = tmpEnemyTower.get(0).getAsInt();
            int lvl = tmpEnemyTower.get(2).getAsInt();
            int x = tmpEnemyTower.get(3).getAsJsonObject().get("x").getAsInt();
            int y = tmpEnemyTower.get(3).getAsJsonObject().get("y").getAsInt();

            if (tmpEnemyTower.get(1).getAsString().equals("a")) {

                ArcherTower archerTower = new ArcherTower(x, y, Owner.ENEMY, lvl, tid);
                Cell tmpCell = myAttackMap.getCellsGrid()[y][x];

                if (tmpCell instanceof GrassCell) {
                    myAttackMap.getCellsGrid()[y][x]=new GrassCell(x,y,archerTower);
                }
                Log.d(TAG, archerTower.toString());

            } else if (tmpEnemyTower.get(1).getAsString().equals("c")) {

                CannonTower canonTower = new CannonTower(x, y, Owner.ENEMY, lvl, tid);
                Cell tmpCell = myAttackMap.getCellsGrid()[y][x];

                if (tmpCell instanceof GrassCell) {
                    myAttackMap.getCellsGrid()[y][x]=new GrassCell(x,y,canonTower);
                }
                Log.d(TAG, canonTower.toString());
            }
        }

        Log.d(TAG, "Players Update:");
        JsonArray playersDetails = msg.args.get(0).getAsJsonObject().getAsJsonArray("players");
        JsonArray myDetails = playersDetails.get(0).getAsJsonArray();
        JsonArray enemyDetails = playersDetails.get(1).getAsJsonArray();

        int myHealth = myDetails.get(0).getAsInt();
        int myMoney = myDetails.get(1).getAsInt();
        int myIncome = myDetails.get(2).getAsInt();
        int myRemBeans = myDetails.get(3).getAsInt();
        int myRemStorms = myDetails.get(4).getAsInt();

        players[0] = new Player(myMoney, myIncome, myHealth, myRemBeans, myRemStorms);
        Log.d(TAG, "Me" + players[0]);
        int enemyHealth = enemyDetails.get(0).getAsInt();
        int enemyRemBeans = enemyDetails.get(1).getAsInt();
        int enemyRemStorms = enemyDetails.get(2).getAsInt();

        players[1] = new Player(0, 0, enemyHealth, enemyRemBeans, enemyRemStorms);
        Log.d(TAG, "Enemy" + players[1]);

        Log.d(TAG, "Events:");
        JsonObject eventsObject = msg.args.get(0).getAsJsonObject().get("events").getAsJsonObject();

        JsonArray deadunitsArray = eventsObject.getAsJsonArray("deadunits");

        deadUnitsInThisCycle = new ArrayList<>();
        for (int i = 0; i < deadunitsArray.size(); i++) {
            JsonArray tmpDead = deadunitsArray.get(i).getAsJsonArray();
            Log.d(TAG, "Unit Died-> " + "isMymap:" + tmpDead.get(0).getAsBoolean() + " - uId:" + tmpDead.get(1).getAsString());//function for this shit.

            for (int j = 0; j < unitsBeforeUpdate.size(); j++) {
                if (unitsBeforeUpdate.get(j).getId() == tmpDead.get(1).getAsInt()) {
                    deadUnitsInThisCycle.add(unitsBeforeUpdate.get(j));
                }
            }
        }

        JsonArray endOfPathArray = eventsObject.getAsJsonArray("endofpath");
        passedUnitsInThisCycle = new ArrayList<>();
        for (int i = 0; i < endOfPathArray.size(); i++) {
            JsonArray tmpEnd = endOfPathArray.get(i).getAsJsonArray();
            Log.d(TAG, "Unit reached End Of Path-> " + "isMymap:" + tmpEnd.get(0).getAsBoolean() + " - uId:" + tmpEnd.get(1).getAsString());//function for this shit

            for (int j = 0; j < unitsBeforeUpdate.size(); j++) {
                if (unitsBeforeUpdate.get(j).getId() == tmpEnd.get(1).getAsInt()) {
                    passedUnitsInThisCycle.add(unitsBeforeUpdate.get(j));
                }
            }
        }

        JsonArray desTowersArray = eventsObject.getAsJsonArray("destroyedtowers");
        destroyedTowersInThisCycle = new ArrayList<>();
        for (int i = 0; i < desTowersArray.size(); i++) {
            JsonArray tmpTower = desTowersArray.get(i).getAsJsonArray();

            int tid = tmpTower.get(1).getAsInt();
            boolean isMymap = tmpTower.get(0).getAsBoolean();

            Log.d(TAG, "Tower destroyed  -> " + "isMymap:" + isMymap + " - tId:" + tid);

            for (int j = 0; j < towersBeforeUpdate.size(); j++) {
                if (towersBeforeUpdate.get(j).getId() == tid)
                    destroyedTowersInThisCycle.add(towersBeforeUpdate.get(j));
            }
        }

        JsonArray beansArray = eventsObject.getAsJsonArray("beans");
        beansInThisCycle = new ArrayList<>();
        for (int i = 0; i < beansArray.size(); i++) {
            JsonArray tmpBean = beansArray.get(i).getAsJsonArray();

            int x = tmpBean.get(1).getAsJsonObject().get("x").getAsInt();
            int y = tmpBean.get(1).getAsJsonObject().get("y").getAsInt();

            boolean isMymap = tmpBean.get(0).getAsBoolean();

            Log.d(TAG, "Bean planted At x:" + x + ",y:" + y + " -> " + "isMyMap:" + isMymap);
            if (!isMymap) {
                beansInThisCycle.add(new BeanEvent(Owner.ME, new Point(x, y)));
                getAttackMap().getCellsGrid()[y][x] = new BlockCell(x, y);
            } else {
                beansInThisCycle.add(new BeanEvent(Owner.ENEMY, new Point(x, y)));
                getDefenceMap().getCellsGrid()[y][x] = new BlockCell(x, y);
            }
        }

        JsonArray stormsArray = eventsObject.getAsJsonArray("storms");
        stormsInThisCycle = new ArrayList<>();
        for (int i = 0; i < stormsArray.size(); i++) {
            JsonArray tmpstorm = stormsArray.get(i).getAsJsonArray();

            int x = tmpstorm.get(1).getAsJsonObject().get("x").getAsInt();
            int y = tmpstorm.get(1).getAsJsonObject().get("y").getAsInt();
            boolean isMymap = tmpstorm.get(0).getAsBoolean();

            if (!isMymap)
                stormsInThisCycle.add(new StormEvent(Owner.ENEMY, new Point(x, y)));
            else stormsInThisCycle.add(new StormEvent(Owner.ME, new Point(x, y)));
            Log.d(TAG, "Storm created At x:" + x + ",y:" + y + " -> " + "isMyMap:" + isMymap);
        }
        Log.d(TAG, "-------------------------------------------- parsing turn " + currentTurn + " finished--------------------------------------------");
    }

    public void createArcherTower(int lvl, int x, int y) {

        Event event = new Event("ct", new Object[]{"a", lvl, x, y});
        sender.accept(new Message(Event.EVENT, event));
        Log.d(TAG, "Request: create ArcherTower @ x:" + x + " y:" + y + " Level:" + lvl);
    }

    public void createCannonTower(int lvl, int x, int y) {

        Event event = new Event("ct", new Object[]{"c", lvl, x, y});
        sender.accept(new Message(Event.EVENT, event));
        Log.d(TAG, "Request: create CannonTower @ x:" + x + " y:" + y + " Level:" + lvl);

    }

    public void createLightUnit(int pathIndex) {

        Event event = new Event("cu", new Object[]{"l", pathIndex});
        sender.accept(new Message(Event.EVENT, event));

        Log.d(TAG, "Request: create LightUnit @ path number:" + pathIndex);

    }

    public void createHeavyUnit(int pathIndex) {

        Event event = new Event("cu", new Object[]{"h", pathIndex});
        sender.accept(new Message(Event.EVENT, event));

        Log.d(TAG, "Request: create HeavyUnit @ path number:" + pathIndex);
    }

    public void upgradeTower(int tid) {

        Event event = new Event("ut", new Object[]{tid});
        sender.accept(new Message(Event.EVENT, event));

        Log.d(TAG, "Request: upgrade tower with TowerId:" + tid);
    }

    public void upgradeTower(Tower tower) {
        upgradeTower(tower.getId());
    }

    public void plantBean(int x, int y) {

        Event event = new Event("b", new Object[]{x, y});
        sender.accept(new Message(Event.EVENT, event));
        Log.d(TAG, "Request: plant bean @ x:" + x + " y:" + y);
    }

    public void createStorm(int x, int y) {

        Event event = new Event("s", new Object[]{x, y});
        sender.accept(new Message(Event.EVENT, event));
        Log.d(TAG, "Request: create storm @ x:" + x + " y:" + y);
    }

    public ArrayList<Unit> getMyUnits() {

        ArrayList<Unit> myUnits = new ArrayList<>();

        for (int i = 0; i < myAttackMap.getHeight(); i++) {
            for (int j = 0; j < myAttackMap.getWidth(); j++) {
                Cell tmp = myAttackMap.getCellsGrid()[i][j];
                if (tmp instanceof RoadCell) {
                    for (int k = 0; k < ((RoadCell) tmp).getUnits().size(); k++) {
                        if (((RoadCell) tmp).getUnits().get(k).getOwner() == Owner.ME) {
                            myUnits.add(((RoadCell) tmp).getUnits().get(k));
                        }
                    }
                }
            }
        }

        return myUnits;
    }

    public ArrayList<Unit> getEnemyUnits() {

        ArrayList<Unit> enemyUnits = new ArrayList<Unit>();

        for (int i = 0; i < myDefenceMap.getHeight(); i++) {
            for (int j = 0; j < myDefenceMap.getWidth(); j++) {
                Cell tmp = myDefenceMap.getCellsGrid()[i][j];
                if (tmp instanceof RoadCell) {
                    for (int k = 0; k < ((RoadCell) tmp).getUnits().size(); k++) {
                        if (((RoadCell) tmp).getUnits().get(k).getOwner() == Owner.ENEMY) {
                            enemyUnits.add(((RoadCell) tmp).getUnits().get(k));
                        }
                    }
                }
            }
        }
        return enemyUnits;
    }

    public ArrayList<Tower> getMyTowers() {

        ArrayList<Tower> myTowers = new ArrayList<>();

        for (int i = 0; i < myDefenceMap.getHeight(); i++) {
            for (int j = 0; j < myDefenceMap.getWidth(); j++) {
                Cell tmp = myDefenceMap.getCellsGrid()[i][j];
                if (tmp instanceof GrassCell) {
                    try {
                        if (((GrassCell) tmp).getTower().getOwner() == Owner.ME) {
                            myTowers.add(((GrassCell) tmp).getTower());
                        }
                    } catch (Exception e) {
                    }

                }
            }
        }
        return myTowers;
    }

    public ArrayList<Tower> getVisibleEnemyTowers() {

        ArrayList<Tower> enemyTowers = new ArrayList<Tower>();

        for (int i = 0; i < myAttackMap.getHeight(); i++) {
            for (int j = 0; j < myAttackMap.getWidth(); j++) {
                Cell tmp = myAttackMap.getCellsGrid()[i][j];
                if (tmp instanceof GrassCell) {
                    try {
                        if (((GrassCell) tmp).getTower().getOwner() == Owner.ENEMY) {
                            enemyTowers.add(((GrassCell) tmp).getTower());
                        }
                    } catch (Exception e) {
                    }

                }
            }
        }
        return enemyTowers;
    }


    public Player getEnemyInformation() {
        return players[1];
    }

    public Player getMyInformation() {
        return players[0];
    }

    public Map getAttackMap() {
        return myAttackMap;
    }

    public Map getDefenceMap() {
        return myDefenceMap;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isTowerConstructable(Cell cell) {
        if (cell instanceof RoadCell)
            return false;

        else if (cell instanceof BlockCell)
            return false;

        else if (cell instanceof GrassCell) {

            if (((GrassCell) cell).getTower() != null)
                return false;

            else {

                int x = cell.getLocation().getX();
                int y = cell.getLocation().getY();

                ArrayList<Cell> adjnct = new ArrayList<>();

                try {
                    adjnct.add(myDefenceMap.getCellsGrid()[y - 1][x]);
                } catch (Exception e) {
                }

                try {
                    adjnct.add(myDefenceMap.getCellsGrid()[y + 1][x]);
                } catch (Exception e) {
                }

                try {
                    adjnct.add(myDefenceMap.getCellsGrid()[y][x + 1]);
                } catch (Exception e) {
                }

                try {
                    adjnct.add(myDefenceMap.getCellsGrid()[y][x - 1]);
                } catch (Exception e) {
                }


                for (int i = 0; i < adjnct.size(); i++) {
                    if (adjnct.get(i) instanceof GrassCell) {
                        if (((GrassCell) adjnct.get(i)).getTower() != null)
                            return false;
                    }
                }

                return true;

            }

        }
        return false;
    }

    @Override
    public ArrayList<Path> getAttackMapPaths() {
        return myAttackMapPaths;
    }

    @Override
    public ArrayList<Path> getDefenceMapPaths() {
        return myDefenceMapPaths;
    }

    @Override
    public ArrayList<BeanEvent> getBeansInThisTurn() {
        return beansInThisCycle;
    }

    @Override
    public ArrayList<StormEvent> getStormsInThisTurn() {
        return stormsInThisCycle;
    }

    @Override
    public ArrayList<Tower> getDestroyedTowersInThisTurn() {
        return destroyedTowersInThisCycle;
    }

    @Override
    public ArrayList<Unit> getDeadUnitsInThisTurn() {
        return deadUnitsInThisCycle;
    }

    @Override
    public ArrayList<Unit> getPassedUnitsInThisTurn() {
        return passedUnitsInThisCycle;
    }
}

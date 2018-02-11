package client;

import client.classes.RoadInfo;
import client.model.*;
import common.util.Log;
import javafx.scene.effect.Light;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static common.network.JsonSocket.TAG;

/**
 * AI class.
 * You should fill body of the method {@link }.
 * Do not change name or modifiers of the methods or fields
 * and do not add constructor for this class.
 * You can add as many methods or fields as you want!
 * Use world parameter to access and modify game's
 * world!
 * See World interface for more details.
 */
public class AI {

    final double thresholdCoeff = 8;
    final double maximumEstimatedCost = 5000.0;
    final double soldierNormalizeFactor = 1.0 / 300.0;
    final double damageNormalizeFactor = 1.0 / 50.0;
    final double initialWeight = 50.0;
    final double constructionCostFactor = 1.0 / 300.0;

    final double scoreCostCoeff = 100.0;
    final double scoreHealthCoeff = -20.0;
    final double scoreHeatMapCoeff = -20.0;

    final int learningMode = 0;

    Random rnd = new Random();
    String mode, geneFile;
    double[] gene;

    int totalCost;
    double spendingMoney;

    RoadInfo[] roadInfos;

    int attackState = 2;

    public AI(String mode, String geneFile) {
        this.mode = mode;
        this.geneFile = geneFile;

        gene = GetGene();

        totalCost = 0;
    }

    void simpleTurn(World game) {
        turn(game);

//        Log.d(TAG,"lightTurn Called"+" Turn:"+game.getCurrentTurn());
//
//        int t=rnd.nextInt();
//        if(t%3==2){
//            game.createArcherTower(rnd.nextInt(4),rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));
//
//        }else if(t%3==1){
//            game.createHeavyUnit(rnd.nextInt(game.getDefenceMapPaths().size()));
//
//        }else if(t%3==0){
//            game.createLightUnit(rnd.nextInt(game.getAttackMapPaths().size()));
//        }
    }

    void complexTurn(World game) {
        turn(game);

//        Log.d(TAG,"HeavyTurn Called"+" Turn:"+game.getCurrentTurn());
//
//        int t=rnd.nextInt();
//        if(t%3==2){
//            game.createStorm(rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));
//        }else if(t%3==1){
//            game.plantBean(rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));
//
//        }else if(t%3==0){
//            game.createCannonTower(rnd.nextInt(4),rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));
//
//        }
    }

    private void turn(World game) {
        spendingMoney = 0.0;

        if (mode.equals("attack"))
            sendAttacker(game);
        else if (mode.equals("defend")) {
            processGene(game);
        }
    }

    public void saveStats(World game) {
        double heatMapScore = roadInfos == null ? 0 : roadInfos[0].getHeatMapScore();
        double cost = scoreCostCoeff * (1 - totalCost / maximumEstimatedCost) +
                      scoreHealthCoeff * (Game.INITIAL_HEALTH - game.getMyInformation().getStrength()) +
                      scoreHeatMapCoeff * heatMapScore;

        String stats = "";
        stats += totalCost + System.lineSeparator();
        stats += game.getMyInformation().getStrength() + System.lineSeparator();
        stats += cost + System.lineSeparator();
        stats += heatMapScore + System.lineSeparator();

        System.out.println("Stats:" + System.lineSeparator() + stats);

        try {
            Files.write(Paths.get(geneFile + ".out"), stats.getBytes(), StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            System.err.println("Can't write stats file.");
        }
    }

    private void processGene(World game) {
        if (roadInfos == null)
            roadInfos = new RoadInfo[1];

        Path path = game.getDefenceMapPaths().get(0);

        if (roadInfos[0] == null)
            roadInfos[0] = new RoadInfo(path);
        else
            roadInfos[0].update(path);

        ArrayList<Double> state = generateState(game, path);

        double[] weights = new double[path.getRoad().size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = initialWeight;
            for (int j = 0; j < state.size(); j++) {
                //System.out.println(i + " * " + state.size() + " + " + j);
                weights[i] += gene[i * state.size() + j] * state.get(j);
            }
        }

        double[] weightsSorted = Arrays.copyOf(weights, weights.length);
        Arrays.sort(weightsSorted);

        double delta = weightsSorted[weights.length * 3 / 4] - weightsSorted[weights.length / 2];

        //if (weightsSorted[0] != 0 || weightsSorted[weightsSorted.length - 1] != 0)
        //    System.out.println("Adjab");

        int max = -1;

        System.out.print("Calculated weights are: ");
        for (int i = 0; i < weights.length; i++) {
            double d = weights[i] - weightsSorted[weights.length / 2];
            //if (d != 0)
            //System.out.println("Adjabbbb" + i + " : " + d + " , " + delta);
            if (d > thresholdCoeff * delta) {
                //if ((max == -1) || (weights[max] < weights[i])) {
                //    max = i;
                //}
                createTowerNear(game, path.getRoad().get(i).getPoint());
            }
            System.out.print(weights[i] + ", ");
            //if (weights[i] >= threshold) {
            //    createTowerNear(game, path.getRoad().get(i).getPoint());
            //}
        }
        System.out.println();

        System.out.println("----" + game.getCurrentTurn() + " : " + game.getMyTowers().size() + " , " + game.getMyInformation().getMoney());

        if (max == -1) {
            System.out.println("No action.");
        }
        else {
            createTowerNear(game, path.getRoad().get(max).getPoint());
        }
    }

    private ArrayList<Double> generateState(World game, Path path) {
        ArrayList<Double> state = new ArrayList<>();

        state.add(1.0);
        FillJoon(path, state);
        FillDamages(game, path, state);
        FillConstructionCosts(game, path, state);

        return state;
    }

    private double getCostForConstructionNear(World game, Point point) {
        for (int i = point.getX() - 1; i <= point.getX() + 1; i++) {
            for (int j = point.getY() - 1; j <= point.getY() + 1; j++) {
                if (IsOutOfBounds(game, i, j))
                    continue;
                if (game.getDefenceMap().getCell(i, j) instanceof GrassCell) {
                    if (((GrassCell)game.getDefenceMap().getCell(i, j)).getTower() != null)
                        continue;

                    if (HasTowerNeighbors(i, j, game))
                        continue;

                    return CannonTower.INITIAL_PRICE;
                }
            }
        }

        int minimumLevel = getMinimumTowerLevelNear(game, point);

        for (int i = point.getX() - 1; i <= point.getX() + 1; i++) {
            for (int j = point.getY() - 1; j <= point.getY() + 1; j++) {
                if (IsOutOfBounds(game, i, j))
                    continue;

                if (game.getDefenceMap().getCell(i, j) instanceof GrassCell) {
                    if (((GrassCell) game.getDefenceMap().getCell(i, j)).isEmpty())
                        continue;

                    Tower t = ((GrassCell) game.getDefenceMap().getCell(i, j)).getTower();

                    if (t.getLevel() > minimumLevel)
                        continue;

                    return (t.getPrice(t.getLevel() + 1) - t.getPrice());
                }
            }
        }


        return 0;
    }

    private int getMinimumTowerLevelNear(World game, Point point) {
        int minimumLevel = Integer.MAX_VALUE;

        for (int i = point.getX() - 1; i <= point.getX() + 1; i++) {
            for (int j = point.getY() - 1; j <= point.getY() + 1; j++) {
                if (IsOutOfBounds(game, i, j))
                    continue;
                if (game.getDefenceMap().getCell(i, j) instanceof GrassCell) {
                    if (((GrassCell) game.getDefenceMap().getCell(i, j)).isEmpty())
                        continue;

                    Tower t = ((GrassCell) game.getDefenceMap().getCell(i, j)).getTower();

                    if (t.getLevel() < minimumLevel)
                        minimumLevel = t.getLevel();
                }
            }
        }
        return minimumLevel;
    }


    private void createTowerNear(World game, Point point) {
        for (int i = point.getX() - 1; i <= point.getX() + 1; i++) {
            for (int j = point.getY() - 1; j <= point.getY() + 1; j++) {

                //TODO: Check this shit
                if (IsOutOfBounds(game, i, j))
                    continue;

                if (game.getDefenceMap().getCell(i, j) instanceof GrassCell) {
                    if (((GrassCell)game.getDefenceMap().getCell(i, j)).getTower() != null)
                        continue;

                    if (HasTowerNeighbors(i, j, game))
                        continue;

                    double potentialCost = CannonTower.INITIAL_PRICE;

                    if (hasMoneyAmount(game, potentialCost)) {
                        System.out.println("Creating a tower near " + point.getX() + ", " + point.getY() + " at " + i + ", " + j);

                        totalCost += potentialCost;
                        if (learningMode == 0)
                            game.createCannonTower(1, i, j);
                        else
                            game.createArcherTower(1, i, j);
                        moneySpent(potentialCost);
                    }
                    return;
                }
            }
        }

        int minimumLevel = getMinimumTowerLevelNear(game, point);

        for (int i = point.getX() - 1; i <= point.getX() + 1; i++) {
            for (int j = point.getY() - 1; j <= point.getY() + 1; j++) {

                //TODO: Check this shit
                if (IsOutOfBounds(game, i, j))
                    continue;

                if (game.getDefenceMap().getCell(i, j) instanceof GrassCell) {
                    if (((GrassCell) game.getDefenceMap().getCell(i, j)).isEmpty())
                        continue;

                    Tower t = ((GrassCell) game.getDefenceMap().getCell(i, j)).getTower();

                    if (t.getLevel() > minimumLevel)
                        continue;

                    double potentialCost = t.getPrice(t.getLevel() + 1) - t.getPrice();

                    if (hasMoneyAmount(game, potentialCost)) {
                        System.out.println("Upgrading tower (near " + point.getX() + ", " + point.getY() + ") at " + i + ", " + j);

                        totalCost += potentialCost;
                        game.upgradeTower(t.getId());
                        moneySpent(potentialCost);

                        return;
                    }
                }
            }
        }


        //System.out.println("Couldn't create or upgrade tower anywhere near " + point.getX() + ", " + point.getY() + "!");
    }

    private boolean hasMoneyAmount(World game, double potentialCost) {
        return ((game.getMyInformation().getMoney() - spendingMoney) >= potentialCost);
    }

    public void moneySpent(double amount) {
        spendingMoney += amount;
    }

    private boolean IsOutOfBounds(World game, int i, int j) {
        return i < 0 || j < 0 || i >= game.getDefenceMap().getWidth() || j >= game.getDefenceMap().getHeight();
    }

    private boolean HasTowerNeighbors(int i, int j, World game) {
        if (HasTower(i+1, j, game))
            return true;
        if (HasTower(i-1, j, game))
            return true;
        if (HasTower(i, j+1, game))
            return true;
        if (HasTower(i, j-1, game))
            return true;

        return false;
    }

    private boolean HasTower(int i, int j, World game) {
        if (IsOutOfBounds(game, i, j))
            return false;

        if (!(game.getDefenceMap().getCell(i, j) instanceof GrassCell))
            return false;

        return !((GrassCell) game.getDefenceMap().getCell(i, j)).isEmpty();
    }


    private void FillConstructionCosts(World game, Path path, ArrayList<Double> state) {
        for (RoadCell cell : path.getRoad()) {
            double cost = getCostForConstructionNear(game, cell.getPoint());
            double costNormalized = cost * constructionCostFactor;

            state.add(costNormalized);
        }
    }

    private void FillDamages(World game, Path path, ArrayList<Double> state) {
        for (RoadCell cell : path.getRoad()) {

            int totalDamage = 0;

            for (Tower tower : game.getMyTowers()) {
                Point towerPoint = tower.getPoint();
                int range = tower.getAttackRange();

                int distance = GetManhatanDistance(cell.getPoint(), towerPoint);

                if (distance <= range) {
                    totalDamage += tower.getDamage();
                }
            }

            System.out.print(totalDamage * damageNormalizeFactor + ", ");
            state.add(totalDamage * damageNormalizeFactor);
        }

        System.out.println("*********************");
    }

    private int GetManhatanDistance(Point point, Point towerPoint) {
        return Math.abs(point.getX() - towerPoint.getX()) + Math.abs(point.getY() - towerPoint.getY());
    }

    private void FillJoon(Path path, ArrayList<Double> state) {
        for (RoadCell cell : path.getRoad()) {
            int joon = 0;
            for (Unit u : cell.getUnits()) {
                if (u instanceof LightUnit)
                    joon += ((LightUnit)u).getMaximumHealth();
                else if (u instanceof HeavyUnit)
                    joon += ((HeavyUnit)u).getMaximumHealth();
            }

            state.add(joon * soldierNormalizeFactor);
            System.out.print(joon * soldierNormalizeFactor + ", ");
        }
        //System.out.println("");
    }

    private double[] GetGene() {
        double[] output;

        try (BufferedReader br = new BufferedReader(new FileReader(geneFile))) {
            int count = Integer.parseInt(br.readLine());

            output = new double[count];

            for (int i = 0; i < count; i++) {
                output[i] = Double.parseDouble(br.readLine());
            }

            return output;
        }
        catch (IOException e) {
            System.out.println("GeneFile not found.");
        }

        return null;
    }

    void sendAttacker(World game) {
        int turn = game.getCurrentTurn();
        System.out.println(turn);
/**/
        int period = 20;

        if (turn % period == 0) {
            //attackState = rnd.nextInt() % 2;

            //System.out.println("attackState = " + attackState);
        }

        if (attackState == 1) {
            if (turn % period == (period - 1)) {
                for (int i = 0; i < 20; i++)
                    game.createLightUnit(0);
            }
        }
        else if (attackState == 2) {
            game.createLightUnit(0);
        }
        else {/**/
            int count = (rnd.nextInt() % 10 - 5);
            if (count <= 0)
                return;

            for (int i = 0; i < count; i++)
                if (learningMode == 0)
                    game.createLightUnit(0);
                else
                    game.createHeavyUnit(0);
        }
    }
}

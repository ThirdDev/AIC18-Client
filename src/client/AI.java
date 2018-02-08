package client;

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

    final double thresholdCoeff = 1.5;
    final double maximumEstimatedCost = 5000.0;

    Random rnd = new Random();
    String mode, geneFile;
    double[] gene;

    int totalCost;

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

    void turn(World game) {
        if (mode.equals("attack"))
            sendAttacker(game);
        else if (mode.equals("defend")) {
            processGene(game);

            if (game.getCurrentTurn() % 10 == 9)
                saveStats(game);
        }

    }

    private void saveStats(World game) {

        double cost = ((1 - totalCost / maximumEstimatedCost) * 100) - 20 * (Game.INITIAL_HEALTH - game.getMyInformation().getStrength());


        String stats = "";
        stats += totalCost + "\n";
        stats += game.getMyInformation().getStrength() + "\n";
        stats += cost + "\n";

        System.out.println("Stats:\n" + stats);

        try {
            Files.write(Paths.get(geneFile + ".out"), stats.getBytes(), StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            System.err.println("Can't write stats file.");
        }
    }

    private void processGene(World game) {


        Path path = game.getDefenceMapPaths().get(0);
        ArrayList<Double> state = new ArrayList<>();

        FillJoon(path, state);
        FillDamages(game, path, state);

        double[] weights = new double[path.getRoad().size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 0;
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
        for (int i = 0; i < weights.length; i++) {
            double d = weights[i] - weightsSorted[weights.length / 2];
            //if (d != 0)
            //    System.out.println("Adjabbbb" + i + " : " + d + " , " + delta);
            if (d >= thresholdCoeff * delta) {
                if ((max == -1) || (weights[max] < weights[i])) {
                    max = i;
                }
            }
        }

        System.out.println("----" + game.getCurrentTurn() + " : " + game.getMyTowers().size() + " , " + game.getMyInformation().getMoney());

        if (max == -1) {
            System.out.println("No action.");
        }
        else {
            createTowerNear(game, path.getRoad().get(max).getPoint());
        }

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

                    System.out.println("Creating a tower near " + point.getX() + ", " + point.getY() + " at " + i + ", " + j);
                    game.createCannonTower(2, i, j);
                    return;
                }
            }
        }


        for (int i = point.getX() - 1; i <= point.getX() + 1; i++) {
            for (int j = point.getY() - 1; j <= point.getY() + 1; j++) {

                //TODO: Check this shit
                if (IsOutOfBounds(game, i, j))
                    continue;

                if (game.getDefenceMap().getCell(i, j) instanceof GrassCell) {
                    if (((GrassCell) game.getDefenceMap().getCell(i, j)).isEmpty())
                        continue;

                    Tower t = ((GrassCell) game.getDefenceMap().getCell(i, j)).getTower();

                    System.out.println("Upgrading tower (near " + point.getX() + ", " + point.getY() + ") at " + i + ", " + j);
                    game.upgradeTower(t.getId());
                    return;
                }
            }
        }


        System.out.println("Couldn't create or upgrade tower anywhere near " + point.getX() + ", " + point.getY() + "!");
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

            state.add((double)totalDamage);
        }
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

            state.add((double)joon);
            System.out.print(joon + ", ");
        }
        System.out.println("");
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
        System.out.println(game.getCurrentTurn());

        int count = (rnd.nextInt() % 10 - 5);
        if (count <= 0)
            return;

        for (int i = 0; i < count; i++)
            game.createLightUnit(0);
    }
}

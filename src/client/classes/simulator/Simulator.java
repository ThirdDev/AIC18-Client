package client.classes.simulator;

import client.classes.Logger;
import client.classes.simulator.judges.*;
import client.classes.simulator.towers.*;
import client.classes.simulator.units.*;
import client.classes.simulator.geneParsers.*;

import java.util.*;

public class Simulator {
    int pathLength;
    private int maximumTurns;
    List<Cannon> cannons;
    List<Archer> archers;

    public Simulator(int pathLength, int maximumTurns, List<Cannon> cannons, List<Archer> archers) {
        this.pathLength = pathLength;
        this.maximumTurns = maximumTurns;

        this.cannons = cannons;
        this.archers = archers;
    }

    public static byte[][] findBestGene(List<byte[][]> genes, Simulator simulator, Judge judge, long maximumTimeMilliseconds) {
        long startTime = System.currentTimeMillis();

        int pathLength = simulator.pathLength;

        byte[][] bestGene = null;
        double score = -9999999.0;

        int counter = 0;
        for (byte[][] gene : genes) {
            //Logger.print("Gene #" + counter);
            SimulationResult result = simulator.simulate(new MyGeneParser(gene));
            double currentScore = judge.calculateScore(result);
            counter++;
            //Logger.println(": " + currentScore);

            if (currentScore > score) {
                bestGene = gene;
            }

            long tDelta = System.currentTimeMillis() - startTime;
            if (tDelta > maximumTimeMilliseconds) {
                Logger.println("Simulator.findBestGene: maximum allowed time exceeded. Will report the best from examined candidates.");
                break;
            }
        }

        Logger.println("Examined " + counter + " genes.");

        return bestGene;
    }

    public SimulationResult simulate(GeneParser parser) {
        List<Unit> units = new ArrayList<>();
        List<Unit> deadUnits = new ArrayList<>();
        List<Unit> survivorUnits = new ArrayList<>();

        int elapsedTurns = 0;

        for (Cannon item : cannons)
            item.reset();
        for (Archer item : archers)
            item.reset();

        for (int i = 0; i < maximumTurns; i++) {
            processTowers(units);
            deadUnits.addAll(processDeadUnits(units));

            units.forEach(x -> x.goForward());
            survivorUnits.addAll(processSurvivedUnits(units));

            AttackAction action = parser.parse(i);
            for (int j = 0; j < action.getCountOfCreeps(); j++)
                units.add(new Creep());
            for (int j = 0; j < action.getCountOfHeros(); j++)
                units.add(new Hero());

            if ((units.size() == 0) && (i > parser.getGene().length / 2))
                break;

            elapsedTurns++;
        }

        //var creepPrice = new Creep().Price * (units.Count(x => x is Creep) + deadUnits.Count(x => x is Creep) + survivorUnits.Count(x => x is Creep));
        //var heroPrice = new Hero().Price * (units.Count(x => x is Hero) + deadUnits.Count(x => x is Hero) + survivorUnits.Count(x => x is Hero));

        int price = 0;
        for (Unit u : units)
            price += u.getPrice();
        for (Unit u : deadUnits)
            price += u.getPrice();
        for (Unit u : survivorUnits)
            price += u.getPrice();

        SimulationResult sr = new SimulationResult();


        int damageToEnemyBase = 0;
        for (Unit u : survivorUnits)
            damageToEnemyBase += u.getDamageToEnemyBase();

        int[] deadPositions = new int[deadUnits.size()];
        for (int i = 0; i < deadUnits.size(); i++)
            deadPositions[i] = deadUnits.get(i).position;

        sr.ReachedToTheEnd = survivorUnits.size();
        sr.DamagesToEnemyBase = damageToEnemyBase;
        sr.DeadPositions = deadPositions;
        sr.Length = pathLength;
        sr.Turns = elapsedTurns;
        sr.TotalPrice = price;

        return sr;
    }

    private List<Unit> processSurvivedUnits(List<Unit> units) {
        List<Unit> survivedUnits = new ArrayList<>();

        for (Unit u : units)
            if (u.position >= pathLength)
                survivedUnits.add(u);

        for (Unit u : survivedUnits)
            units.remove(u);

        return survivedUnits;
    }

    private List<Unit> processDeadUnits(List<Unit> units) {
        List<Unit> deadUnits = new ArrayList<>();

        for (Unit u : units)
            if (u.getHealth() <= 0)
                deadUnits.add(u);

        for (Unit u : deadUnits)
            units.remove(u);

        return deadUnits;
    }

    private void processTowers(List<Unit> units) {
        for (Tower item : cannons) {
            if (!item.canAttack())
                continue;

            Unit[] probablyAffectedUnits = units.stream().filter(x -> item.isInRange(x.position)).sorted((x1, x2) -> Integer.compare(x2.position, x1.position)).toArray(Unit[]::new);
            Unit[] affectedUnits = Arrays.stream(probablyAffectedUnits).filter(x -> x.position == probablyAffectedUnits[0].position).toArray(Unit[]::new);

            if (affectedUnits.length > 0) {
                item.attack();
                Arrays.stream(affectedUnits).forEach(x -> x.getAttackedByCannon());
            }
        }

        for (Tower item : archers) {
            if (!item.canAttack())
                continue;

            Unit affectedUnit = units.stream().filter(x -> item.isInRange(x.position)).sorted((x1, x2) -> Integer.compare(x2.position, x1.position)).findFirst().orElse(null);

            if (affectedUnit != null) {
                item.attack();
                affectedUnit.getAttackedByArcher();
            }
        }

        for (Tower item : cannons)
            item.turnPassed();
        for (Tower item : archers)
            item.turnPassed();
    }
}

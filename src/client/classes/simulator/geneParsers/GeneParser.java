package client.classes.simulator.geneParsers;

import client.classes.simulator.AttackAction;

public interface GeneParser {
    double[] getGene();

    AttackAction parse(int turn);
}

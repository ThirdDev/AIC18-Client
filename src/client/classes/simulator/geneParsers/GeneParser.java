package client.classes.simulator.geneParsers;

import client.classes.simulator.AttackAction;

public interface GeneParser {
    byte[][] getGene();

    AttackAction parse(int turn);
}

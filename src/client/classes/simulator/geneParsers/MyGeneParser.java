package client.classes.simulator.geneParsers;

import client.classes.simulator.AttackAction;

public class MyGeneParser implements GeneParser {

    double[] gene;
    int len;

    @Override
    public double[] getGene() {
        return gene;
    }

    public MyGeneParser(double[] gene, int len) {
        this.gene = gene;
        this.len = len;
    }

    @Override
    public AttackAction parse(int turn) {
        if (turn >= len)
            return new AttackAction(0, 0);

        double a = gene[turn % len];
        double b = gene[(turn % len) + len];

        return new AttackAction(geneToTroopCount(a), geneToTroopCount(b));

    }

    public static int geneToTroopCount(double a) {
        return (int) Math.max(7, a) - 7;
    }
}

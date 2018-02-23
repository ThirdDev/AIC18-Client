package client.classes.simulator.geneParsers;

import client.classes.simulator.AttackAction;

public class MyGeneParser implements GeneParser {

    byte[][] gene;
    int len;

    @Override
    public byte[][] getGene() {
        return gene;
    }

    public MyGeneParser(byte[][] gene) {
        this.gene = gene;
        this.len = gene[0].length;
    }

    @Override
    public AttackAction parse(int turn) {
        if (turn >= len)
            return new AttackAction(0, 0);

        byte a = gene[0][turn];
        byte b = gene[1][turn];

        return new AttackAction(a, b);

    }
}

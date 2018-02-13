package client.IntelligentAgents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Gene {

    double gene[];

    public Gene(double[] gene) {
        this.gene = gene;
    }

    public Gene(String geneFileAddress) {

        try (BufferedReader br = new BufferedReader(new FileReader(geneFileAddress))) {
            int count = Integer.parseInt(br.readLine());

            gene = new double[count];

            for (int i = 0; i < count; i++) {
                gene[i] = Double.parseDouble(br.readLine());
            }

        }
        catch (IOException e) {
            System.err.println("GeneFile not found.");
        }
    }
}

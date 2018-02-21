package client.classes.simulator.judges;

import client.classes.simulator.SimulationResult;

public interface Judge {
    double calculateScore(SimulationResult simulationResult);
}

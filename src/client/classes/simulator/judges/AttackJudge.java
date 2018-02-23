package client.classes.simulator.judges;

import client.classes.simulator.SimulationResult;

public class AttackJudge implements Judge {
    public double calculateScore(SimulationResult simulationResult) {
        return simulationResult.DamagesToEnemyBase;
    }
}

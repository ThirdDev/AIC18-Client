package client.ahmadalli.defend;

import client.classes.Logger;
import client.model.Cell;
import client.model.RoadCell;
import client.model.World;

public class StormCreation {
    public static void stormIfNecessary(World world) {
        if (world.getMyInformation().getStormsLeft() == 0)
            return;
        Logger.println("starting storm if necessary");

        RoadCell[] dangerousInRange3Ordered = client.ahmadalli.utils.RoadCell.orderByDamageScoreAscending(
                world.getDefenceMapPaths(),
                0.2, 3, 3);
        if (dangerousInRange3Ordered.length == 0) {
            Logger.println("no dangerous cell found in range 3 of the end of the map");
            return;
        }

        RoadCell mostDangerous = dangerousInRange3Ordered[0];
        Cell bestShot = client.ahmadalli.utils.RoadCell.centerOfMostDangerousAreaContainingRoadCell(mostDangerous, world.getDefenceMap(), 2);
        int bestShotScore = client.ahmadalli.scoring.Cell.stormCoveredDamageScore(bestShot, world.getDefenceMap(), 2);
        int mostDangerousScore = client.ahmadalli.scoring.RoadCell.damageToBaseScore(mostDangerous);
        Logger.println("bestShotScore: " + bestShotScore + ", mostDangerousScore: " + mostDangerousScore +
                ", current strength: " + world.getMyInformation().getStrength());
        if ((bestShotScore >= 8) || (mostDangerousScore >= world.getMyInformation().getStrength())) {
            Logger.println("storm is necessary. storming @(" + bestShot.getLocation().getX() + ", " + bestShot.getLocation().getY());
            world.createStorm(bestShot.getLocation().getX(), bestShot.getLocation().getY());
        }

    }
}

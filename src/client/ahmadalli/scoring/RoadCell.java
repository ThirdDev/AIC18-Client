package client.ahmadalli.scoring;

import client.Util;
import client.model.Cell;
import client.model.HeavyUnit;
import client.model.LightUnit;
import client.model.Map;

public class RoadCell {

    public static double unitScore(client.model.RoadCell roadCell) {
        int creepCount = client.ahmadalli.utils.RoadCell.creepCount(roadCell);
        int heroCount = client.ahmadalli.utils.RoadCell.heroCount(roadCell);

        return creepCount * 1.0 + heroCount * 2.5;
    }

    public static int damageToBaseScore(client.model.RoadCell roadCell) {
        long creepsCount = client.ahmadalli.utils.RoadCell.creepCount(roadCell);
        long herosCount = client.ahmadalli.utils.RoadCell.heroCount(roadCell);

        long score = 0;

        score += creepsCount * LightUnit.DAMAGE;
        score += (herosCount) * HeavyUnit.DAMAGE;

        return (int) score;
    }
}

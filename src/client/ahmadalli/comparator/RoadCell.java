package client.ahmadalli.comparator;

import java.util.Comparator;

public class RoadCell {
    public static Comparator<client.model.RoadCell> byDamageToBaseScore() {
        return Comparator.comparingDouble(o -> -client.ahmadalli.scoring.RoadCell.damageToBaseScore(o));
    }
}

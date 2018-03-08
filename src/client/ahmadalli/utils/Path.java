package client.ahmadalli.utils;

import client.model.Map;
import client.model.PredictionReport;

public class Path {
    public static boolean isPathVulnerable(client.model.Path path, Map map) {
        PredictionReport pathReport = path.getReport(map);
        return (pathReport.getCreepDamageToBase() + pathReport.getHeroDamageToBase()) > 0;
    }
}

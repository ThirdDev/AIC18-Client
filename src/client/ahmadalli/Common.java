package client.ahmadalli;

import client.Util;
import client.classes.Logger;
import client.model.*;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Common {
    public static void initialize(World world) {
    }

    public static void beanCheck(World world) {
        for (BeanEvent beanEvent : world.getBeansInThisTurn()) {
            Cell beanned;
            if (beanEvent.getOwner() == Owner.ENEMY)
                beanned = world.getDefenceMap().getCell(beanEvent.getPoint().getX(), beanEvent.getPoint().getY());
            else {
                beanned = world.getAttackMap().getCell(beanEvent.getPoint().getX(), beanEvent.getPoint().getY());
            }

            if (!(beanned instanceof BlockCell)) {
                Logger.error("fuck fuck fuck beans on map aren't right");
            }
        }
    }
}

package de.fhdw.robocode;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;

import robocode.ScannedRobotEvent;

public class Bob extends AdvancedRobot {

    @Override
    public void run() {

        double radius = 100.0;
        double angle = 90.0;

        while (true) {
            setAhead(5);
            setTurnRadarLeft(5);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        //fire(1);
        setGunColor(Color.BLACK);
        setTurnGunRight(5);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        //turnLeft(90 - e.getBearing());
    }

}

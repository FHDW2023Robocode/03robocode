package de.fhdw.robocode;

import java.util.logging.LogManager;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class NetterName extends AdvancedRobot {
    @Override
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        turnRadarRightRadians(Double.POSITIVE_INFINITY);
        do {
            scan();
            if (getScannedRobotEvents().isEmpty()) {
                
                turnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
        } while (true);
    }
    long fireTime = 0;
    void doGun(double power) {
        if (fireTime == getTime() && getGunTurnRemaining() == 0) {
            setFire(power);
        }

        // ... aiming code ...
        syncGunToRadar();
        // Don't need to check whether gun turn will complete in single turn because
        // we check that gun is finished turning before calling setFire(...).
        // This is simpler since the precise angle your gun can move in one tick
        // depends on where your robot is turning.
        fireTime = getTime() + 1;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double radarTurn =
                // Absolute bearing to target
                getHeadingRadians() + e.getBearingRadians()
                        // Subtract current radar heading to get turn required
                        - getRadarHeadingRadians();

        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        // ...
        
        doGun(3);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

    public void syncGunToRadar() {
        if (getRadarHeading() != getGunHeading()) {
            double angle = getRadarHeading() - getGunHeading();
            setTurnGunRight(angle);
        }
    }
}

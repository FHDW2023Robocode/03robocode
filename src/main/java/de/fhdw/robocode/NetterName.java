package de.fhdw.robocode;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;

import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class NetterName extends AdvancedRobot {

    @Override
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        turnRadarRightRadians(Double.POSITIVE_INFINITY);
        do {
            // Check for new targets.
            // Only necessary for Narrow Lock because sometimes our radar is already
            // pointed at the enemy and our onScannedRobot code doesn't end up telling
            // it to turn, so the system doesn't automatically call scan() for us
            // [see the javadocs for scan()].
            scan();
        } while (true);


    }
    long fireTime = 0;
    void doGun(double power) {
        if (fireTime == getTime() && getGunTurnRemaining() == 0) {
            setFire(power);
        }

        // ... aiming code ...

        setTurnGunRight(1);
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
        doGun(1);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

}

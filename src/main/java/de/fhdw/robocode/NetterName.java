package de.fhdw.robocode;

import java.util.logging.LogManager;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.RobotDeathEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class NetterName extends AdvancedRobot {
    private String enemy = null;
    private Boolean enemyDead = false;
    @Override
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        turnRadarRightRadians(Double.POSITIVE_INFINITY);
        do {
            scan();
            if (enemyDead) {
                turnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
        } while (true);
    }
    long fireTime = 0;
    void doGun(double power) {
        if (fireTime == getTime() && getGunTurnRemaining() == 0) {
            setFire(power);
        }
        syncGunToRadar();

        fireTime = getTime() + 1;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (enemy == null) {
            enemy = e.getName();
            enemyDead = false;
        }
        if (enemy.equals(e.getName())) {
            double radarTurn =
                    // Absolute bearing to target
                    getHeadingRadians() + e.getBearingRadians()
                    // Subtract current radar heading to get turn required
                            - getRadarHeadingRadians();

            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

            // ...
            doGun(3);
        }

    }

    public void syncGunToRadar() {

        if (getRadarHeading() != getGunHeading()) {
            double angle = getRadarHeading() - getGunHeading();
            setTurnGunRight(angle);
        }
    }

    public void onRobotDeath(RobotDeathEvent event) {
        // Überprüfe, ob der zerstörte Roboter der Gegner ist 
        if (event.getName().equals(enemy)) {
            enemyDead = true;
            enemy = null;
        }
    }
}

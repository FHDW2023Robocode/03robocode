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
    private double power = 3;
    double enemySpeed;
    double enemyDistans;
    double enemyAngle;
    double bulltSpeed = 20 - 3 * power;
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

            // calculate firepower based on distance and Velocity
            if (e.getVelocity()<=1) {
                power = 3;
            } else {
                power = Math.min(500 / e.getDistance(), 3);
            }

            // get Enemy Data

            // enemySpeed=e.getVelocity();//the velocity of the robot in pixel pro tick
            // enemyDistans=e.getDistance();//the distance to the robot in pixel
            // enemyAngle=e.getBearing();//Returns the bearing to the robot you scanned, relative to your robot's heading, in degrees (-180 <= getBearing() < 180)
            // ...
            syncGunToRadar();
            doGun();
        }

    }
    long fireTime = 0;
    void doGun() {
        if (fireTime == getTime()) { // && getGunTurnRemaining() == 0
            setFire(power);
        }
        // syncGunToRadar();

        fireTime = getTime() + 1;
    }
    public void syncGunToRadar() {
        if (getRadarHeading() != getGunHeading()) {
            double angle = getRadarHeading() - getGunHeading(); // der Winkel um der sich die Waffe drehen muss um auf den Gegener zu zeigen
            // double bulletSpeed = 20 - 3 * power;
            // double timeToHit = enemyDistans / bulletSpeed;
            // double predictedAngle = enemyAngle + (enemySpeed * timeToHit);
            // angle -= getRadarHeading() - getGunHeading() + predictedAngle;
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

    public void Movement() {

    }
    
}

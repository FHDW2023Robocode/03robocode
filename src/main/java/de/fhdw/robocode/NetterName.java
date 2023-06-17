package de.fhdw.robocode;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.util.logging.LogManager;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
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

    boolean movingForward; // Is set to true when setAhead is called, set to false on setBack
	boolean inWall; // Is true when robot is near the wall.
    @Override
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        turnRadarRightRadians(Double.POSITIVE_INFINITY);

        // Check if the robot is closer than 50px from the wall.
        if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50
                || getBattleFieldHeight() - getY() <= 50) {
            inWall = true;
        } else {
            inWall = false;
        }

        setAhead(40000); // go ahead until you get commanded to do differently
        setTurnRadarRight(360); // scan until you find your first enemy
        movingForward = true; // we called setAhead, so movingForward is true

        while (true) {
            scan();
            if (enemyDead) {
                turnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
            /**
            * Check if we are near the wall, and check if we have noticed (inWall boolean) yet.
            * If we have noticed yet, do nothing
            * If we have not noticed yet, reverseDirection and set inWall to true
            * If we are out of the wall, reset inWall
            */
            if (getX() > 50 && getY() > 50 && getBattleFieldWidth() - getX() > 50
                    && getBattleFieldHeight() - getY() > 50 && inWall == true) {
                inWall = false;
            }
            if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50
                    || getBattleFieldHeight() - getY() <= 50) {
                if (inWall == false) {
                    reverseDirection();
                    inWall = true;
                }
            }
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (enemy == null) {
            enemy = e.getName();
            enemyDead = false;
        }
        if (enemy.equals(e.getName())) {
        // Calculate exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
		double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
		
		//Spiral around our enemy. 90 degrees would be circling it (parallel at all times)
		// 80 and 100 make that we move a bit closer every turn.
		if (movingForward){
			setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 80));
		} else {
			setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 100));
		}

            double radarTurn =
                    // Absolute bearing to target
                    getHeadingRadians() + e.getBearingRadians()
                    // Subtract current radar heading to get turn required
                            - getRadarHeadingRadians();

            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

            // calculate firepower based on distance and Velocity
            if (e.getVelocity() <= 1) {
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
        setAhead(enemyDistans);
        setTurnLeft(enemyDistans);
        setTurnRight(enemyDistans);
    }

    public void onHitRobot(HitRobotEvent e) {
        // If we're moving the other robot, reverse!
        if (e.isMyFault()) {
            reverseDirection();
        }
    }

    /**
     * reverseDirection:  Switch from ahead to back & vice versa
     */
    public void reverseDirection() {
        if (movingForward) {
            setBack(40000);
            movingForward = false;
        } else {
            setAhead(40000);
            movingForward = true;
        }
    }
    /**
	 * onHitWall:  There is a small chance the robot will still hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Bounce off!
		reverseDirection();
	}
}

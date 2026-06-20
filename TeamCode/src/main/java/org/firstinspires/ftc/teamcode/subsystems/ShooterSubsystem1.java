package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ShooterSubsystem1 {

    private final DcMotorEx shooterL;
    private final DcMotorEx shooterR;

    private static final double FAST_VELOCITY = 1480;
    private static final double SLOW_VELOCITY = 1340;
    private static final double SPINUP_BOOST_POWER = 1.0;
    private static final double SPINUP_BOOST_ERROR = 120;
    private static final double READY_TOLERANCE = 80;
    private static final double SHOOTER_P = 90;
    private static final double SHOOTER_I = 0;
    private static final double SHOOTER_D = 3;
    private static final double SHOOTER_F = 24.5;

    public ShooterSubsystem1(HardwareMap hardwareMap) {

        shooterL = hardwareMap.get(DcMotorEx.class, "ShooterL");
        shooterR = hardwareMap.get(DcMotorEx.class, "ShooterR");

        shooterL.setDirection(DcMotor.Direction.REVERSE);
        shooterR.setDirection(DcMotor.Direction.FORWARD);

        shooterL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        shooterL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        shooterL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // FTC SDK built-in velocity controller
        shooterL.setVelocityPIDFCoefficients(
                SHOOTER_P, SHOOTER_I, SHOOTER_D, SHOOTER_F);
        shooterR.setVelocityPIDFCoefficients(
                SHOOTER_P, SHOOTER_I, SHOOTER_D, SHOOTER_F);
    }

    public void shootFast() {

        shootVelocity(FAST_VELOCITY);
    }

    public void shootSlow() {

        shootVelocity(SLOW_VELOCITY);
    }

    public void shootVelocity(double velocity) {

        if (getAverageVelocity() < velocity - SPINUP_BOOST_ERROR) {
            shooterL.setPower(SPINUP_BOOST_POWER);
            shooterR.setPower(SPINUP_BOOST_POWER);
        } else {
            shooterL.setVelocity(velocity);
            shooterR.setVelocity(velocity);
        }
    }

    public void reverse() {

        shooterL.setPower(-0.5);
        shooterR.setPower(-0.5);
    }

    public void stop() {

        shooterL.setVelocity(0);
        shooterR.setVelocity(0);
    }

    public boolean readyForFastShot() {

        return getAverageVelocity() >= FAST_VELOCITY - READY_TOLERANCE;
    }

    public boolean readyForSlowShot() {

        return getAverageVelocity() >= SLOW_VELOCITY - READY_TOLERANCE;
    }

    public double getAverageVelocity() {

        return (Math.abs(shooterL.getVelocity())
                + Math.abs(shooterR.getVelocity())) / 2.0;
    }

    public double getLeftVelocity() {
        return shooterL.getVelocity();
    }

    public double getRightVelocity() {

        return shooterR.getVelocity();
    }
}

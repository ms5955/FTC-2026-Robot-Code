package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class TurretSubsystem {

    private final DcMotorEx turret;
    public static final double MOTOR_TPR = 537.7;
    /*
    GEAR_RATIO
        = 84 / 19
        = 4.4210526316

    TICKS_PER_TURRET
        = 537.7 × 4.4210526316
        ≈ 2377.2 ticks
    */
    public static final double GEAR_RATIO = 84.0 / 19.0;      // 4.42105263

    public static final double TICKS_PER_TURRET_REV = MOTOR_TPR * GEAR_RATIO;

    private static final double TICKS_PER_DEGREE = 13.2047;

    private static final double MIN_ANGLE = -120.0;
    private static final double MAX_ANGLE = 120.0;

    private double currentPower = 0;
    private static final double MAX_POWER_CHANGE = 0.02;

    // PIDF
    private double kP = 0.0065;
    private double kI = 0.0000;
    private double kD = 0.00025;
    private double kF = 0.03;

    private double integral = 0;
    private double lastError = 0;

    private double lockedFieldAngle = 0;
    private double turretOffsetDeg = 0;

    private double lastPosition = 0;
    private double turretVelocity = 0;




    private final ElapsedTime pidTimer = new ElapsedTime();

    public TurretSubsystem(HardwareMap hardwareMap) {

        turret = hardwareMap.get(DcMotorEx.class, "turret");

        turret.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);


        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pidTimer.reset();
    }

    /**
     * Field-centric turret lock
     */
    public void update(double robotHeadingDeg) {

        double turretTargetDeg =
                lockedFieldAngle
                        - robotHeadingDeg
                        + turretOffsetDeg;

        turretTargetDeg = Math.max(
                MIN_ANGLE,
                Math.min(MAX_ANGLE, turretTargetDeg));

        int targetTicks =
                (int) (turretTargetDeg * TICKS_PER_DEGREE);

        int currentTicks =
                turret.getCurrentPosition();

        double error =
                targetTicks - currentTicks;

        // Deadband to prevent oscillation
        if (Math.abs(error) < 7) {
            turret.setPower(0);
            integral = 0;
            lastError = error;
            return;
        }

        double dt = pidTimer.seconds();

        pidTimer.reset();

        turretVelocity = (currentTicks - lastPosition) / Math.max(dt, 0.001);
        lastPosition = currentTicks;

        integral += error * dt;

        integral = Math.max(
                -5000,
                Math.min(5000, integral));

        double derivative =
                (error - lastError)
                        / Math.max(dt, 0.001);

        derivative = Math.max(-4000, Math.min(4000, derivative));

        double power =
                (kP * error)
                        + (kI * integral)
                        + (kD * derivative)
                        - (0.00003 * turretVelocity);

        if (Math.abs(error) > 200) {
            power += Math.signum(error) * 0.10;
        }
        else if (Math.abs(error) > 80) {
            power += Math.signum(error) * 0.06;
        }
        else if (Math.abs(error) > 20) {
            power += Math.signum(error) * 0.03;
        }

        power = Math.max(
                -1.0,
                Math.min(1.0, power));

        double maxPower;

        if (Math.abs(error) > 150) {
            maxPower = 0.45;
        }
        else if (Math.abs(error) > 50) {
            maxPower = 0.30;
        }
        else {
            maxPower = 0.18;
        }
        power = Math.max(-maxPower, Math.min(maxPower, power));
        turret.setPower(smoothPower(power));

        lastError = error;
    }

    /**
     * D-pad left
     */
    public void aimLeft() {
        lockedFieldAngle += 1;
    }

    /**
     * D-pad right
     */
    public void aimRight() {
        lockedFieldAngle -= 1;
    }

    private double smoothPower(double targetPower) {

        double delta = targetPower - currentPower;

        if (Math.abs(delta) > MAX_POWER_CHANGE) {
            delta = Math.signum(delta) * MAX_POWER_CHANGE;
        }

        currentPower += delta;

        return currentPower;
    }

    /**
     * Direct field angle set
     */
    public void setFieldAngle(double angle) {
        lockedFieldAngle = angle;
    }

    /**
     * Zero turret lock
     */
    public void resetLock() {
        lockedFieldAngle = 0;
    }

    /**
     * Encoder reset
     */
    public void resetEncoder() {
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * PID tuning
     */
    public void setPIDF(
            double p,
            double i,
            double d,
            double f) {

        kP = p;
        kI = i;
        kD = d;
        kF = f;
    }

    public void setOffset(double offsetDeg) {
        turretOffsetDeg = offsetDeg;
    }

    public void setLimelightOffset(double offsetDeg) {
        turretOffsetDeg = offsetDeg;
    }

    public void stop() {
        turret.setPower(0);
    }

    public int getPosition() {
        return turret.getCurrentPosition();
    }

    public double getLockedFieldAngle() {
        return lockedFieldAngle;
    }

    public double getKP() {
        return kP;
    }

    public double getKD() {
        return kD;
    }

    public double getKF() {
        return kF;
    }
}
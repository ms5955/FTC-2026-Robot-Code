package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class TurretTestSubsystem {

    private final DcMotorEx turret;

    // -------------------------
    // Encoder Constants
    // -------------------------

    private static final double MOTOR_TPR = 537.7;
    private static final double GEAR_RATIO = 84.0 / 19.0;
    private static final double TICKS_PER_REV = MOTOR_TPR * GEAR_RATIO;

    // -------------------------
    // Tune these only
    // -------------------------

    private double KP = 0.009;
    private double kI = 0.0000;
    private double KD = 0.0002;
    private double kF = 0.05;

    public double RESPONSE_GAIN = 12.0;

    public double MAX_POWER = 0.95;

    public double LOCK_RANGE = 0.6;

    public double MIN_ANGLE = -120;
    public double MAX_ANGLE = 120;

    // -------------------------

    private double initialAngle = 0;

    private double targetAngle = 0;

    private double lastError = 0;

    private int zeroTicks = 0;

    private final ElapsedTime timer = new ElapsedTime();

    public TurretTestSubsystem(HardwareMap hardwareMap) {

        turret = hardwareMap.get(DcMotorEx.class, "turret");

        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        timer.reset();
    }

    // -----------------------------------------------------

    public void setInitialAngle(double angle) {

        initialAngle = angle;

        targetAngle = angle;

        zeroTicks = turret.getCurrentPosition();
    }

    // -----------------------------------------------------

    public void setTargetAngle(double angle) {

        targetAngle = Math.max(MIN_ANGLE,
                Math.min(MAX_ANGLE, angle));
    }

    // -----------------------------------------------------

    public double getCurrentAngle() {

        int ticks = turret.getCurrentPosition() - zeroTicks;

        return initialAngle +
                (ticks * 360.0 / TICKS_PER_REV);

        // If reversed use:
        // return initialAngle -
        //      (ticks * 360.0 / TICKS_PER_REV);
    }

    // -----------------------------------------------------

    public void update() {

        double current = getCurrentAngle();

        double error = targetAngle - current;

        double dt = timer.seconds();

        timer.reset();

        if (dt < 0.001)
            dt = 0.001;

        double derivative =
                (error - lastError) / dt;

        lastError = error;

        double output =
                KP * Math.tanh(error / RESPONSE_GAIN)
                        + KD * derivative;

        output = Math.max(-MAX_POWER,
                Math.min(MAX_POWER, output));

        if (Math.abs(error) < LOCK_RANGE)
            output = 0;

        turret.setPower(output);
    }

    // -----------------------------------------------------

    public boolean locked() {

        return Math.abs(targetAngle - getCurrentAngle())
                < LOCK_RANGE;
    }

    // -----------------------------------------------------

    public double getTargetAngle() {
        return targetAngle;
    }

    public int getEncoder() {
        return turret.getCurrentPosition() - zeroTicks;
    }

}
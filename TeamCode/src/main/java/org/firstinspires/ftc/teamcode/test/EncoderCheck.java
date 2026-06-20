package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
@Disabled
@TeleOp(name = "Encoder Check", group = "Test")
public class EncoderCheck extends LinearOpMode {

    private DcMotorEx ShooterL;
    private DcMotorEx ShooterR;
    private DcMotorEx turret;

    private boolean lastAState = false;
    private boolean turretHoming = false;

    @Override
    public void runOpMode() {

        // Hardware Mapping
        ShooterL = hardwareMap.get(DcMotorEx.class, "ShooterL");
        ShooterR = hardwareMap.get(DcMotorEx.class, "ShooterR");
        turret   = hardwareMap.get(DcMotorEx.class, "turret");

        // Shooter Directions
        ShooterL.setDirection(DcMotor.Direction.FORWARD);
        ShooterR.setDirection(DcMotor.Direction.REVERSE);

        // Reset Encoders at Startup
        ShooterL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ShooterR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        ShooterL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ShooterR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("Encoder Check Ready");
        telemetry.addLine("Left Stick Y = Turret");
        telemetry.addLine("Right Bumper = Shooter");
        telemetry.addLine("A = Return Turret To Position 0");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // =========================
            // TURRET MANUAL CONTROL
            // =========================
            if (!turretHoming) {
                turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turret.setPower(gamepad1.left_stick_y);
            }

            // =========================
            // SHOOTER CONTROL
            // =========================
            if (gamepad1.right_bumper) {
                ShooterL.setPower(0.4);
                ShooterR.setPower(0.4);
            } else {
                ShooterL.setPower(0);
                ShooterR.setPower(0);
            }

            // =========================
            // A BUTTON -> RETURN TO ZERO
            // =========================
            boolean currentAState = gamepad1.a;

            if (currentAState && !lastAState) {

                turret.setTargetPosition(0);
                turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                turret.setPower(0.6);

                turretHoming = true;
            }

            lastAState = currentAState;

            // =========================
            // CHECK HOMING COMPLETE
            // =========================
            if (turretHoming && !turret.isBusy()) {

                turret.setPower(0);
                turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                turretHoming = false;
            }

            // =========================
            // TELEMETRY
            // =========================

            telemetry.addLine("===== ENCODER POSITIONS =====");
            telemetry.addData("Turret Position", turret.getCurrentPosition());
            telemetry.addData("Shooter L Position", ShooterL.getCurrentPosition());
            telemetry.addData("Shooter R Position", ShooterR.getCurrentPosition());

            telemetry.addLine("");

            telemetry.addLine("===== VELOCITIES =====");
            telemetry.addData("Turret Velocity", "%.1f", turret.getVelocity());
            telemetry.addData("Shooter L Velocity", "%.1f", ShooterL.getVelocity());
            telemetry.addData("Shooter R Velocity", "%.1f", ShooterR.getVelocity());

            telemetry.addLine("");

            telemetry.addLine("===== STATUS =====");
            telemetry.addData("Turret Homing", turretHoming);

            telemetry.addLine("");
            telemetry.addLine("Left Stick Y = Turret");
            telemetry.addLine("Right Bumper = Shooter");
            telemetry.addLine("A = Return Turret To Position 0");

            telemetry.update();
        }
    }
}
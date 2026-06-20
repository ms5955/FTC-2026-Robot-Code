package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled
@TeleOp(name = "DriveTeleOp", group = "FTC")
public class DriveTeleOp extends LinearOpMode {

    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;

    @Override
    public void runOpMode() {

        // Hardware Mapping
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        // Motor Directions
        FL.setDirection(DcMotor.Direction.FORWARD);
        BL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);

        // Brake Mode
        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        final double SPEED = 0.8;

        telemetry.addLine("Drive TeleOp Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Joystick Inputs
            double drive  = -gamepad1.left_stick_y * SPEED;
            double strafe = gamepad1.left_stick_x * SPEED;
            double turn   = gamepad1.right_stick_x * SPEED;

            // Mecanum Math
            double fl = drive + strafe + turn;
            double fr = drive - strafe - turn;
            double bl = drive - strafe + turn;
            double br = drive + strafe - turn;

            // Normalize
            double max = Math.max(
                    Math.max(Math.abs(fl), Math.abs(fr)),
                    Math.max(Math.abs(bl), Math.abs(br))
            );

            if (max > 1.0) {
                fl /= max;
                fr /= max;
                bl /= max;
                br /= max;
            }

            // Deadband
            if (Math.abs(gamepad1.left_stick_y) < 0.05 &&
                    Math.abs(gamepad1.left_stick_x) < 0.05 &&
                    Math.abs(gamepad1.right_stick_x) < 0.05) {

                fl = 0;
                fr = 0;
                bl = 0;
                br = 0;
            }

            // Set Powers
            FL.setPower(fl);
            FR.setPower(fr);
            BL.setPower(bl);
            BR.setPower(br);

            // Calculate Max/Min Motor Power
            double maxPower = Math.max(
                    Math.max(fl, fr),
                    Math.max(bl, br)
            );

            double minPower = Math.min(
                    Math.min(fl, fr),
                    Math.min(bl, br)
            );

            // Telemetry
            telemetry.addData("FL Power", "%.2f", fl);
            telemetry.addData("FR Power", "%.2f", fr);
            telemetry.addData("BL Power", "%.2f", bl);
            telemetry.addData("BR Power", "%.2f", br);

            telemetry.addData("Max Power", "%.2f", maxPower);
            telemetry.addData("Min Power", "%.2f", minPower);

            telemetry.update();
        }
    }
}
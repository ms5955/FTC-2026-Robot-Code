package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@Autonomous(name = "Encoder Telemetry Drive")
public class ShortEncoderDrive extends LinearOpMode {

    DcMotor frontLeft, frontRight, backLeft, backRight;

    @Override
    public void runOpMode() {

        // Motor Mapping
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Reverse Right Side Motors
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Brake Mode
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reset Encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Target Encoder Ticks
        int target = 10;

        // Set Target Position
        frontLeft.setTargetPosition(target);
        frontRight.setTargetPosition(target);
        backLeft.setTargetPosition(target);
        backRight.setTargetPosition(target);

        // Run To Position
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addLine("Ready To Start");
        telemetry.update();

        waitForStart();

        // Slow Speed
        frontLeft.setPower(0.17);
        frontRight.setPower(0.17);
        backLeft.setPower(0.17);
        backRight.setPower(0.17);

        // Encoder Telemetry
        while (opModeIsActive() &&
                (frontLeft.isBusy() ||
                        frontRight.isBusy() ||
                        backLeft.isBusy() ||
                        backRight.isBusy())) {

            telemetry.addData("FL Tick", frontLeft.getCurrentPosition() + " / " + target);
            telemetry.addData("FR Tick", frontRight.getCurrentPosition() + " / " + target);
            telemetry.addData("BL Tick", backLeft.getCurrentPosition() + " / " + target);
            telemetry.addData("BR Tick", backRight.getCurrentPosition() + " / " + target);

            telemetry.addLine("Encoder Driving Running");

            telemetry.update();
        }

        // Stop Motors
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        telemetry.addLine("Movement Complete");
        telemetry.update();

        sleep(3000);
    }
}
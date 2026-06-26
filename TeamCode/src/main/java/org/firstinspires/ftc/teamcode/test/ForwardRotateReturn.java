package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@Autonomous(name = "ForwardRotateReturn")
public class ForwardRotateReturn extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    // Safe beginner speed
    double speed = 0.25;

    @Override
    public void runOpMode() {

        // Connect Motors
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        // Reverse Right Side Motors
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Ready Message
        telemetry.addLine("Robot Ready!");
        telemetry.update();

        waitForStart();

        // =========================
        // STEP 1 : MOVE FORWARD
        // =========================
        telemetry.addLine("Step 1: Moving Forward");
        telemetry.update();

        frontLeft.setPower(speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(speed);

        sleep(1500);

        stopRobot();

        telemetry.addLine("Step 1 Complete");
        telemetry.update();

        sleep(1000);

        // =========================
        // STEP 2 : ROTATE 360 DEGREE
        // =========================
        telemetry.addLine("Step 2: Rotating 360 Degree");
        telemetry.update();

        frontLeft.setPower(speed);
        frontRight.setPower(-speed);
        backLeft.setPower(speed);
        backRight.setPower(-speed);

        // Adjust if rotation is not exact
        sleep(1700);

        stopRobot();

        telemetry.addLine("Step 2 Complete");
        telemetry.update();

        sleep(1000);

        // =========================
        // STEP 3 : MOVE FORWARD AGAIN
        // =========================
        telemetry.addLine("Step 3: Returning To Start Position");
        telemetry.update();

        frontLeft.setPower(speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(speed);

        sleep(1500);

        stopRobot();

        telemetry.addLine("All Steps Finished!");
        telemetry.update();

        sleep(3000);
    }

    // =========================
    // STOP ALL MOTORS
    // =========================
    public void stopRobot() {

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
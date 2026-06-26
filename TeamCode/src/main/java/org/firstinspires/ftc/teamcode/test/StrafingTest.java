package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@Autonomous(name = "StrafingTest")
public class StrafingTest extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    // Slow safe speed
    double speed = 0.15;

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

        telemetry.addLine("Robot Ready!");
        telemetry.update();

        waitForStart();

        // =========================
        // STRAFE RIGHT
        // =========================
        telemetry.addLine("Strafing Right");
        telemetry.update();

        strafeRight();

        sleep(2000);

        stopRobot();

        sleep(1000);

        // =========================
        // STRAFE LEFT
        // =========================
        telemetry.addLine("Strafing Left");
        telemetry.update();

        strafeLeft();

        sleep(2000);

        stopRobot();

        sleep(1000);

        // =========================
        // FINISHED
        // =========================
        telemetry.addLine("Strafing Complete!");
        telemetry.update();

        sleep(3000);
    }

    // =========================
    // STRAFE RIGHT
    // =========================
    public void strafeRight() {

        frontLeft.setPower(speed);
        frontRight.setPower(-speed);
        backLeft.setPower(-speed);
        backRight.setPower(speed);
    }

    // =========================
    // STRAFE LEFT
    // =========================
    public void strafeLeft() {

        frontLeft.setPower(-speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(-speed);
    }

    // =========================
    // STOP ROBOT
    // =========================
    public void stopRobot() {

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@Autonomous(name = "SquarePathSlow")
public class SquarePathSlow extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    // Very slow and safe speed
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
        // SIDE 1
        // =========================
        telemetry.addLine("Side 1");
        telemetry.update();

        moveForward();
        sleep(1500);

        stopRobot();
        sleep(500);

        telemetry.addLine("Turning Right");
        telemetry.update();

        rotateRight();

        // Increased rotation timing
        sleep(2100);

        stopRobot();
        sleep(500);

        // =========================
        // SIDE 2
        // =========================
        telemetry.addLine("Side 2");
        telemetry.update();

        moveForward();
        sleep(1500);

        stopRobot();
        sleep(500);

        telemetry.addLine("Turning Right");
        telemetry.update();

        rotateRight();
        sleep(2100);

        stopRobot();
        sleep(500);

        // =========================
        // SIDE 3
        // =========================
        telemetry.addLine("Side 3");
        telemetry.update();

        moveForward();
        sleep(1500);

        stopRobot();
        sleep(500);

        telemetry.addLine("Turning Right");
        telemetry.update();

        rotateRight();
        sleep(2100);

        stopRobot();
        sleep(500);

        // =========================
        // SIDE 4
        // =========================
        telemetry.addLine("Side 4");
        telemetry.update();

        moveForward();
        sleep(1500);

        stopRobot();
        sleep(500);

        telemetry.addLine("Final Turn");
        telemetry.update();

        rotateRight();
        sleep(2100);

        stopRobot();

        // =========================
        // FINISHED
        // =========================
        telemetry.addLine("Square Path Complete!");
        telemetry.update();

        sleep(3000);
    }

    // =========================
    // MOVE FORWARD
    // =========================
    public void moveForward() {

        frontLeft.setPower(speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(speed);
    }

    // =========================
    // ROTATE RIGHT
    // =========================
    public void rotateRight() {

        frontLeft.setPower(speed);
        frontRight.setPower(-speed);
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
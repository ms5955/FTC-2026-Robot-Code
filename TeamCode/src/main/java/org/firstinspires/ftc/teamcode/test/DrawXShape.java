package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@Autonomous(name = "DrawXShape")
public class DrawXShape extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    // Slow and safe speed
    double speed = 0.2;

    @Override
    public void runOpMode() {

        // Connect Motors
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        // Reverse Right Motors
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addLine("Ready To Draw X");
        telemetry.update();

        waitForStart();

        // =========================
        // DIAGONAL 1
        // FRONT RIGHT
        // =========================
        telemetry.addLine("Drawing Diagonal 1");
        telemetry.update();

        diagonalFrontRight();

        sleep(2000);

        stopRobot();

        sleep(1000);

        // =========================
        // RETURN TO CENTER
        // BACK LEFT
        // =========================
        telemetry.addLine("Returning To Center");
        telemetry.update();

        diagonalBackLeft();

        sleep(2000);

        stopRobot();

        sleep(1000);

        // =========================
        // DIAGONAL 2
        // FRONT LEFT
        // =========================
        telemetry.addLine("Drawing Diagonal 2");
        telemetry.update();

        diagonalFrontLeft();

        sleep(2000);

        stopRobot();

        sleep(1000);

        // =========================
        // RETURN TO CENTER
        // BACK RIGHT
        // =========================
        telemetry.addLine("Returning To Center");
        telemetry.update();

        diagonalBackRight();

        sleep(2000);

        stopRobot();

        // =========================
        // FINISHED
        // =========================
        telemetry.addLine("X Shape Complete!");
        telemetry.update();

        sleep(3000);
    }

    // =========================
    // DIAGONAL FRONT RIGHT
    // =========================
    public void diagonalFrontRight() {

        frontLeft.setPower(speed);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(speed);
    }

    // =========================
    // DIAGONAL BACK LEFT
    // =========================
    public void diagonalBackLeft() {

        frontLeft.setPower(-speed);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(-speed);
    }

    // =========================
    // DIAGONAL FRONT LEFT
    // =========================
    public void diagonalFrontLeft() {

        frontLeft.setPower(0);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(0);
    }

    // =========================
    // DIAGONAL BACK RIGHT
    // =========================
    public void diagonalBackRight() {

        frontLeft.setPower(0);
        frontRight.setPower(-speed);
        backLeft.setPower(-speed);
        backRight.setPower(0);
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
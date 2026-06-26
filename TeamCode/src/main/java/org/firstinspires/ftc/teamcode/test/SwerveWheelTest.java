package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@Autonomous(name = "SwerveWheelTest")
public class SwerveWheelTest extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

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

        telemetry.addLine("Ready!");
        telemetry.update();

        waitForStart();

        // ===== FORWARD =====
        frontLeft.setPower(0.7);
        frontRight.setPower(0.7);
        backLeft.setPower(0.7);
        backRight.setPower(0.7);

        sleep(1000);

        // Stop for 1 second
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        sleep(1000);

        // ===== BACKWARD =====
        frontLeft.setPower(-0.7);
        frontRight.setPower(-0.7);
        backLeft.setPower(-0.7);
        backRight.setPower(-0.7);

        sleep(1000);

        // Final Stop
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        telemetry.addLine("Finished!");
        telemetry.update();

        sleep(2000);
    }
}
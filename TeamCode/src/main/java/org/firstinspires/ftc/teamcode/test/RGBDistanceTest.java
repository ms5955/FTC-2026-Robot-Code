package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Disabled
@TeleOp(name = "RGB Distance Test")
public class RGBDistanceTest extends LinearOpMode {

    private DistanceSensor distanceSensor;
    private Servo rgb;

    @Override
    public void runOpMode() {

        distanceSensor = hardwareMap.get(DistanceSensor.class, "distance");

        // Change name to your RGB configuration name
        rgb = hardwareMap.get(Servo.class, "rgb");

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            double distance = distanceSensor.getDistance(DistanceUnit.CM);

            if (distance < 10) {

                setRed();

            } else if (distance < 30) {

                setYellow();

            } else {

                setGreen();
            }

            telemetry.addData("Distance (cm)", "%.1f", distance);
            telemetry.update();
        }
    }

    // Example positions
    // Replace with actual values for your RGB light

    private void setRed() {
        rgb.setPosition(0.28);
    }

    private void setGreen() {
        rgb.setPosition(0.50);
    }

    private void setYellow() {
        rgb.setPosition(0.39);
    }
}
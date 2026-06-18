package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;

@TeleOp(name = "Pinpoint Drive Test")
public class PinpointTest extends LinearOpMode {

    private GoBildaPinpointDriver pinpoint;
    private DriveSubsystem drive;

    @Override
    public void runOpMode() {

        drive = new DriveSubsystem(hardwareMap);

        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "pinpoint");

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.REVERSED,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        pinpoint.resetPosAndIMU();

        telemetry.addLine("Pinpoint Ready");
        telemetry.update();

        waitForStart();

        sleep(500);

        while (opModeIsActive()) {

            // Drive Robot
            drive.drive(
                    -gamepad1.left_stick_y,
                    gamepad1.left_stick_x,
                    gamepad1.right_stick_x
            );

            // Update Pinpoint
            pinpoint.update();

            telemetry.addLine("===== PINPOINT =====");

            telemetry.addData(
                    "X (in)",
                    "%.2f",
                    pinpoint.getPosX(DistanceUnit.INCH));

            telemetry.addData(
                    "Y (in)",
                    "%.2f",
                    pinpoint.getPosY(DistanceUnit.INCH));

            telemetry.addData(
                    "Heading (deg)",
                    "%.2f",
                    pinpoint.getHeading(AngleUnit.DEGREES));

            telemetry.addData(
                    "Frequency",
                    "%.0f Hz",
                    pinpoint.getFrequency());

            telemetry.update();
        }
    }
}
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
@Disabled
@TeleOp(name = "Intake Test", group = "FTC")
public class IntakeTest extends LinearOpMode {

    private IntakeSubsystem intake;

    @Override
    public void runOpMode() {

        intake = new IntakeSubsystem(hardwareMap);

        telemetry.addLine("Intake Ready");
        telemetry.addLine("Left Bumper = Intake In");
        telemetry.addLine("Left Trigger = Intake Reverse");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Intake In
            if (gamepad1.left_bumper) {
                intake.intakeOut();
            }

            // Intake Reverse
            else if (gamepad1.left_trigger > 0.5) {
                intake.intakeIn();
            }

            // Stop
            else {
                intake.stop();
            }

            telemetry.addData("Intake Power", "%.2f", intake.getPower());
            telemetry.update();
        }
    }
}
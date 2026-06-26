package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;

@Disabled
@TeleOp(name = "Turret Angle Test", group = "Test")
public class TurretAngleTest extends LinearOpMode {

    private TurretSubsystem turret;

    private static final double TICKS_PER_DEGREE = 6.6033;

    private double targetAngle = 0;

    @Override
    public void runOpMode() {

        turret = new TurretSubsystem(hardwareMap);

        telemetry.addLine("===== Turret Angle Test =====");
        telemetry.addLine("X = 75°");
        telemetry.addLine("Y = 90°");
        telemetry.addLine("A = 0°");
        telemetry.addLine("B = -70°");
        telemetry.addLine("DPad Up/Down = +/-1°");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Preset Positions
            if (gamepad1.x) {
                targetAngle = 75;
            }

            if (gamepad1.y) {
                targetAngle = 90;
            }

            if (gamepad1.a) {
                targetAngle = 0;
            }

            if (gamepad1.b) {
                targetAngle = -70;
            }

            // Fine adjustment
            if (gamepad1.dpad_up) {
                targetAngle += 1;
                sleep(120);
            }

            if (gamepad1.dpad_down) {
                targetAngle -= 1;
                sleep(120);
            }

            // Limit angle
            targetAngle = Math.max(-120, Math.min(120, targetAngle));

            turret.setFieldAngle(targetAngle);
            turret.update(0);   // Robot heading = 0

            double currentAngle = turret.getPosition() / TICKS_PER_DEGREE;
            double error = targetAngle - currentAngle;

            telemetry.addData("Target Angle", "%.1f°", targetAngle);
            telemetry.addData("Current Angle", "%.2f°", currentAngle);
            telemetry.addData("Error", "%.2f°", error);
            telemetry.addData("Encoder", turret.getPosition());

            telemetry.update();
        }

        turret.stop();
    }
}
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.subsystems.TurretTestSubsystem;
@Disabled
@TeleOp(name = "Turret Test", group = "Test")
public class TurretTest extends OpMode {

    private TurretTestSubsystem turret;

    private Limelight3A limelight;

    private double targetAngle = -37.14;

    private boolean upPressed = false;
    private boolean downPressed = false;

    @Override
    public void init() {

        turret = new TurretTestSubsystem(hardwareMap);

        turret.setInitialAngle(0);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Change to your AprilTag pipeline
        limelight.pipelineSwitch(1);

        limelight.start();
    }

    @Override
    public void loop() {

        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {

            double tx = result.getTx();

            double currentAngle = turret.getCurrentAngle();

            double targetAngle = currentAngle + tx;

            turret.setTargetAngle(targetAngle);
        }

        turret.update();

        //------------------------
        // Preset Angles
        //------------------------

        if (gamepad1.a)
            targetAngle = 0;

        if (gamepad1.b)
            targetAngle = 45;

        if (gamepad1.x)
            targetAngle = -45;

        if (gamepad1.y)
            targetAngle = 90;

        //------------------------
        // Fine Adjustment
        //------------------------

        if (gamepad1.dpad_up && !upPressed) {
            targetAngle += 1;
            upPressed = true;
        }

        if (!gamepad1.dpad_up)
            upPressed = false;

        if (gamepad1.dpad_down && !downPressed) {
            targetAngle -= 1;
            downPressed = true;
        }

        if (!gamepad1.dpad_down)
            downPressed = false;

        //------------------------
        // Manual Control
        //------------------------

        if (Math.abs(gamepad1.left_stick_x) > 0.1) {
            targetAngle += gamepad1.left_stick_x * 2.0;
        }

        turret.setTargetAngle(targetAngle);

        turret.update();

        //------------------------
        // Telemetry
        //------------------------

        telemetry.addLine("========== TURRET TEST ==========");

        telemetry.addData("Encoder", turret.getEncoder());
        telemetry.addData("Current Angle", "%.2f", turret.getCurrentAngle());
        telemetry.addData("Target Angle", "%.2f", turret.getTargetAngle());
        telemetry.addData("Locked", turret.locked());

        telemetry.addLine();

        if (result != null && result.isValid()) {
            telemetry.addData("Tag ID", result.getFiducialResults().get(0).getFiducialId());
            telemetry.addData("TX", result.getTx());
            telemetry.addData("TY", result.getTy());
            telemetry.addData("Locked", turret.locked());
        } else {
            telemetry.addLine("No AprilTag");
        }

        telemetry.addLine("A = 0°");
        telemetry.addLine("B = 45°");
        telemetry.addLine("X = -45°");
        telemetry.addLine("Y = 90°");

        telemetry.addLine();

        telemetry.addLine("Left Stick = Adjust Target");
        telemetry.addLine("D-Pad Up/Down = ±1°");

        telemetry.update();
    }
}
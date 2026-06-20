package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
@Disabled
@TeleOp(name = "Limelight Test")
public class LimelightTest extends LinearOpMode {

    private Limelight3A limelight;
    private DcMotorEx turret;

    private DriveSubsystem drive;

    private double filteredTx = 0;

    @Override
    public void runOpMode() {

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        turret = hardwareMap.get(DcMotorEx.class, "turret");
        drive = new DriveSubsystem(hardwareMap);


        turret.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        limelight.pipelineSwitch(1);
        limelight.start();

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Drive
            drive.drive(
                    -gamepad1.left_stick_y * 0.9,
                    gamepad1.left_stick_x * 0.9,
                    gamepad1.right_stick_x * 0.9
            );
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                double tx = result.getTx();

// Smooth TX
                filteredTx = filteredTx * 0.95 + tx * 0.05;

// Lock zone
                if (Math.abs(filteredTx) < 2.0) {
                    turret.setPower(0);
                } else {

                    double kP = 0.01;

                    double turretPower = -filteredTx * kP;

                    // Minimum power to overcome friction
                    if (Math.abs(turretPower) < 0.05) {
                        turretPower = Math.signum(turretPower) * 0.05;
                    }

                    // Limit speed
                    turretPower = Math.max(-0.25, Math.min(0.25, turretPower));

                    turret.setPower(turretPower);

                    telemetry.addData("Turret Power", turretPower);
                }

                telemetry.addData("TX Raw", tx);
                telemetry.addData("TX Filtered", filteredTx);

            } else {

                turret.setPower(0);

                telemetry.addLine("No Tag Detected");
            }

            telemetry.update();
        }

        limelight.stop();
    }
}
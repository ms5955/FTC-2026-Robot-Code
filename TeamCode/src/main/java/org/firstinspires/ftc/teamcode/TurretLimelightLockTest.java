package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ServoSubsystem;

@TeleOp(name = "Turret Limelight Lock Test")
public class TurretLimelightLockTest extends LinearOpMode {

    private DriveSubsystem drive;
    private ShooterSubsystem shooter;
    private IntakeSubsystem intake;
    private DcMotorEx turret;
    private Limelight3A limelight;
    private ServoSubsystem servos;

    private Servo hooder;


    // Tune these

    // Servo Positions
    private static final double STOPPER_OPEN = 0.6;
    private static final double DRIVE_SPEED = 0.9;
    private static final double STOPPER_CLOSED = 0.3;
    private double filteredTx = 0;
    private static final double KP = 0.01;
    private static final double MIN_POWER = 0.05;
    private static final double MAX_POWER = 0.20;
    private static final double DEADZONE = 2.0;
    @Override
    public void runOpMode() {

        drive = new DriveSubsystem(hardwareMap);
        shooter = new ShooterSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        servos = new ServoSubsystem(hardwareMap);


        turret = hardwareMap.get(DcMotorEx.class, "turret");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        limelight.pipelineSwitch(1);
        limelight.start();

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Drive
            drive.drive(
                    -gamepad1.left_stick_y * DRIVE_SPEED,
                    gamepad1.left_stick_x * DRIVE_SPEED,
                    gamepad1.right_stick_x * DRIVE_SPEED
            );

            // =========================
            // INTAKE CONTROL
            // =========================
            if (gamepad1.left_bumper) {
                intake.intakeOut();
            }
            else if (gamepad1.left_trigger > 0.1) {
                intake.intakeIn();
            }
            else {
                intake.stop();
            }

            // =========================
            // STOPPER SERVO CONTROL
            // =========================
            if (gamepad1.dpad_down) {
                servos.setStopper(STOPPER_CLOSED);
            }

            if (gamepad1.dpad_up) {
                servos.setStopper(STOPPER_OPEN);
            }

            // =========================
            // HOODER SERVO CONTROL
            // =========================
            if (gamepad1.a) {
                servos.setHudder(0.55);
            }

            if (gamepad1.b) {
                servos.setHudder(0.39);
            }

            // =========================
            // SHOOTER CONTROL
            // =========================
            if (gamepad1.right_bumper) {

                shooter.shootFast();

            }
            else if (gamepad1.right_trigger > 0.1) {

                shooter.reverse();

            }
            else if (gamepad1.y){
                shooter.shootSlow();
            }
            else {

                shooter.stop();
            }


            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                    telemetry.addData("BotPose", result.getBotpose());

                    telemetry.addData("X", result.getBotpose().getPosition().x);
                    telemetry.addData("Y", result.getBotpose().getPosition().y);

                    telemetry.update();


                double tx = result.getTx();

                double x = result.getBotpose().getPosition().x;
                double y = result.getBotpose().getPosition().y;

                double distance = Math.hypot(x, y);

                telemetry.addData("Distance ()", distance);

                // Smooth TX
                filteredTx = filteredTx * 0.95 + tx * 0.05;

                double turretPower = 0;

                // Larger lock zone to prevent hunting
                if (Math.abs(filteredTx) > DEADZONE) {

                    turretPower = -filteredTx * KP;

                    if (Math.abs(turretPower) < MIN_POWER) {
                        turretPower = Math.signum(turretPower) * MIN_POWER;
                    }

                    turretPower = Math.max(
                            -MAX_POWER,
                            Math.min(MAX_POWER, turretPower)
                    );
                }

                turret.setPower(turretPower);

                telemetry.addData("Tag Visible", true);
                telemetry.addData("TX Raw", tx);
                telemetry.addData("TX Filtered", filteredTx);
                telemetry.addData("Turret Power", turretPower);

            } else {

                turret.setPower(0);

                telemetry.addData("Tag Visible", false);
            }

            // Manual override
            if (gamepad1.dpad_left) {
                turret.setPower(0.3);
            }

            if (gamepad1.dpad_right) {
                turret.setPower(-0.3);
            }

            telemetry.addData("Turret Position",
                    turret.getCurrentPosition());

            telemetry.addLine("===== SHOOTER =====");

            telemetry.addData("Left Velocity",
                    "%.1f", shooter.getLeftVelocity());

            telemetry.addData("Right Velocity",
                    "%.1f", shooter.getRightVelocity());
            telemetry.addData("Servo position", "%.2f", servos.getHudderPosition());







            telemetry.update();
        }

        turret.setPower(0);
        drive.stop();
    }
}

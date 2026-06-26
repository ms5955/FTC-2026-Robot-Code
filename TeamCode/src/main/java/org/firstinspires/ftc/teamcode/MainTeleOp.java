package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.RGBSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ServoSubsystem;
@Disabled
@TeleOp(name = "Main TeleOp", group = "FTC")
public class MainTeleOp extends LinearOpMode {

    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private ShooterSubsystem shooter;
    private TurretSubsystem turret;
    private ServoSubsystem servos;

    private Limelight3A limelight;
    private GoBildaPinpointDriver pinpoint;

    private RGBSubsystem rgb;
    private double lastError = 0;

    private boolean autoAimEnabled = false;

    private static final int TARGET_TAG_ID = 20;
    private static final double AIM_kP = 0.035;
    private static final double AIM_MAX_POWER = 0.50;
    private static final double AIM_TOLERANCE = 0.20;

    private static final double DRIVE_SPEED = 0.8;
    private static final double TURRET_SPEED = 0.5;

    // Servo Positions
    private static final double STOPPER_OPEN = 0.6;
    private static final double STOPPER_CLOSED = 0.3;

    private static final double HUDDER_CLOSED = 0.0;
    private static final double HUDDER_OPEN = 1.0;
    private boolean blinkState = false;
    private long lastBlinkTime = 0;


    @Override
    public void runOpMode() {

        // =========================
        // INITIALIZE SUBSYSTEMS
        // =========================
        drive = new DriveSubsystem(hardwareMap);
        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "pinpoint");

        pinpoint.resetPosAndIMU();
        intake = new IntakeSubsystem(hardwareMap);
        shooter = new ShooterSubsystem(hardwareMap);
        turret = new TurretSubsystem(hardwareMap);
        servos = new ServoSubsystem(hardwareMap);
        rgb = new RGBSubsystem(hardwareMap);
        limelight = hardwareMap.get(
                Limelight3A.class,
                "limelight");



        // Initial Servo Positions
        servos.setStopper(STOPPER_CLOSED);
        servos.setHudder(HUDDER_CLOSED);

        telemetry.addLine("Robot Ready");
        telemetry.update();

        waitForStart();
        limelight.pipelineSwitch(0);
        limelight.start();
        while (opModeIsActive()) {
            pinpoint.update();
            // =========================
            // DRIVE CONTROL
            // =========================
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
            // RGB INDICATOR
            // =========================

            if (Math.abs(intake.getPower()) > 0.1) {

                if (Math.abs(intake.getVelocity()) < 100) {

                    // Intake stalled / ball jammed
                    rgb.red();

                } else {

                    // Intake running normally
                    rgb.blue();
                }

            } else {

                // Intake stopped
                rgb.off();
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
            else {

                shooter.stop();
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
            // HUDDER SERVO CONTROL
            // =========================
            if (gamepad1.y) {
                servos.setHudder(HUDDER_OPEN);
            }

            if (gamepad1.a) {
                servos.setHudder(HUDDER_CLOSED);
            }

            // =========================
            // TURRET CONTROL
            // =========================
            double robotHeadingDeg =
                    pinpoint.getHeading(AngleUnit.DEGREES);

            turret.update(robotHeadingDeg);

            if(gamepad1.dpad_left)
                turret.aimLeft();

            if(gamepad1.dpad_right)
                turret.aimRight();
            /*
            if (gamepad1.x) {
                turret.manual(TURRET_SPEED);
            }
            else if (gamepad1.b) {
                turret.manual(-TURRET_SPEED);
            }
            else {
                turret.manual(0);
            }
            */
            /*
            if (gamepad1.x) {

                LLResult result = limelight.getLatestResult();

                if (result != null && result.isValid()) {

                    double tx = result.getTx();

                    double power = -tx * AIM_kP;

                    power = Math.max(
                            -AIM_MAX_POWER,
                            Math.min(AIM_MAX_POWER, power));

                    if (Math.abs(tx) < AIM_TOLERANCE) {
                        power = 0;
                    }

                    turret.manual(power);

                    telemetry.addData("TX", tx);

                } else {

                    turret.manual(0);
                    telemetry.addLine("NO TAG FOUND");
                }

            }
            else if (gamepad1.dpad_left) {

                turret.manual(TURRET_SPEED);

            }
            else if (gamepad1.dpad_right) {

                turret.manual(-TURRET_SPEED);

            }
            else {

                turret.manual(0);
            }
            /*

             */
          // Enable Auto Aim
            /*
            if (gamepad1.x) {
                autoAimEnabled = true;
            }
            // Disable Auto Aim
            if (gamepad1.b) {
                autoAimEnabled = false;
                lastError = 0;
            }
            */

            // Auto Tracking
            /*
            if (autoAimEnabled) {

                LLResult result = limelight.getLatestResult();

                if (result != null && result.isValid()) {

                    double tx = result.getTx();

                    double error = -tx;

                    double p = error * 0.040;

                    double d = (error - lastError) * 0.008;

                    double power = p + d;

                    lastError = error;

                    power = Math.max(
                            -AIM_MAX_POWER,
                            Math.min(AIM_MAX_POWER, power));

                    if (Math.abs(tx) < AIM_TOLERANCE) {
                        power = 0;
                    }

                    if (Math.abs(tx) > AIM_TOLERANCE &&
                            Math.abs(power) < 0.10) {

                        power = Math.signum(power) * 0.10;
                    }

                    turret.manual(power);

                } else {

                    turret.manual(0);
                }

            }
            */
            // Auto Tracking
            /*
            if (autoAimEnabled) {

                LLResult result = limelight.getLatestResult();

                if (result != null && result.isValid()) {

                    double tx = result.getTx();

                    double error = -tx;

                    double p = error * 0.035;

                    double d = (error - lastError) * 0.005;

                    double power = p + d;

                    lastError = error;

                    if (Math.abs(tx) > AIM_TOLERANCE &&
                            Math.abs(power) < 0.10) {

                        power = Math.signum(power) * 0.10;
                    }
                    power = Math.max(
                            -AIM_MAX_POWER,
                            Math.min(AIM_MAX_POWER, power));

                    if (Math.abs(tx) < AIM_TOLERANCE) {
                        power = 0;
                    }

                    turret.manual(power);
                }
               else {

                        double heading =
                                pinpoint.getHeading(AngleUnit.DEGREES);
                        int targetPosition = (int)(heading * 5);

                        turret.stabilizeToGoal(heading);
                    }

            }



            // Manual Turret
            else if (gamepad1.dpad_left) {

                turret.manual(TURRET_SPEED);

            }
            else if (gamepad1.dpad_right) {

                turret.manual(-TURRET_SPEED);

            }
            else {

                turret.manual(0);
            }

             */
            // =========================
            // TELEMETRY
            // =========================
            /*
            telemetry.addLine("===== DRIVE =====");
            telemetry.addData("FL Encoder", drive.getFLPosition());
            telemetry.addData("FR Encoder", drive.getFRPosition());
            telemetry.addData("BL Encoder", drive.getBLPosition());
            telemetry.addData("BR Encoder", drive.getBRPosition());

            telemetry.addLine("");
            */
            LLResult result = limelight.getLatestResult();

            telemetry.addLine("===== LIMELIGHT =====");

            if (result != null && result.isValid()) {

                telemetry.addData("TX", result.getTx());
                telemetry.addData("TY", result.getTy());


            } else {

                telemetry.addLine("Result NULL");
            }


            telemetry.addLine("===== INTAKE =====");
            telemetry.addData("Power", "%.2f", intake.getPower());
            telemetry.addData("Velocity", "%.2f", intake.getVelocity());

            telemetry.addLine("");

            telemetry.addLine("===== SHOOTER =====");

            telemetry.addData("Average Velocity",
                    "%.1f", shooter.getAverageVelocity());

            telemetry.addData("Left Velocity",
                    "%.1f", shooter.getLeftVelocity());

            telemetry.addData("Right Velocity",
                    "%.1f", shooter.getRightVelocity());



            telemetry.addLine("");

            telemetry.addLine("===== TURRET =====");
            telemetry.addData("Position",
                    turret.getPosition());
            telemetry.addData(
                    "Robot Heading",
                    pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.addLine("");
            telemetry.addLine("===== SERVOS =====");
            telemetry.addData("Stopper Position",
                    servos.getStopperPosition());
            telemetry.addData("Hudder Position",
                    servos.getHudderPosition());
            telemetry.update();
        }
    }
}
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

@Disabled
@TeleOp(name = "TurretTest", group = "Test")
public class TurretTest extends LinearOpMode {

    private DcMotor turret;
    private GoBildaPinpointDriver pinpoint;
    private DriveSubsystem drive;

    private ShooterSubsystem shooter;


    // 537.7 CPR * (100/20)
    private static final double TICKS_PER_DEGREE = 7.47;

    private static final double MIN_ANGLE = -120.0;
    private static final double MAX_ANGLE = 120.0;

    private double lockedFieldAngle = 0.0;

    private static final double DRIVE_SPEED = 0.8;

    // ---------------- PIDF ----------------
    private double kP = 0.009;
    private double kI = 0.0000;
    private double kD = 0.0002;
    private double kF = 0.05;

    private double integral = 0;
    private double lastError = 0;

    private double turretOffsetDeg = 0;

    private final ElapsedTime pidTimer = new ElapsedTime();

    @Override
    public void runOpMode() {

        drive = new DriveSubsystem(hardwareMap);

        turret = hardwareMap.get(DcMotor.class, "turret");

        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "pinpoint"
        );

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pinpoint.resetPosAndIMU();

        telemetry.addLine("Turret Test Ready");
        telemetry.update();

        waitForStart();

        pidTimer.reset();

        while (opModeIsActive()) {

            pinpoint.update();

            // ---------------- DRIVE ----------------
            drive.drive(
                    -gamepad1.left_stick_y * DRIVE_SPEED,
                    gamepad1.left_stick_x * DRIVE_SPEED,
                    gamepad1.right_stick_x * DRIVE_SPEED
            );

            // ---------------- HEADING ----------------
            double robotHeadingDeg =
                    pinpoint.getHeading(AngleUnit.DEGREES);

            // ---------------- MANUAL AIM ----------------
            if (gamepad1.dpad_left) {
                lockedFieldAngle += 1;
            }

            if (gamepad1.dpad_right) {
                lockedFieldAngle -= 1;
            }

            // ---------------- FIELD LOCK ----------------
            double turretTargetDeg =
                    lockedFieldAngle - robotHeadingDeg + turretOffsetDeg;

            turretTargetDeg = Math.max(
                    MIN_ANGLE,
                    Math.min(MAX_ANGLE, turretTargetDeg)
            );

            int targetTicks =
                    (int) (turretTargetDeg * TICKS_PER_DEGREE);

            // ---------------- PIDF ----------------
            int currentTicks = turret.getCurrentPosition();

            double error =
                    targetTicks - currentTicks;

            double dt = pidTimer.seconds();
            pidTimer.reset();

            integral += error * dt;

            double derivative =
                    (error - lastError) /
                            Math.max(dt, 0.001);

            double power =
                    (kP * error) +
                            (kI * integral) +
                            (kD * derivative);

            if (Math.abs(error) > 5) {
                power += Math.signum(error) * kF;
            }

            power = Math.max(-1.0,
                    Math.min(1.0, power));

            turret.setPower(power);

            lastError = error;
            // ---------------- SHOOTER CODE ----------------
            if (gamepad1.right_bumper) {

                shooter.shootFast();

            }
            if (gamepad1.right_trigger > 0.1) {

                shooter.reverse();

            }


            // ---------------- TELEMETRY ----------------
            telemetry.addData("Robot Heading", robotHeadingDeg);
            telemetry.addData("Locked Field Angle", lockedFieldAngle);
            telemetry.addData("Turret Target Deg", turretTargetDeg);

            telemetry.addData("Target Ticks", targetTicks);
            telemetry.addData("Current Ticks", currentTicks);

            telemetry.addData("Error", error);
            telemetry.addData("Power", power);

            telemetry.addData("kP", kP);
            telemetry.addData("kD", kD);
            telemetry.addData("kF", kF);

            telemetry.update();
        }
    }
}
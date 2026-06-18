package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;

import com.pedropathing.Constants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ServoSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

@Autonomous(name = "RedDown8", group = "Autonomous")
@Configurable
public class RedDown8 extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private int pathState = 0;
    private Paths paths;

    private ShooterSubsystem shooter;
    private IntakeSubsystem intake;
    private ServoSubsystem servos;
    private DcMotorEx turret;
    private Limelight3A limelight;

    private ElapsedTime waitTimer = new ElapsedTime();

    private static final double STOPPER_OPEN = 0.6;
    private static final double STOPPER_CLOSED = 0.3;

    private static final double KP = 0.01;
    private static final double MIN_POWER = 0.05;
    private static final double MAX_POWER = 0.20;
    private static final double DEADZONE = 2.0;

    private static final double SHOOT_TIME = 1000;

    private double filteredTx = 0;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setPose(new Pose(103, 9, Math.toRadians(90)));

        shooter = new ShooterSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        servos = new ServoSubsystem(hardwareMap);

        turret = hardwareMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(1);
        limelight.start();

        servos.setStopper(STOPPER_CLOSED);
        servos.setHudder(0.55);

        paths = new Paths(follower);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        follower.update();

        turretLockToTag();

        autonomousPathUpdate();

        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());

        telemetry.addData("Turret Position", turret.getCurrentPosition());
        telemetry.addData("Left Velocity", "%.1f", shooter.getLeftVelocity());
        telemetry.addData("Right Velocity", "%.1f", shooter.getRightVelocity());

        panelsTelemetry.update(telemetry);
    }

    private void shootAtPosition(int nextState) {
        shooter.shootFast();

        if (shooter.readyForFastShot()) {
            servos.setStopper(STOPPER_OPEN);
            intake.intakeIn();

            if (waitTimer.milliseconds() >= SHOOT_TIME) {
                servos.setStopper(STOPPER_CLOSED);
                intake.stop();
                shooter.stop();

                pathState = nextState;
            }
        } else {
            servos.setStopper(STOPPER_CLOSED);
            intake.stop();
            waitTimer.reset();
        }
    }

    private void startShootPath(PathChain path, int nextState) {
        shooter.shootFast();
        servos.setStopper(STOPPER_CLOSED);
        intake.stop();

        follower.followPath(path);
        pathState = nextState;
    }

    private void turretLockToTag() {
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            double tx = result.getTx();

            filteredTx = filteredTx * 0.95 + tx * 0.05;

            double turretPower = 0;

            if (Math.abs(filteredTx) > DEADZONE) {
                turretPower = -filteredTx * KP;

                if (Math.abs(turretPower) < MIN_POWER) {
                    turretPower = Math.signum(turretPower) * MIN_POWER;
                }

                turretPower = Math.max(-MAX_POWER, Math.min(MAX_POWER, turretPower));
            }

            turret.setPower(turretPower);
        } else {
            turret.setPower(0);
        }
    }

    public void autonomousPathUpdate() {
        switch (pathState) {

            case 0:
                waitTimer.reset();
                pathState = 1;
                break;

            case 1:
                shootAtPosition(2);
                break;

            case 2:
                follower.followPath(paths.up);
                pathState = 3;
                break;

            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(paths.upIntake);
                    pathState = 4;
                }
                break;

            case 4:
                if (!follower.isBusy()) {
                    startShootPath(paths.shoot2, 5);
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    waitTimer.reset();
                    pathState = 6;
                }
                break;

            case 6:
                shootAtPosition(7);
                break;

            case 7:
                intake.intakeIn();
                follower.followPath(paths.down1);
                pathState = 8;
                break;

            case 8:
                if (!follower.isBusy()) {
                    startShootPath(paths.shoot3, 9);
                }
                break;

            case 9:
                if (!follower.isBusy()) {
                    waitTimer.reset();
                    pathState = 10;
                }
                break;

            case 10:
                shootAtPosition(11);
                break;

            case 11:
                intake.intakeIn();
                follower.followPath(paths.down2);
                pathState = 12;
                break;

            case 12:
                if (!follower.isBusy()) {
                    startShootPath(paths.shoot4, 13);
                }
                break;

            case 13:
                if (!follower.isBusy()) {
                    waitTimer.reset();
                    pathState = 14;
                }
                break;

            case 14:
                shootAtPosition(15);
                break;

            case 15:
                intake.intakeIn();
                follower.followPath(paths.down3);
                pathState = 16;
                break;

            case 16:
                if (!follower.isBusy()) {
                    startShootPath(paths.shoot5, 17);
                }
                break;

            case 17:
                if (!follower.isBusy()) {
                    waitTimer.reset();
                    pathState = 18;
                }
                break;

            case 18:
                shootAtPosition(19);
                break;

            case 19:
                intake.intakeIn();
                follower.followPath(paths.down4);
                pathState = 20;
                break;

            case 20:
                if (!follower.isBusy()) {
                    startShootPath(paths.shoot6, 21);
                }
                break;

            case 21:
                if (!follower.isBusy()) {
                    waitTimer.reset();
                    pathState = 22;
                }
                break;

            case 22:
                shootAtPosition(23);
                break;

            case 23:
                intake.intakeIn();
                follower.followPath(paths.up2);
                pathState = 24;
                break;

            case 24:
                if (!follower.isBusy()) {
                    startShootPath(paths.shoot7, 25);
                }
                break;

            case 25:
                if (!follower.isBusy()) {
                    waitTimer.reset();
                    pathState = 26;
                }
                break;

            case 26:
                shootAtPosition(27);
                break;

            case 27:
                intake.intakeIn();
                follower.followPath(paths.down5);
                pathState = 28;
                break;

            case 28:
                if (!follower.isBusy()) {
                    startShootPath(paths.shoot8, 29);
                }
                break;

            case 29:
                if (!follower.isBusy()) {
                    waitTimer.reset();
                    pathState = 30;
                }
                break;

            case 30:
                shootAtPosition(31);
                break;

            case 31:
                intake.stop();
                shooter.stop();
                turret.setPower(0);
                servos.setStopper(STOPPER_CLOSED);
                break;
        }
    }

    public static class Paths {
        public PathChain up;
        public PathChain upIntake;
        public PathChain shoot2;
        public PathChain down1;
        public PathChain shoot3;
        public PathChain down2;
        public PathChain shoot4;
        public PathChain down3;
        public PathChain shoot5;
        public PathChain down4;
        public PathChain shoot6;
        public PathChain up2;
        public PathChain shoot7;
        public PathChain down5;
        public PathChain shoot8;

        public Paths(Follower follower) {
            up = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(95.000, 9.000),
                            new Pose(97.000, 35.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            upIntake = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(97.000, 35.000),
                            new Pose(126.000, 35.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot2 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(126.000, 35.000),
                            new Pose(95.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            down1 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(95.000, 9.000),
                            new Pose(132.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot3 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(132.000, 9.000),
                            new Pose(95.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            down2 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(95.000, 9.000),
                            new Pose(116.000, 12.000),
                            new Pose(133.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot4 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(133.000, 9.000),
                            new Pose(95.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            down3 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(95.000, 9.000),
                            new Pose(100.000, 19.000),
                            new Pose(133.000, 16.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot5 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(133.000, 16.000),
                            new Pose(95.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            down4 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(95.000, 9.000),
                            new Pose(101.000, 30.000),
                            new Pose(133.000, 30.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot6 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(133.000, 30.000),
                            new Pose(95.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            up2 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(95.000, 9.000),
                            new Pose(133.000, 30.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot7 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(133.000, 30.000),
                            new Pose(95.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            down5 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(95.000, 9.000),
                            new Pose(133.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot8 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(133.000, 9.000),
                            new Pose(95.000, 9.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();
        }
    }
}
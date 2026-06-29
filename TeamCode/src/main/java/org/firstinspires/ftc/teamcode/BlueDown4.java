package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;

@Autonomous(name = "BlueDown4", group = "Autonomous")
@Configurable
public class BlueDown4 extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private int pathState = 0;
    private Paths paths;

    private ShooterSubsystem shooter;
    private IntakeSubsystem intake;
    private ServoSubsystem servos;
    private TurretSubsystem turret;

    private final ElapsedTime waitTimer = new ElapsedTime();

    private static final double STOPPER_OPEN = 0.6;
    private static final double STOPPER_CLOSED = 0.3;

    private static final double SHOOT_TIME = 1200;
    private static final double SHOOT_VELOCITY_READY = 1430;

    private static final double TURRET_ANGLE = 71;

    private boolean shootingStarted = false;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(40.5, 8, Math.toRadians(180)));
        follower.setMaxPower(0.8);

        shooter = new ShooterSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        servos = new ServoSubsystem(hardwareMap);
        turret = new TurretSubsystem(hardwareMap);

        turret.setFieldAngle(TURRET_ANGLE);

        servos.setStopper(STOPPER_CLOSED);
        servos.setHudder(0.22);

        paths = new Paths(follower);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        follower.update();
        turret.update(Math.toDegrees(follower.getPose().getHeading()));

        autonomousPathUpdate();

        telemetry.addLine("===== SHOOTER =====");
        telemetry.addData("Left Velocity", "%.1f", shooter.getLeftVelocity());
        telemetry.addData("Right Velocity", "%.1f", shooter.getRightVelocity());
        telemetry.addData("Average Velocity", "%.1f", shooter.getAverageVelocity());
        telemetry.addData("Shooting Started", shootingStarted);

        telemetry.addLine("===== TURRET =====");
        telemetry.addData("Turret Position", turret.getPosition());
        telemetry.addData("Locked Field Angle", "%.1f", turret.getLockedFieldAngle());

        telemetry.update();

        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }

    private void startShootPath(PathChain path, int nextState) {
        turret.setFieldAngle(TURRET_ANGLE);
        shooter.longshoot();
        servos.setStopper(STOPPER_CLOSED);
        intake.stop();
        shootingStarted = false;

        follower.followPath(path);
        pathState = nextState;
    }

    private void runShootSequence(int nextState) {
        shooter.longshoot();

        if (!shootingStarted) {
            servos.setStopper(STOPPER_CLOSED);
            intake.stop();

            if (shooter.getAverageVelocity() > SHOOT_VELOCITY_READY) {
                shootingStarted = true;
                waitTimer.reset();
            }

            return;
        }

        servos.setStopper(STOPPER_OPEN);
        intake.intakeIn();

        if (waitTimer.milliseconds() >= SHOOT_TIME) {
            servos.setStopper(STOPPER_CLOSED);
            intake.stop();
            shooter.stop();

            shootingStarted = false;
            pathState = nextState;
        }
    }

    private void startIntakePath(PathChain path, int nextState) {
        shooter.stop();
        servos.setStopper(STOPPER_CLOSED);
        intake.intakeIn();

        follower.followPath(path);
        pathState = nextState;
    }

    private void startNormalPath(PathChain path, int nextState) {
        shooter.stop();
        intake.stop();
        servos.setStopper(STOPPER_CLOSED);

        follower.followPath(path);
        pathState = nextState;
    }

    public void autonomousPathUpdate() {
        switch (pathState) {

            case 0:
                follower.followPath(paths.shoot1, true);
                pathState = 1;
                break;

            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(paths.intake1, true);
                    pathState = 2;
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(paths.shoot2, true);
                    pathState = 3;
                }
                break;

            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(paths.pathToIntake2, true);
                    pathState = 4;
                }
                break;

            case 4:
                if (!follower.isBusy()) {
                    follower.followPath(paths.intake2, true);
                    pathState = 5;
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    follower.followPath(paths.shoot3, true);
                    pathState = 6;
                }
                break;

            case 6:
                if (!follower.isBusy()) {
                    follower.followPath(paths.pathToIntake3, true);
                    pathState = 7;
                }
                break;

            case 7:
                if (!follower.isBusy()) {
                    follower.followPath(paths.intake3, true);
                    pathState = 8;
                }
                break;

            case 8:
                if (!follower.isBusy()) {
                    follower.followPath(paths.shoot4, true);
                    pathState = 9;
                }
                break;

            case 9:
                if (!follower.isBusy()) {
                    follower.followPath(paths.pathToIntake4, true);
                    pathState = 10;
                }
                break;

            case 10:
                if (!follower.isBusy()) {
                    follower.followPath(paths.intake4, true);
                    pathState = 11;
                }
                break;

            case 11:
                if (!follower.isBusy()) {
                    follower.followPath(paths.shoot5, true);
                    pathState = 12;
                }
                break;
        }
    }

    public static class Paths {

        public PathChain shoot1;
        public PathChain intake1;
        public PathChain shoot2;
        public PathChain pathToIntake2;
        public PathChain intake2;
        public PathChain shoot3;
        public PathChain pathToIntake3;
        public PathChain intake3;
        public PathChain shoot4;
        public PathChain pathToIntake4;
        public PathChain intake4;
        public PathChain shoot5;

        public Paths(Follower follower) {

            shoot1 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(40.500, 8.000),
                            new Pose(46.500, 8.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            intake1 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(46.500, 8.000),
                            new Pose(48.500, 36.000),
                            new Pose(15.500, 35.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            shoot2 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(15.500, 35.000),
                            new Pose(46.500, 8.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            pathToIntake2 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(46.500, 8.000),
                            new Pose(13.500, 15.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            intake2 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(13.500, 15.000),
                            new Pose(12.500, 7.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(180))
                    .build();

            shoot3 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(12.500, 7.000),
                            new Pose(46.500, 8.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            pathToIntake3 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(46.500, 8.000),
                            new Pose(12.500, 34.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            intake3 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(12.500, 34.000),
                            new Pose(9.500, 6.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(180))
                    .build();

            shoot4 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(9.500, 6.000),
                            new Pose(46.500, 8.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            pathToIntake4 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(46.500, 8.000),
                            new Pose(15.500, 35.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            intake4 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(15.500, 35.000),
                            new Pose(12.500, 6.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(180))
                    .build();

            shoot5 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(12.500, 6.000),
                            new Pose(46.500, 8.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();
        }
    }
}
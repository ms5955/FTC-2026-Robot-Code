package org.firstinspires.ftc.teamcode.auto.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;

import com.pedropathing.Constants;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ServoSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;

@Disabled
@Autonomous(name = "RedUp7-test2-v2", group = "Autonomous")
@Configurable
public class RedUp7Test2V2 extends OpMode {

    public static double STOPPER_OPEN = 0.60;
    public static double STOPPER_CLOSED = 0.30;
    public static double HUDDER_POSITION = 0.25;

    public static double SHOT_DELAY_MS = 225.0;
    public static double FEED_PULSE_MS = 125.0;
    public static double SHOOTER_READY_TIMEOUT_MS = 450.0;
    public static double SHOOT_SEQUENCE_TIMEOUT_MS = 1600.0;
    public static double OPEN_WAIT_MS = 300.0;
    public static double INTAKE_SETTLE_MS = 450.0;
    public static double PATH_TIMEOUT_MS = 4500.0;
    public static int BALLS_PER_CYCLE = 3;

    private enum ShooterState {
        IDLE,
        SPINNING_UP,
        FEEDING,
        BETWEEN_SHOTS,
        COMPLETE
    }

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private Paths paths;

    private ShooterSubsystem shooter;
    private IntakeSubsystem intake;
    private ServoSubsystem servos;
    private TurretSubsystem turret;

    private final ElapsedTime autoTimer = new ElapsedTime();
    private final ElapsedTime waitTimer = new ElapsedTime();
    private final ElapsedTime pathTimer = new ElapsedTime();
    private final ElapsedTime shooterStateTimer = new ElapsedTime();
    private final ElapsedTime shooterTotalTimer = new ElapsedTime();

    private int pathState = 0;
    private int ballsShot = 0;
    private ShooterState shooterState = ShooterState.IDLE;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(110, 131, Math.toRadians(90)));
        follower.setMaxPower(0.95);

        shooter = new ShooterSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        servos = new ServoSubsystem(hardwareMap);
        turret = new TurretSubsystem(hardwareMap);

        turret.setFieldAngle(0);
        turret.setOffset(43);

        servos.setStopper(STOPPER_CLOSED);
        servos.setHudder(HUDDER_POSITION);

        paths = new Paths(follower);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void start() {
        autoTimer.reset();
        waitTimer.reset();
        pathTimer.reset();
        resetShooterState();
    }

    @Override
    public void loop() {
        follower.update();
        turret.update(Math.toDegrees(follower.getPose().getHeading()));
        autonomousPathUpdate();
        updateTelemetry();
    }

    private void startShootPath(PathChain path, int nextState) {
        shooter.shootSlow();
        intake.stop();
        servos.setStopper(STOPPER_CLOSED);
        resetShooterState();
        follow(path, nextState);
    }

    private void startIntakePath(PathChain path, int nextState) {
        shooter.stop();
        servos.setStopper(STOPPER_CLOSED);
        resetShooterState();
        intake.intakeOut();
        follow(path, nextState);
    }

    private void startOpenPath(PathChain path, int nextState) {
        shooter.stop();
        intake.stop();
        servos.setStopper(STOPPER_CLOSED);
        resetShooterState();
        follow(path, nextState);
    }

    private void startPathToIntake(PathChain path, int nextState) {
        shooter.stop();
        intake.stop();
        servos.setStopper(STOPPER_CLOSED);
        resetShooterState();
        follow(path, nextState);
    }

    private void follow(PathChain path, int nextState) {
        follower.followPath(path);
        pathTimer.reset();
        pathState = nextState;
    }

    private boolean pathDoneOrTimedOut() {
        return !follower.isBusy() || pathTimer.milliseconds() >= PATH_TIMEOUT_MS;
    }

    private void beginWait() {
        waitTimer.reset();
    }

    private boolean waitDone(double waitMs) {
        return waitTimer.milliseconds() >= waitMs;
    }

    private void waitWithIntakeOn(int nextState) {
        shooter.stop();
        servos.setStopper(STOPPER_CLOSED);
        resetShooterState();
        intake.intakeOut();

        if (waitDone(INTAKE_SETTLE_MS)) {
            intake.stop();
            pathState = nextState;
        }
    }

    private void waitWithIntakeOff(int nextState) {
        shooter.stop();
        intake.stop();
        servos.setStopper(STOPPER_CLOSED);
        resetShooterState();

        if (waitDone(OPEN_WAIT_MS)) {
            pathState = nextState;
        }
    }

    private void resetShooterState() {
        ballsShot = 0;
        shooterState = ShooterState.IDLE;
        shooterStateTimer.reset();
        shooterTotalTimer.reset();
    }

    private void startShooterStateMachine() {
        ballsShot = 0;
        shooterState = ShooterState.SPINNING_UP;
        shooterStateTimer.reset();
        shooterTotalTimer.reset();
        shooter.shootSlow();
        intake.stop();
        servos.setStopper(STOPPER_CLOSED);
    }

    private void runThreeBallShot(int nextState) {
        if (shooterState == ShooterState.IDLE) {
            startShooterStateMachine();
        }

        shooter.shootSlow();

        if (shooterTotalTimer.milliseconds() >= SHOOT_SEQUENCE_TIMEOUT_MS) {
            finishShooting(nextState);
            return;
        }

        switch (shooterState) {
            case SPINNING_UP:
                intake.stop();
                servos.setStopper(STOPPER_CLOSED);
                if (shooter.readyForSlowShot()
                        || shooterStateTimer.milliseconds() >= SHOOTER_READY_TIMEOUT_MS) {
                    shooterState = ShooterState.FEEDING;
                    shooterStateTimer.reset();
                }
                break;

            case FEEDING:
                servos.setStopper(STOPPER_OPEN);
                intake.intakeOut();
                if (shooterStateTimer.milliseconds() >= FEED_PULSE_MS) {
                    ballsShot++;
                    servos.setStopper(STOPPER_CLOSED);
                    shooterState = ballsShot >= BALLS_PER_CYCLE
                            ? ShooterState.COMPLETE
                            : ShooterState.BETWEEN_SHOTS;
                    shooterStateTimer.reset();
                }
                break;

            case BETWEEN_SHOTS:
                servos.setStopper(STOPPER_CLOSED);
                intake.intakeOut();
                if (shooterStateTimer.milliseconds() >= SHOT_DELAY_MS) {
                    shooterState = ShooterState.FEEDING;
                    shooterStateTimer.reset();
                }
                break;

            case COMPLETE:
                finishShooting(nextState);
                break;

            case IDLE:
            default:
                startShooterStateMachine();
                break;
        }
    }

    private void finishShooting(int nextState) {
        servos.setStopper(STOPPER_CLOSED);
        intake.stop();
        shooter.stop();
        shooterState = ShooterState.COMPLETE;
        pathState = nextState;
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                startShootPath(paths.shoot1, 1);
                break;

            case 1:
                if (pathDoneOrTimedOut()) {
                    pathState = 2;
                }
                break;

            case 2:
                runThreeBallShot(3);
                break;

            case 3:
                startIntakePath(paths.intake1, 4);
                break;

            case 4:
                if (pathDoneOrTimedOut()) {
                    startOpenPath(paths.open1, 5);
                }
                break;

            case 5:
                if (pathDoneOrTimedOut()) {
                    beginWait();
                    pathState = 6;
                }
                break;

            case 6:
                waitWithIntakeOff(7);
                break;

            case 7:
                startShootPath(paths.shoot2, 8);
                break;

            case 8:
                if (pathDoneOrTimedOut()) {
                    pathState = 9;
                }
                break;

            case 9:
                runThreeBallShot(10);
                break;

            case 10:
                startIntakePath(paths.intake2, 11);
                break;

            case 11:
                if (pathDoneOrTimedOut()) {
                    startOpenPath(paths.open2, 12);
                }
                break;

            case 12:
                if (pathDoneOrTimedOut()) {
                    beginWait();
                    pathState = 13;
                }
                break;

            case 13:
                waitWithIntakeOff(14);
                break;

            case 14:
                startShootPath(paths.shoot3, 15);
                break;

            case 15:
                if (pathDoneOrTimedOut()) {
                    pathState = 16;
                }
                break;

            case 16:
                runThreeBallShot(17);
                break;

            case 17:
                startPathToIntake(paths.pathTOintake1, 18);
                break;

            case 18:
                if (pathDoneOrTimedOut()) {
                    startIntakePath(paths.intake3, 19);
                }
                break;

            case 19:
                if (pathDoneOrTimedOut()) {
                    beginWait();
                    pathState = 20;
                }
                break;

            case 20:
                waitWithIntakeOn(21);
                break;

            case 21:
                startShootPath(paths.shoot4, 22);
                break;

            case 22:
                if (pathDoneOrTimedOut()) {
                    pathState = 23;
                }
                break;

            case 23:
                runThreeBallShot(24);
                break;

            case 24:
                startPathToIntake(paths.pathTOintake2, 25);
                break;

            case 25:
                if (pathDoneOrTimedOut()) {
                    startIntakePath(paths.intake4, 26);
                }
                break;

            case 26:
                if (pathDoneOrTimedOut()) {
                    beginWait();
                    pathState = 27;
                }
                break;

            case 27:
                waitWithIntakeOn(28);
                break;

            case 28:
                startShootPath(paths.shoot5, 29);
                break;

            case 29:
                if (pathDoneOrTimedOut()) {
                    pathState = 30;
                }
                break;

            case 30:
                runThreeBallShot(31);
                break;

            case 31:
                startPathToIntake(paths.pathTOintake3, 32);
                break;

            case 32:
                if (pathDoneOrTimedOut()) {
                    startIntakePath(paths.intake5, 33);
                }
                break;

            case 33:
                if (pathDoneOrTimedOut()) {
                    beginWait();
                    pathState = 34;
                }
                break;

            case 34:
                waitWithIntakeOn(35);
                break;

            case 35:
                startShootPath(paths.shoot6, 36);
                break;

            case 36:
                if (pathDoneOrTimedOut()) {
                    pathState = 37;
                }
                break;

            case 37:
                runThreeBallShot(38);
                break;

            case 38:
                startPathToIntake(paths.pathTOintake4, 39);
                break;

            case 39:
                if (pathDoneOrTimedOut()) {
                    startIntakePath(paths.intake6, 40);
                }
                break;

            case 40:
                if (pathDoneOrTimedOut()) {
                    beginWait();
                    pathState = 41;
                }
                break;

            case 41:
                waitWithIntakeOn(42);
                break;

            case 42:
                startShootPath(paths.shoot7, 43);
                break;

            case 43:
                if (pathDoneOrTimedOut()) {
                    pathState = 44;
                }
                break;

            case 44:
                runThreeBallShot(45);
                break;

            case 45:
            default:
                intake.stop();
                shooter.stop();
                turret.stop();
                servos.setStopper(STOPPER_CLOSED);
                shooterState = ShooterState.IDLE;
                break;
        }
    }

    private void updateTelemetry() {
        Pose pose = follower.getPose();

        telemetry.addData("Path State", pathState);
        telemetry.addData("Shooter State", shooterState);
        telemetry.addData("Balls This Cycle", "%d / %d", ballsShot, BALLS_PER_CYCLE);
        telemetry.addData("Shot Timer ms", "%.0f", shooterStateTimer.milliseconds());
        telemetry.addData("Sequence Timer ms", "%.0f", shooterTotalTimer.milliseconds());
        telemetry.addData("Auto Timer s", "%.1f", autoTimer.seconds());
        telemetry.addData("RPM", "%.1f", shooter.getAverageVelocity());
        telemetry.addData("X", "%.2f", pose.getX());
        telemetry.addData("Y", "%.2f", pose.getY());
        telemetry.addData("Heading Deg", "%.2f", Math.toDegrees(pose.getHeading()));
        telemetry.addData("Turret Position", turret.getPosition());
        telemetry.update();

        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("Shooter State", shooterState);
        panelsTelemetry.debug("Balls This Cycle", ballsShot + " / " + BALLS_PER_CYCLE);
        panelsTelemetry.debug("Shot Timer ms", shooterStateTimer.milliseconds());
        panelsTelemetry.debug("RPM", shooter.getAverageVelocity());
        panelsTelemetry.debug("Auto Timer s", autoTimer.seconds());
        panelsTelemetry.debug("X", pose.getX());
        panelsTelemetry.debug("Y", pose.getY());
        panelsTelemetry.debug("Heading", pose.getHeading());
        panelsTelemetry.update(telemetry);
    }

    public static class Paths {
        public PathChain shoot1;
        public PathChain intake1;
        public PathChain open1;
        public PathChain shoot2;
        public PathChain intake2;
        public PathChain open2;
        public PathChain shoot3;
        public PathChain pathTOintake1;
        public PathChain intake3;
        public PathChain shoot4;
        public PathChain pathTOintake2;
        public PathChain intake4;
        public PathChain shoot5;
        public PathChain pathTOintake3;
        public PathChain intake5;
        public PathChain shoot6;
        public PathChain pathTOintake4;
        public PathChain intake6;
        public PathChain shoot7;

        public Paths(Follower follower) {

            shoot1 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(110.000, 131.000),
                            new Pose(83.000, 82.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(0))
                    .build();

            intake1 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(83.000, 82.000),
                            new Pose(126.000, 82.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(5), Math.toRadians(0))
                    .build();

            open1 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(126.000, 82.000),
                            new Pose(120.000, 78.000),
                            new Pose(126.000, 74.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot2 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(126.000, 74.000),
                            new Pose(84.000, 82.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-60))
                    .build();

            intake2 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(84.000, 82.000),
                            new Pose(84.000, 56.000),
                            new Pose(126.000, 58.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(-60), Math.toRadians(0))
                    .build();

            open2 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(126.000, 58.000),
                            new Pose(121.000, 61.000),
                            new Pose(126.000, 63.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            shoot3 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(126.000, 63.000),
                            new Pose(84.000, 82.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-40))
                    .setReversed()
                    .build();

            pathTOintake1 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(84.000, 82.000),
                            new Pose(98.000, 64.000),
                            new Pose(123.000, 64.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(-40), Math.toRadians(0))
                    .build();

            intake3 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(123.000, 64.000),
                            new Pose(128.000, 56.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(35))
                    .build();

            shoot4 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(128.000, 56.000),
                            new Pose(92.812, 64.500),
                            new Pose(84.000, 82.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-40))
                    .build();

            pathTOintake2 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(84.000, 82.000),
                            new Pose(98.000, 64.000),
                            new Pose(123.000, 64.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(-40), Math.toRadians(0))
                    .build();

            intake4 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(123.000, 64.000),
                            new Pose(128.000, 56.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(35))
                    .build();

            shoot5 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(128.000, 56.000),
                            new Pose(84.000, 82.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-40))
                    .build();

            pathTOintake3 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(84.000, 82.000),
                            new Pose(98.000, 64.000),
                            new Pose(123.000, 64.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(-40), Math.toRadians(0))
                    .build();

            intake5 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(123.000, 64.000),
                            new Pose(128.000, 56.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(35))
                    .build();

            shoot6 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(128.000, 56.000),
                            new Pose(84.000, 82.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-40))
                    .build();

            pathTOintake4 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(84.000, 82.000),
                            new Pose(98.000, 64.000),
                            new Pose(123.000, 64.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(-40), Math.toRadians(0))
                    .build();

            intake6 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(123.000, 64.000),
                            new Pose(128.000, 56.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(35))
                    .build();

            shoot7 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(128.000, 56.000),
                            new Pose(84.000, 82.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-40))
                    .build();
        }
    }
}
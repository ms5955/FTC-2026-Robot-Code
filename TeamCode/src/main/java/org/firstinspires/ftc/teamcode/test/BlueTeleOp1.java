package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.hardware.limelightvision.*;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
@Disabled
@TeleOp
public class BlueTeleOp1 extends LinearOpMode {

    // ================= HARDWARE =================
    DcMotor FL, FR, BL, BR;
    DcMotorEx shooterL, shooterR;
    DcMotor intake;
    Servo feederServo;
    Limelight3A limelight;

    // ================= DRIVE =================
    static final double TURN_MULTIPLIER = 0.9;

    // ================= SHOOTER TARGET SPEEDS =================
    static final double VELO_HIGH = 1600;
    static final double VELO_MID  = 1250;
    static final double VELO_REV  = 200;

    static final double VELO_RAMP_RATE = 120;

    // 🔒 ABSOLUTE HARD LIMIT
    static final double MAX_ACTUAL_VELO = 1700;

    double currentTargetVelo = 0;
    double commandedVelo = 0;

    // ================= APRILTAG =================
    static final int TAG_ID = 20;
    static final double KP = 0.02;
    static final double TOLERANCE = 1.5;
    static final double MAX_TURN = 0.5;
    static final double MIN_TURN = 0.08;

    // ================= SERVO =================
    static final double SERVO_FORWARD = 0.2;
    static final double SERVO_BACK = 0.0;

    @Override
    public void runOpMode() {

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        shooterL = hardwareMap.get(DcMotorEx.class, "ShooterL");
        shooterR = hardwareMap.get(DcMotorEx.class, "ShooterR");
        intake   = hardwareMap.get(DcMotor.class, "I");
        feederServo = hardwareMap.get(Servo.class, "Sservo");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // ================= DRIVE =================
        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.FORWARD);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // ================= SHOOTER =================
        shooterL.setDirection(DcMotor.Direction.REVERSE);
        shooterR.setDirection(DcMotor.Direction.FORWARD);

        shooterL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // 🔧 STABLE PIDF (NO OVERSHOOT)
        PIDFCoefficients shooterPIDF =
                new PIDFCoefficients(6.0, 0.0, 0.0, 5.5);

        shooterL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, shooterPIDF);
        shooterR.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, shooterPIDF);

        feederServo.setDirection(Servo.Direction.REVERSE);
        feederServo.setPosition(SERVO_BACK);

        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry.addLine("Shooter HARD CAPPED @ 1100");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.a) autoAlign();
            else manualDrive();

            shooterControl();
            intakeControl();
            servoControl();

            double actualL = Math.min(shooterL.getVelocity(), MAX_ACTUAL_VELO);
            double actualR = Math.min(shooterR.getVelocity(), MAX_ACTUAL_VELO);

            telemetry.addData("Target", currentTargetVelo);
            telemetry.addData("Cmd", commandedVelo);
            telemetry.addData("Actual L", actualL);
            telemetry.addData("Actual R", actualR);
            telemetry.addData("MAX", MAX_ACTUAL_VELO);
            telemetry.update();
        }

        limelight.stop();
    }

    // ================= SHOOTER CONTROL =================
    private void shooterControl() {

        if (gamepad1.right_bumper) currentTargetVelo = VELO_HIGH;
        else if (gamepad1.y) currentTargetVelo = VELO_MID;
        else if (gamepad1.right_trigger > 0.1) currentTargetVelo = VELO_REV;
        else currentTargetVelo = 0;

        if (commandedVelo < currentTargetVelo)
            commandedVelo += VELO_RAMP_RATE;
        else if (commandedVelo > currentTargetVelo)
            commandedVelo -= VELO_RAMP_RATE;

        commandedVelo = Math.max(0, Math.min(commandedVelo, MAX_ACTUAL_VELO));

        double actualMax = Math.max(shooterL.getVelocity(), shooterR.getVelocity());
        if (actualMax > MAX_ACTUAL_VELO) {
            commandedVelo = MAX_ACTUAL_VELO - 120; // HARD PULLBACK
        }

        shooterL.setVelocity(commandedVelo);
        shooterR.setVelocity(commandedVelo);
    }

    // ================= DRIVE =================
    private void manualDrive() {
        double drive  = gamepad1.left_stick_y;
        double strafe = -gamepad1.left_stick_x;
        double turn   = gamepad1.right_stick_x * TURN_MULTIPLIER;

        double fl = drive + strafe - turn;
        double bl = drive - strafe - turn;
        double fr = drive - strafe + turn;
        double br = drive + strafe + turn;

        normalizeAndSet(fl, bl, fr, br);
    }

    // ================= INTAKE =================
    private void intakeControl() {
        if (gamepad1.left_bumper) intake.setPower(0.85);
        else if (gamepad1.left_trigger > 0.1) intake.setPower(-0.4);
        else intake.setPower(0);
    }

    // ================= SERVO =================
    private void servoControl() {
        if (gamepad1.dpad_up) feederServo.setPosition(SERVO_BACK);
        else if (gamepad1.dpad_down) feederServo.setPosition(SERVO_FORWARD);
    }

    // ================= HELPERS =================
    private void normalizeAndSet(double fl, double bl, double fr, double br) {
        double max = Math.max(Math.max(Math.abs(fl), Math.abs(bl)),
                Math.max(Math.abs(fr), Math.abs(br)));
        if (max > 1.0) {
            fl /= max; bl /= max; fr /= max; br /= max;
        }
        FL.setPower(fl);
        BL.setPower(bl);
        FR.setPower(fr);
        BR.setPower(br);
    }

    private void stopDrive() {
        FL.setPower(0); FR.setPower(0);
        BL.setPower(0); BR.setPower(0);
    }

    // ================= AUTO ALIGN =================
    private void autoAlign() {
        LLResult result = limelight.getLatestResult();
        LLResultTypes.FiducialResult tag = null;

        if (result != null && result.isValid()) {
            for (LLResultTypes.FiducialResult f : result.getFiducialResults()) {
                if (f.getFiducialId() == TAG_ID) { tag = f; break; }
            }
        }

        if (tag == null) { stopDrive(); return; }

        double error = -tag.getTargetXDegrees();
        double turn = Math.max(-MAX_TURN,
                Math.min(MAX_TURN, error * KP));

        if (Math.abs(turn) < MIN_TURN && Math.abs(error) > TOLERANCE)
            turn = Math.signum(turn) * MIN_TURN;

        if (Math.abs(error) < TOLERANCE) turn = 0;

        FL.setPower(turn);
        BL.setPower(turn);
        FR.setPower(-turn);
        BR.setPower(-turn);
    }
}

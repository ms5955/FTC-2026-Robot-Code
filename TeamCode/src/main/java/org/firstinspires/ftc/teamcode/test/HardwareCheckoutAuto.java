package org.firstinspires.ftc.teamcode.test;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
@Disabled
@Autonomous(name = "HardwareCheckoutAuto")
public class HardwareCheckoutAuto extends LinearOpMode {

    enum TestState {
        INIT,
        TEST_FL,
        TEST_FR,
        TEST_BL,
        TEST_BR,
        TEST_INTAKE,
        TEST_TURRET,
        TEST_SHOOTER,
        TEST_SERVOS,
        COMPLETE
    }

    private TestState state = TestState.INIT;

    private DcMotorEx FL, FR, BL, BR;
    private DcMotorEx intake;
    private DcMotorEx turret;
    private DcMotorEx shooterLeft;
    private DcMotorEx shooterRight;

    private Servo ballStop;
    private Servo hood;

    @Override
    public void runOpMode() {

        // Hardware Mapping
        FL = hardwareMap.get(DcMotorEx.class, "FL");
        FR = hardwareMap.get(DcMotorEx.class, "FR");
        BL = hardwareMap.get(DcMotorEx.class, "BL");
        BR = hardwareMap.get(DcMotorEx.class, "BR");

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        turret = hardwareMap.get(DcMotorEx.class, "turret");

        shooterLeft = hardwareMap.get(DcMotorEx.class, "shooterLeft");
        shooterRight = hardwareMap.get(DcMotorEx.class, "shooterRight");

        ballStop = hardwareMap.get(Servo.class, "ballStop");
        hood = hardwareMap.get(Servo.class, "hood");

        // Reset Encoders
        resetEncoder(FL);
        resetEncoder(FR);
        resetEncoder(BL);
        resetEncoder(BR);

        resetEncoder(intake);
        resetEncoder(turret);

        resetEncoder(shooterLeft);
        resetEncoder(shooterRight);

        telemetry.addLine("================================");
        telemetry.addLine("DECODE HARDWARE CHECK");
        telemetry.addLine("All encoders reset to zero");
        telemetry.addLine("Place robot on stand");
        telemetry.addLine("Press START");
        telemetry.addLine("================================");

        telemetry.addData("FL", FL.getCurrentPosition());
        telemetry.addData("FR", FR.getCurrentPosition());
        telemetry.addData("BL", BL.getCurrentPosition());
        telemetry.addData("BR", BR.getCurrentPosition());

        telemetry.addData("Intake", intake.getCurrentPosition());
        telemetry.addData("Turret", turret.getCurrentPosition());

        telemetry.addData("Shooter L", shooterLeft.getCurrentPosition());
        telemetry.addData("Shooter R", shooterRight.getCurrentPosition());

        telemetry.update();

        waitForStart();

        if(isStopRequested()) return;

        testMotor("FL", TestState.TEST_FL, FL, 0.25, 1000);
        testMotor("FR", TestState.TEST_FR, FR, 0.25, 1000);
        testMotor("BL", TestState.TEST_BL, BL, 0.25, 1000);
        testMotor("BR", TestState.TEST_BR, BR, 0.25, 1000);

        testMotor("INTAKE", TestState.TEST_INTAKE, intake, 0.15, 1000);

        testMotor("TURRET", TestState.TEST_TURRET, turret, 0.25, 1000);

        state = TestState.TEST_SHOOTER;
        showState();

        shooterLeft.setPower(0.25);
        shooterRight.setPower(0.25);

        sleep(1500);

        shooterLeft.setPower(0);
        shooterRight.setPower(0);

        state = TestState.TEST_SERVOS;
        showState();

        ballStop.setPosition(0.0);
        sleep(500);

        ballStop.setPosition(0.5);
        sleep(500);

        ballStop.setPosition(1.0);
        sleep(500);

        hood.setPosition(0.30);
        sleep(1000);

        hood.setPosition(0.60);
        sleep(1000);

        hood.setPosition(0.45);

        state = TestState.COMPLETE;

        while(opModeIsActive()) {

            telemetry.clearAll();

            telemetry.addLine("===== TEST COMPLETE =====");

            telemetry.addData("STATE", state);

            telemetry.addLine("");

            telemetry.addData("FL Encoder", FL.getCurrentPosition());
            telemetry.addData("FR Encoder", FR.getCurrentPosition());
            telemetry.addData("BL Encoder", BL.getCurrentPosition());
            telemetry.addData("BR Encoder", BR.getCurrentPosition());

            telemetry.addData("Intake Encoder", intake.getCurrentPosition());
            telemetry.addData("Turret Encoder", turret.getCurrentPosition());

            telemetry.addData("Shooter L Encoder", shooterLeft.getCurrentPosition());
            telemetry.addData("Shooter R Encoder", shooterRight.getCurrentPosition());

            telemetry.addLine("");
            telemetry.addLine("Verify all encoders changed");

            telemetry.update();
        }
    }

    private void resetEncoder(DcMotorEx motor) {

        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    private void testMotor(String name,
                           TestState currentState,
                           DcMotorEx motor,
                           double power,
                           long duration) {

        state = currentState;

        telemetry.clearAll();
        telemetry.addData("STATE", state);
        telemetry.addData("Testing", name);
        telemetry.update();

        int before = motor.getCurrentPosition();

        motor.setPower(power);

        sleep(duration);

        motor.setPower(0);

        int after = motor.getCurrentPosition();

        telemetry.clearAll();
        telemetry.addData("Motor", name);
        telemetry.addData("Before", before);
        telemetry.addData("After", after);
        telemetry.update();

        sleep(700);
    }

    private void showState() {

        telemetry.clearAll();
        telemetry.addData("STATE", state);
        telemetry.update();
    }
}
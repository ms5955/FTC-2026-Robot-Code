package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Disabled
@TeleOp(name = "Turret Encoder Test")
public class TurretEncoderTest extends LinearOpMode {

    @Override
    public void runOpMode() {

        DcMotorEx turret = hardwareMap.get(DcMotorEx.class, "turret");

        turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        while (opModeIsActive()) {

            telemetry.addData("Encoder", turret.getCurrentPosition());
            telemetry.update();
        }
    }
}
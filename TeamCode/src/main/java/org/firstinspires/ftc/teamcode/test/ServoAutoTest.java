package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
@Disabled
@Autonomous(name = "Servo Auto Test")
public class ServoAutoTest extends LinearOpMode {

    Servo servo1;

    @Override
    public void runOpMode() {

        servo1 = hardwareMap.get(Servo.class, "servo1");

        waitForStart();

        servo1.setPosition(0.0);

        sleep(2000);

        servo1.setPosition(1.0);

        sleep(2000);
    }
}
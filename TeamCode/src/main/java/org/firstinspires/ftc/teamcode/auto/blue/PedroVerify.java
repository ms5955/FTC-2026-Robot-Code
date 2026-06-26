package org.firstinspires.ftc.teamcode.auto.blue;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
@Disabled
@Autonomous(name = "Pedro Verify")
public class PedroVerify extends LinearOpMode {

    private Follower follower;

    @Override
    public void runOpMode() {

        MecanumConstants mecanum = new MecanumConstants()
                .leftFrontMotorName("FL")
                .rightFrontMotorName("FR")
                .leftRearMotorName("BL")
                .rightRearMotorName("BR");

        PinpointConstants pinpoint = new PinpointConstants()
                .hardwareMapName("pinpoint");

        follower = new FollowerBuilder(
                new FollowerConstants(),
                hardwareMap
        )
                .mecanumDrivetrain(mecanum)
                .pinpointLocalizer(pinpoint)
                .build();

        follower.setStartingPose(
                BlueConstants.START
        );

        telemetry.addLine("Pedro Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            follower.update();

            telemetry.addData("X", follower.getPose().getX());
            telemetry.addData("Y", follower.getPose().getY());
            telemetry.addData("Heading",
                    Math.toDegrees(follower.getPose().getHeading()));

            telemetry.addData("Busy", follower.isBusy());

            telemetry.update();
        }
    }
}
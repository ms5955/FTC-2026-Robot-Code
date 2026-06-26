package org.firstinspires.ftc.teamcode.auto.blue;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
@Disabled
@Autonomous(name = "Pedro Drive Test")
public class PedroDriveTest extends LinearOpMode {

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

        Pose startPose = new Pose(0, 0, 0);
        Pose endPose = new Pose(24, 0, 0);

        follower.setStartingPose(startPose);

        PathChain testPath = follower.pathBuilder()

                .addPath(
                        new Path(
                                BezierCurve.through(
                                        startPose,
                                        new Pose(12, 0),
                                        endPose
                                )
                        )
                )

                .setConstantHeadingInterpolation(0)

                .build();

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        follower.followPath(testPath);

        while (opModeIsActive()) {

            follower.update();

            telemetry.addData("Busy", follower.isBusy());
            telemetry.addData("X", follower.getPose().getX());
            telemetry.addData("Y", follower.getPose().getY());
            telemetry.addData("Heading",
                    Math.toDegrees(follower.getPose().getHeading()));

            telemetry.update();
        }
    }
}
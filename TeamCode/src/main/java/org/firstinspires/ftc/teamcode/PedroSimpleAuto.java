package org.firstinspires.ftc.teamcode;

import com.pedropathing.Constants;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.RGBSubsystem;

@Disabled
@Autonomous(name = "Pedro Slow Auto")
public class PedroSimpleAuto extends LinearOpMode {

    private Follower follower;
    private RGBSubsystem rgb;

    @Override
    public void runOpMode() {

        follower = Constants.createFollower(hardwareMap);
        rgb = new RGBSubsystem(hardwareMap);
        // Starting Pose
        follower.setStartingPose(new Pose(110, 131, 0));

        // VERY SLOW
        follower.setMaxPower(0.4);
        // Create path
        PathChain path = follower.pathBuilder()
                .addPath(
                new BezierCurve(
                        new Pose(110.000, 131.000),
                        new Pose(84.000, 82.000)
                )
                )
                .addParametricCallback(0.25, () -> {
                rgb.green();
                })
                .addParametricCallback(0.50, () -> {
                    rgb.blue();
                })
                .addParametricCallback(0.75, () -> {
                    rgb.red();
                })
                .addParametricCallback(1, () -> {
                    rgb.purple();
                })
                .build();
        waitForStart();

        if (isStopRequested()) return;

        follower.followPath(path);

        while (opModeIsActive()) {

            follower.update();

            telemetry.addData("X", follower.getPose().getX());
            telemetry.addData("Y", follower.getPose().getY());
            telemetry.addData("Heading",
                    Math.toDegrees(follower.getPose().getHeading()));

            telemetry.update();

            if (!follower.isBusy()) {
                break;
            }
        }
    }
}
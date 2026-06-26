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
@Autonomous(name = "Blue Auto")
public class BlueAuto extends LinearOpMode {

    private Follower follower;

    private BluePaths paths;

    private BlueStates state =
            BlueStates.SHOOT_PRELOAD;

    @Override
    public void runOpMode() {

        MecanumConstants mecanum =
                new MecanumConstants()

                        .leftFrontMotorName("FL")
                        .rightFrontMotorName("FR")
                        .leftRearMotorName("BL")
                        .rightRearMotorName("BR");

        PinpointConstants pinpoint =
                new PinpointConstants()

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

        paths = new BluePaths(follower);

        waitForStart();

        follower.followPath(
                paths.preloadPath
        );

        while (opModeIsActive()) {

            follower.update();

            switch (state) {

                case SHOOT_PRELOAD:

                    if (!follower.isBusy()) {

                        // shoot preload

                        follower.followPath(
                                paths.collectBall1
                        );

                        state =
                                BlueStates.COLLECT_BALL_1;
                    }

                    break;

                case COLLECT_BALL_1:

                    if (!follower.isBusy()) {

                        follower.followPath(
                                paths.shootBall1
                        );

                        state =
                                BlueStates.SHOOT_BALL_1;
                    }

                    break;

                case SHOOT_BALL_1:

                    if (!follower.isBusy()) {

                        follower.followPath(
                                paths.collectBall2
                        );

                        state =
                                BlueStates.COLLECT_BALL_2;
                    }

                    break;

                case COLLECT_BALL_2:

                    if (!follower.isBusy()) {

                        follower.followPath(
                                paths.shootBall2
                        );

                        state =
                                BlueStates.SHOOT_BALL_2;
                    }

                    break;

                case SHOOT_BALL_2:

                    if (!follower.isBusy()) {

                        follower.followPath(
                                paths.collectBall3
                        );

                        state =
                                BlueStates.COLLECT_BALL_3;
                    }

                    break;

                case COLLECT_BALL_3:

                    if (!follower.isBusy()) {

                        follower.followPath(
                                paths.shootBall3
                        );

                        state =
                                BlueStates.SHOOT_BALL_3;
                    }

                    break;

                case SHOOT_BALL_3:

                    if (!follower.isBusy()) {

                        follower.followPath(
                                paths.parkPath
                        );

                        state =
                                BlueStates.PARK;
                    }

                    break;

                case PARK:

                    if (!follower.isBusy()) {

                        state =
                                BlueStates.IDLE;
                    }

                    break;

                case IDLE:
                    break;
            }

            telemetry.addData(
                    "State",
                    state
            );

            telemetry.addData(
                    "X",
                    follower.getPose().getX()
            );

            telemetry.addData(
                    "Y",
                    follower.getPose().getY()
            );

            telemetry.addData(
                    "Heading",
                    Math.toDegrees(
                            follower.getPose().getHeading()
                    )
            );

            telemetry.update();
        }
    }
}
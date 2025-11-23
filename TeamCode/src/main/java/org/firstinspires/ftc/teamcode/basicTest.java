/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/*
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp(name="decode1024", group="Linear OpMode")
public class basicTest extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontRight = null;
    private DcMotor frontLeft = null;
    private DcMotor backRight = null;
    private DcMotor backLeft = null;
    private DcMotorEx outtake1 = null;
    private DcMotor intake1 = null;
//    double outtakePower = 0.0;
    double intakePower = 0.0;
    private Servo blockerR;
    private Servo blockerL;
    private CRServo pushR;
    private CRServo pushL;
    double targetOuttakeVelocity = 0.0;
    double FAR_OUTTAKE_VELOCITY = 1500;
    double CLOSE_OUTTAKE_VELOCITY = 1300; // 11.21_ 1400 too high
    double FAR_PIVOT_POSITION = 0.73;
    double CLOSE_PIVOT_POSITION = 0.30;
    double PUSH_POWER = 0.9;
    double R_BLOCKER_UP = 0.65;
    double R_BLOCKER_DOWN = 0.0;


    private Servo pivot;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        frontRight  = hardwareMap.get(DcMotor.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");

        outtake1  = hardwareMap.get(DcMotorEx.class, "outtake1");
        intake1 = hardwareMap.get(DcMotor.class,"intake1");

        blockerR = hardwareMap.get(Servo.class, "blockerL");
        blockerL = hardwareMap.get(Servo.class, "blockerR");
        double blockerPositionR = R_BLOCKER_DOWN;
        double blockerPositionL = 1.0;

        pushR = hardwareMap.get(CRServo.class, "pushR");
        pushL = hardwareMap.get(CRServo.class, "pushL");

        pivot = hardwareMap.get(Servo.class, "pivot");
        double pivotPosition = CLOSE_PIVOT_POSITION;

        blockerR.setPosition(Range.clip(blockerPositionR, 0.0, 1.0));
        blockerL.setPosition(Range.clip(blockerPositionL, 0.0, 1.0));
//        pushR.setPosition(Range.clip(pushPositionR,0.0,1.0));
        pivot.setPosition(Range.clip(pivotPosition, 0.0, 1.0));

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        outtake1.setDirection(DcMotorEx.Direction.REVERSE);
        outtake1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        outtake1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        outtake1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        outtake1.setVelocity(targetOuttakeVelocity);

        intake1.setDirection(DcMotorSimple.Direction.FORWARD);

        outtake1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start (driver presses START)
        float step = 0.001f;
        double outtakeStep = 7.0;

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry


            double y = gamepad1.left_stick_y; // Remember, Y stick is reversed!
            double x = -gamepad1.left_stick_x;
            double rx = -gamepad1.right_stick_x;
//
            frontLeft.setPower(Range.clip((y + x + rx),-0.6,0.6));
            backLeft.setPower(Range.clip((y - x + rx),-0.6,0.6));
            frontRight.setPower(Range.clip((y - x - rx),-0.6,0.6));
            backRight.setPower(Range.clip((y + x - rx),-0.6,0.6));

            if(gamepad1.a) {
                outtake1.setVelocity(FAR_OUTTAKE_VELOCITY);
//                outtake1.setVelocity(Vrariables.);
            } else if(gamepad1.b) {
                outtake1.setVelocity(CLOSE_OUTTAKE_VELOCITY);
            } else if (gamepad1.x) {
//                pivotPosition = CLOSE_PIVOT_POSITION;
                pivotPosition += step;
                pivotPosition = Range.clip(pivotPosition, 0.0, 1.0);
                pivot.setPosition(pivotPosition);
            } else if (gamepad1.y) {
//                pivotPosition = FAR_PIVOT_POSITION;
                pivotPosition -= step;
                pivotPosition = Range.clip(pivotPosition, 0.0, 1.0);
                pivot.setPosition(pivotPosition);
            } else if (gamepad2.y) {
                blockerPositionR = R_BLOCKER_DOWN;
//                blockerR.setPosition(blockerPositionR);
//                blockerPositionR += step;
                blockerR.setPosition(Range.clip(blockerPositionR, 0.0 , R_BLOCKER_UP));
//                blockerPosition = Range.clip(blockerPosition, 0.0, 1.0);
            } else if(gamepad1.dpad_left){
                intakePower = -1.0;
            } else if (gamepad1.dpad_right) {
                outtake1.setVelocity(0.0);
            } else if (gamepad2.a){
                blockerPositionR = R_BLOCKER_UP;
//                blockerR.setPosition(blockerPositionR);
//                blockerPositionR -= step;
                blockerR.setPosition(Range.clip(blockerPositionR, 0.0, R_BLOCKER_UP));
//                blockerPosition += step;
//                blockerPosition = Range.clip(blockerPosition, 0.0, 1.0);
            } else if (gamepad2.x) {
                blockerPositionL = 1.0;
//                blockerPositionL += step;
//                blockerL.setPosition(blockerPositionL);
                blockerL.setPosition(Range.clip(blockerPositionL, 0.0, 1.0));
            } else if (gamepad2.b){
                blockerPositionL = 0.15;
//                blockerL.setPosition(blockerPositionL);
//                blockerPositionL -= step;
                blockerL.setPosition(Range.clip(blockerPositionL, 0.0, 1.0));
            } else if (gamepad2.right_bumper) {
                pushR.setPower(PUSH_POWER); // subtracting brings ball in
//                pushR.setPosition(pushPositionR);
            } else if (gamepad2.left_bumper){
                pushR.setPower(0.0);
            } else if (gamepad2.dpad_up) {
                pushL.setPower(-PUSH_POWER); // subtracting brings ball in
//                pushR.setPosition(pushPositionR);
            }else if (gamepad2.dpad_down) {
                pushL.setPower(0.0); // subtracting brings ball in
//                pushR.setPosition(pushPositionR);
            }else if (gamepad1.right_bumper) {
                intakePower = 1.0;
                pushR.setPower(PUSH_POWER); // subtracting brings ball in
            } else if (gamepad1.left_bumper){
                intakePower = 0.0;
                pushR.setPower(0.0); // subtracting brings ball in
            } else if (gamepad1.dpad_up){
//                outtakePower+=outtakeStep;
                targetOuttakeVelocity = targetOuttakeVelocity + outtakeStep;
                outtake1.setVelocity(Range.clip(targetOuttakeVelocity,0.0,FAR_OUTTAKE_VELOCITY));
            }else if (gamepad1.dpad_down){
//                outtakePower-=outtakeStep;
                targetOuttakeVelocity = targetOuttakeVelocity - outtakeStep;
                outtake1.setVelocity(Range.clip(targetOuttakeVelocity,0.0,FAR_OUTTAKE_VELOCITY));
            }
//            outtake1.setPower(outtakePower);
            intake1.setPower(intakePower);
//            blockerR.setPosition(Range.clip(blockerPositionR, 0.0, 1.0));

//            pivot.setPosition(pivotPosition);
//            blocker.setPosition(blockerPosition);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
//            telemetry.addData("Outtake Power", "Outtake Power: " + outtakePower);
            telemetry.addData("Intake Power", "Intake Power: " + intakePower);
            telemetry.addData("blockerPosLEFT", "Position: " + blockerPositionL);
            telemetry.addData("blockerPosRIGHT", "Position: " + blockerPositionR);
            telemetry.addData("Current Position", "Position: " + pivotPosition);
            telemetry.addData("Target Velocity", targetOuttakeVelocity);
            telemetry.addData("power", outtake1.getPower());
            telemetry.addData("Outtake Velocity", outtake1.getVelocity());
            telemetry.update();
        }
    }
}

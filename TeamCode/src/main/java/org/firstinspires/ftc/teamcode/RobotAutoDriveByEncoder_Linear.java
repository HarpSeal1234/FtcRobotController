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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * This OpMode illustrates the concept of driving a path based on encoder counts.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: RobotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forward, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backward for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This method assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

// port 1 223 vertical2
// port 0 vertical1 1170
//port 2 vertical3 1170
@TeleOp(name = "Control Linear Slide By Encoder", group = "Robot")
public class RobotAutoDriveByEncoder_Linear extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotorEx vertical1 = null;
    private DcMotorEx vertical2 = null;
    private DcMotorEx vertical3 = null;

    private ElapsedTime runtime = new ElapsedTime();

    // Calculate the COUNTS_PER_INCH for your specific drive train.
    // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
    // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
    // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
    // This is gearing DOWN for less speed and more torque.
    // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
    static final double COUNTS_PER_MOTOR_REV = 1440;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // No External Gearing.
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED = 0.6;
    static final double TURN_SPEED = 0.5;

    //PIDF
    //VERTICAL ONE
    private double verticalOneF = 10.995; //32767/motorOneMaxVelocity
    private double verticalOnekP = 0.9;
    private double verticalOnekI = 1.25;
    private double verticalOnekD = 0.007;
    private double verticalOnePosition = 5.0;
    private double verticalOneZeroPower = 0.0;


    //VERTICAL TWO
    private double verticalTwoZeroPower = 0.0;
    private double verticalTwoF = 10.782;
    private double verticalTwokP = 1.0;
    private double verticalTwokI = 0.0;
    private double verticalTwokD = 0.005;
    private double verticalTwoPosition = 5.0;


//VERTICAL THREE
    private double verticalThreeZeroPower = 0.0;
    private double verticalThreekP = 1.0;
    private double verticalThreekI = 0.0;
    private double verticalThreekD =  0.005;
    private double verticalThreeF = 10.782;
    private double verticalThreePosition = 5.0;


    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        initMotors();

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips


        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Starting at", "%7d :%7d",
                vertical1.getCurrentPosition(),
                vertical2.getCurrentPosition(),
                vertical3.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        while (opModeIsActive()) {
            int verticalOneTargetPosition = vertical1.getCurrentPosition();
            int verticalThreeTargetPosition = vertical3.getCurrentPosition();

            // Step through each leg of the path,
            // Note: Reverse movement is obtained by setting a negative distance (not speed)
        int verticalTwoTargetPosition = vertical2.getCurrentPosition();

            // Determine new target position, and pass to motor controller
            if (gamepad1.y) {
                verticalOneTargetPosition = verticalOneTargetPosition + 150;
                verticalTwoTargetPosition = verticalTwoTargetPosition + 150;
                verticalThreeTargetPosition = verticalThreeTargetPosition + 150;
            } else if (gamepad1.a) {
                verticalOneTargetPosition = verticalOneTargetPosition - 150;
                verticalTwoTargetPosition = verticalTwoTargetPosition - 150;
                verticalThreeTargetPosition = verticalThreeTargetPosition - 150;
            }else{
                vertical1.setVelocity(0);
                vertical2.setVelocity(0);
                vertical3.setVelocity(0);
            }
            vertical1.setTargetPosition(verticalOneTargetPosition);
            vertical2.setTargetPosition(verticalTwoTargetPosition);
            vertical3.setTargetPosition(verticalThreeTargetPosition);

            // Turn On RUN_TO_POSITION
            vertical1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            vertical2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            vertical3.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
//            runtime.reset();
            vertical1.setVelocity(2900);
            vertical2.setVelocity(2900);
            vertical3.setVelocity(2900);

            //            while(vertical2.isBusy()){
//                telemetry.addData("Running to", " %7d :%7d", verticalTwoTargetPosition, verticalTwoTargetPosition);
//                telemetry.addData("Currently at", " at %7d :%7d",
//                        vertical2.getCurrentPosition(), vertical2.getCurrentPosition());
//                telemetry.update();
//            }
////            vertical1.setVelocity(Math.abs(2000));
//            vertical2.setVelocity(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.


            // Stop all motion;
//            vertical1.setPower(0);
//            vertical2.setPower(0);

            // Turn off RUN_TO_POSITION
//            vertical2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//        telemetry.addData("Path", "Complete");
//        telemetry.addData("position", );
//        telemetry.update();
//            sleep(1000);  // pause to display final telemetry message.
        }
    }

    /*
     *  Method to perform a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the OpMode running.
     */

    public void initMotors() {
        initVertical1();
        initVertical2();
        initVertical3();
    }

    public void initVertical1() {
        vertical1 = hardwareMap.get(DcMotorEx.class, "vertical1");
        vertical1.setVelocityPIDFCoefficients(verticalOnekP, verticalOnekI, verticalOnekD, verticalOneF);
        vertical1.setDirection(DcMotor.Direction.FORWARD);
        vertical1.setPower(verticalOneZeroPower);
        vertical1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vertical1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertical1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void initVertical2() {
        vertical2 = hardwareMap.get(DcMotorEx.class, "vertical2");
        vertical2.setVelocityPIDFCoefficients(verticalTwokP, verticalTwokI, verticalTwokD, verticalTwoF);
        vertical2.setDirection(DcMotor.Direction.REVERSE);
        vertical2.setPower(verticalTwoZeroPower);
        vertical2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vertical2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void initVertical3() {
        vertical3 = hardwareMap.get(DcMotorEx.class, "vertical3");
        vertical3.setVelocityPIDFCoefficients(verticalThreekP, verticalThreekI, verticalThreekD, verticalThreeF);
        vertical3.setDirection(DcMotor.Direction.REVERSE);
        vertical3.setPower(verticalOneZeroPower);
        vertical3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vertical3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertical3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}

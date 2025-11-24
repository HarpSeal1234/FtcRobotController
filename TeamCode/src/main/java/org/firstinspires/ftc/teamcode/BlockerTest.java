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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="blockerTest", group="Linear OpMode")
public class BlockerTest extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Servo blockerR;
    private Servo blockerL;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        blockerL = hardwareMap.get(Servo.class, "blockerL");
        blockerR = hardwareMap.get(Servo.class, "blockerR");

        double blockerPositionL = 0.5;
        double blockerPositionR = 0.5;

        blockerR.setPosition(Range.clip(blockerPositionR, 0.0, 1.0));
        blockerL.setPosition(Range.clip(blockerPositionL, 0.0, 1.0));

        float step = 0.001f;


        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

             if (gamepad2.y) {
                blockerPositionR += step;
                blockerR.setPosition(Range.clip(blockerPositionR, 0.0, 0.65));
            } else if (gamepad2.a){
                blockerPositionR -= step;
                blockerR.setPosition(Range.clip(blockerPositionR, 0.0, 0.65));
            } else if (gamepad2.x) {
                blockerPositionL += step;
                blockerL.setPosition(Range.clip(blockerPositionL, 0.0, 0.65));
            } else if (gamepad2.b){
                blockerPositionL -= step;
                blockerL.setPosition(Range.clip(blockerPositionL, 0.0, 0.65));
            }

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("blockerPosRIGHT", "Position: " + blockerPositionR);
            telemetry.addData("blockerPosLEFT", "Position: " + blockerPositionL);
            telemetry.update();
        }
    }
}

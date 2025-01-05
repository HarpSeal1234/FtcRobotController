package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


//vertical 2 2700 max velocity
@TeleOp(name="Max Velocity Test")
public class MaxVelocityTest extends LinearOpMode {
//Motor Variables
    private DcMotorEx motorOne;
    private double motorOneCurrentVelocity = 0.0;
    private double motorOneMaxVelocity = 0.0;

//PID Variables


    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();
        while (!isStarted()) {
            motorTelemetry();
        }
        waitForStart();
        while(opModeIsActive()) {
            runMotorOne();
            motorTelemetry();
        }
    }

    public void initHardware() {
        initMotorOne();
    }
//initialize motorOne
    public void initMotorOne() {
        motorOne = hardwareMap.get(DcMotorEx.class, "vertical2");
        motorOne.setDirection(DcMotor.Direction.FORWARD);
        motorOne.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motorOne.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void runMotorOne() {
        motorOne.setPower(1.0);
        motorOneCurrentVelocity = motorOne.getVelocity();
        if (motorOneCurrentVelocity > motorOneMaxVelocity) {
            motorOneMaxVelocity = motorOneCurrentVelocity;
        }
    }

    public void motorTelemetry() {
        telemetry.log().clear();
        telemetry.addData("Power", motorOne.getPower());
        telemetry.addData("Max Velocity", motorOneMaxVelocity);
        telemetry.addData("Current Velocity", motorOneCurrentVelocity);
        telemetry.update();
    }
}

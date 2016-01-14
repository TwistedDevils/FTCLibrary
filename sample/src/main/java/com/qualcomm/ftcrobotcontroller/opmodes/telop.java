package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Teleop extends OpMode {

    DcMotor ltrack, rtrack, lslide, rslide, lpivot, wench;
    Servo arm, rightzip, leftzip;
    Controller one, two;

    public void init() {
        gamepad1.setJoystickDeadzone(.1F);
        gamepad2.setJoystickDeadzone(.1F);
        //setup motors
        ltrack = hardwareMap.dcMotor.get("ltrack");
        rtrack = hardwareMap.dcMotor.get("rtrack");
        lslide = hardwareMap.dcMotor.get("lslide");
        rslide = hardwareMap.dcMotor.get("rslide");
        lpivot = hardwareMap.dcMotor.get("lpivot");
        wench = hardwareMap.dcMotor.get("wench");

        //setup servos
        arm = hardwareMap.servo.get("arm");
        rightzip = hardwareMap.servo.get("rightzip");
        leftzip = hardwareMap.servo.get("leftzip");

        one = new Controller(gamepad1);
        two = new Controller(gamepad2);

        arm.setPosition(1);
        rightzip.setPosition(1);
        leftzip.setPosition(0);
    }

    double lzippos = 0;
    double rzippos = 1;


    public void loop() {
        one.update(gamepad1);
        two.update(gamepad2);

        //manage the drive tracks
        Tank.motor2(ltrack, rtrack, -one.left_stick_y, one.right_stick_y);

        //manage lslide
        lslide.setPower(two.left_stick_y);

        //manage rslide
        rslide.setPower(two.right_stick_y);

        //manage lpivot
        if(gamepad2.a)
            lpivot.setPower(.3);
        else if(gamepad2.b)
            lpivot.setPower(-.6);
        else
            lpivot.setPower(0);

        //manage wench
        if(gamepad1.a)
            wench.setPower(1);
        else if(gamepad1.b)
            wench.setPower(-1);
        else
            wench.setPower(0);

        //manage arm position
        if(gamepad2.dpad_down) {
            arm.setPosition(1);
            telemetry.addData("arm:", "1");
        }
        else if (gamepad2.dpad_up) {
            arm.setPosition(0);
            telemetry.addData("arm:", "0");
        }
        //manage leftzip position
        if(gamepad2.left_bumper && lzippos < .9) {
            lzippos += .1;
            leftzip.setPosition(lzippos);
            telemetry.addData("leftzip:", lzippos);
        }
        else if(gamepad2.left_trigger>.5 && lzippos > .1) {
            lzippos -= .1;
            leftzip.setPosition(lzippos);
            telemetry.addData("leftzip:", lzippos);
        }

        //manage rightzip position
        if(gamepad2.right_bumper && rzippos > .1) {
            rzippos -= .1;
            rightzip.setPosition(rzippos);
            telemetry.addData("rightzip:", rzippos);
        }
        else if(gamepad2.right_trigger>.5 && rzippos < .9) {
            rzippos += .1;
            rightzip.setPosition(rzippos);
            telemetry.addData("rightzip:", rzippos);
        }
    }

    public void stop() {

    }
}

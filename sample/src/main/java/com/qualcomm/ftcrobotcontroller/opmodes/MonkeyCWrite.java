package com.qualcomm.ftcrobotcontroller.opmodes;


import android.os.Environment;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.monkeyc.MonkeyC;
import com.lasarobotics.library.options.Category;
import com.lasarobotics.library.options.OptionMenu;
import com.lasarobotics.library.options.SingleSelectCategory;
import com.lasarobotics.library.options.TextCategory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.File;

/**
 * MonkeyC2 Write Test
 */
public class MonkeyCWrite extends OpMode {
    //basic FTC classes
    DcMotor ltrack, rtrack, lslide, rslide, lpivot, wench;
    Servo arm, rightzip, leftzip;
    Controller one, two;

    MonkeyC writer;

    //setup options menu
    private OptionMenu menu;
    private String filename = "";  //initialize string
    @Override
    public void init() {

        OptionMenu.Builder builder = new OptionMenu.Builder(hardwareMap.appContext);
        //Setup a SingleSelectCategory
        SingleSelectCategory alliance = new SingleSelectCategory("Alliance");

        alliance.addOption("Red-");
        alliance.addOption("Blue-");
        builder.addCategory(alliance);
        //Setup a TextCategory
        TextCategory robotName = new TextCategory("Auto name");
        builder.addCategory(robotName);
        //Create menu
        menu = builder.create();
        //Display menu
        menu.show();

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

    @Override
    public void start() {
        MonkeyCDo.isTested = false;
        writer = new MonkeyC();

        for (Category c : menu.getCategories())
            filename += menu.selectedOption(c.getName());
        filename += ".json";
    }

    @Override
    public void loop() {
        //update gamepads to controllers with events
        one.update(gamepad1);
        two.update(gamepad2);
        writer.add(one, two);

        if (one.x == ButtonState.PRESSED) {
            writer.pauseTime();
            MonkeyCDo.test();
            writer.waitForController(one, two);
        }

        if (MonkeyCDo.isTested) {
            telemetry.addData("X KEY", "PRESSED!");
        } else {
            telemetry.addData("X KEY", "Not pressed");
        }

        telemetry.addData("Status", writer.getCommandsWritten() + " commands written");
        telemetry.addData("Time", writer.getTime() + " seconds");
        telemetry.addData("Location", filename);
        //Drive commands go here (must match when playing back)
        Tank.motor2(ltrack, rtrack, -one.left_stick_y, one.right_stick_y);

        if(gamepad1.dpad_down) {
            arm.setPosition(1);
            telemetry.addData("arm:", "1");
        }
        else if (gamepad1.dpad_up) {
            arm.setPosition(0);
            telemetry.addData("arm:", "0");
        }
    }

    @Override
    public void stop() {
        writer.write(filename, true);
        telemetry.addData("Location", filename);
    }

}

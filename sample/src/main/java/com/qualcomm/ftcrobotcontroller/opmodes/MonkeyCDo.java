package com.qualcomm.ftcrobotcontroller.opmodes;

import android.os.Environment;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.monkeyc.MonkeyData;
import com.lasarobotics.library.monkeyc.MonkeyDo;
import com.lasarobotics.library.options.Category;
import com.lasarobotics.library.options.NumberCategory;
import com.lasarobotics.library.options.OptionMenu;
import com.lasarobotics.library.options.SingleSelectCategory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.File;

/**
 * MonkeyC2 Do Test
 */
public class MonkeyCDo extends OpMode {
    public static boolean isTested = false;
    //basic FTC classes
    DcMotor ltrack, rtrack, lslide, rslide, lpivot, wench;
    Servo arm, rightzip, leftzip;
    Controller one, two;
    MonkeyDo reader;

    private OptionMenu menu;
    
    public static void test() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isTested = true;
    }

    @Override
    public void init() {
        OptionMenu.Builder builder = new OptionMenu.Builder(hardwareMap.appContext);
        //Setup a SingleSelectCategory
        SingleSelectCategory opmode = new SingleSelectCategory("opmode");

        File tmp = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MonkeyC/");
        File file[] = tmp.listFiles();


        for (File f : file)
        {
            if (f.isFile() && f.getPath().endsWith(".json")) {
                opmode.addOption(f.getName());
            }
        }
        builder.addCategory(opmode);
        //Setup a NumberCategory
        NumberCategory time = new NumberCategory("time");
        builder.addCategory(time);
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

        //frontLeft.setDirection(DcMotor.Direction.REVERSE);
        //frontRight.setDirection(DcMotor.Direction.REVERSE);

        //for (Category c : menu.getCategories())
        //    telemetry.addData(c.getName(), menu.selectedOption(c.getName()));

    }

    @Override
    public void start() {
        try {
            //telemetry.addData("Sleep:", menu.selectedOption("time"));
            int sleep = Integer.parseInt(menu.selectedOption("time"));
            Thread.sleep(sleep * 1000);
            telemetry.addData("Sleep:", "Done");
        } catch (Exception e) {
            telemetry.addData("Error:", e.toString());
        }
        reader = new MonkeyDo(menu.selectedOption("opmode"));
        isTested = false;
        reader.onStart();
    }

    @Override
    public void loop() {
        MonkeyData m = reader.getNextCommand();
        if (m.hasUpdate()) {
            m = reader.getNextCommand();
            one = m.updateControllerOne(one);
            two = m.updateControllerTwo(two);

            if (one.x == ButtonState.PRESSED) {
                reader.pauseTime();
                test();
                reader.resumeTime();
            }

            if (isTested) {
                telemetry.addData("X KEY", "PRESSED!");
            } else {
                telemetry.addData("X KEY", "Not pressed");
            }

            telemetry.addData("Status", "Replaying commands for file " + reader.getFilename());

            //Drive commands go here
            Tank.motor2(ltrack, rtrack, -one.left_stick_y, one.right_stick_y);

            if(gamepad1.dpad_down) {
                arm.setPosition(1);
                telemetry.addData("arm:", "1");
            }
            else if (gamepad1.dpad_up) {
                arm.setPosition(0);
                telemetry.addData("arm:", "0");
            }
        } else {
            telemetry.addData("Status", "Done replaying!");
            //We can choose to stop the timer here, but why...
        }
        telemetry.addData("Commands", reader.getCommandsRead() + " read");
        telemetry.addData("Time", reader.getTime() + " seconds");
    }

    @Override
    public void stop() {
    }
}

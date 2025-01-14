package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//@Config
@TeleOp(name = "TeleOpServo", group = "opMode")
public class TeleopServo extends LinearOpMode {

    // Primero declaramos todas las variables que vamos a usar
    // ( Motores, servos y temporizadores)

    DcMotor leftFront;
    DcMotor rightFront;

    DcMotor leftBack;
    DcMotor rightBack;

    DcMotor arm;

    Servo leftClaw;
    Servo rightClaw;
    Servo wrist;

    public static double leftClawClosed = 0.50;
    public static double leftClawOpen = 0.35;

    public static double rightClawClosed = 0.15;
    public static double rightClawOpen = 0.3;

    public static double wristFront = 0;
    public static double wristLeft = 0.25;
    public static double wristBack = 0.5;

    public static double linearSlidePowerMultiplier = 0.6;

    boolean open = false;

    ElapsedTime aButton = new ElapsedTime();
    ElapsedTime aButton2 = new ElapsedTime();
    ElapsedTime bButton = new ElapsedTime();
    ElapsedTime xButton = new ElapsedTime();
    ElapsedTime yButton = new ElapsedTime();
    ElapsedTime yButton2 = new ElapsedTime();

    ElapsedTime armDelay = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {

        // Luego las asignamos a su respectivo pedazo de hardware
        leftFront = hardwareMap.dcMotor.get("fl");
        rightFront = hardwareMap.dcMotor.get("fr");
        leftBack = hardwareMap.dcMotor.get("bl");
        rightBack = hardwareMap.dcMotor.get("br");

        arm = hardwareMap.dcMotor.get("arm");

        leftClaw = hardwareMap.servo.get("lcl");
        rightClaw = hardwareMap.servo.get("rcl");
        wrist = hardwareMap.servo.get("wrist");


        // Invertimos los motores de frabrica
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);

        arm.setDirection(DcMotorSimple.Direction.REVERSE);

        // Hacemos esto para que por defecto, cuando alguien deje de mover el stick de motor, se frenen todos los motores y no se quede patinando
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reiniciamos los temporizadores
        aButton.reset();
        aButton2.reset();
        xButton.reset();
        yButton.reset();
        bButton.reset();
        armDelay.reset();

        double invert = 1;
        double adjust = 10;
        double k_p = 0.003;

        waitForStart();
        while (opModeIsActive()) {

            // Invert puede tener dos valores: 1 o -1. Al mutiplicar invert por eso, invertimos el poder que se le debe de asignar al motor
            // Adjust es un numero que se dividira entre 10, para generar un numero decimal (e.g. 0.5) entonces al multiplicar todo el valor por este, se reducira a la mitad el poder de las llantas

            rightFront.setPower((-gamepad1.left_stick_y/1.45 - gamepad1.left_stick_x/1.45 - (gamepad1.right_stick_x/1.45 * -invert)) * (adjust / 10.0));
            leftFront.setPower((-gamepad1.left_stick_y/1.45 + gamepad1.left_stick_x/1.45 + (gamepad1.right_stick_x/1.45 * -invert)) * (adjust / 10.0));
            rightBack.setPower((-gamepad1.left_stick_y/1.45 + gamepad1.left_stick_x/1.45 - (gamepad1.right_stick_x/1.45 * -invert)) * (adjust / 10.0));
            leftBack.setPower((-gamepad1.left_stick_y/1.45 - gamepad1.left_stick_x/1.45 + (gamepad1.right_stick_x/1.45 * -invert)) * (adjust / 10.0));

            // Slow Mode
            if (gamepad1.a && aButton.milliseconds() > 500) {
                if (adjust == 10) {
                    adjust = 4;
                } else {
                    adjust = 10;
                }
                aButton.reset();
            }

            // Invert Mode
            if (gamepad1.y && yButton.milliseconds() > 500) {
                if (invert == 1) {
                    leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
                    rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
                    leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
                    rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
                    invert = -1;
                } else {
                    leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
                    rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
                    leftBack.setDirection(DcMotorSimple.Direction.FORWARD);
                    rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
                    invert = 1;
                }
                yButton.reset();
            }

            // Jugador 2

            // Esto de los miliseconds, es un truco porque como el codigo se actauliza muy rapido, el darle un click a la x, haria que se corriera este pedazo de codigo como unas 15 veces. Haciendo un delay de 300 milisegundos evita esto
            if (gamepad2.x && xButton.milliseconds() > 500) {
                if (open) {
                    leftClaw.setPosition(leftClawClosed);
                    rightClaw.setPosition(rightClawClosed);
                    open = false;
                } else {
                    leftClaw.setPosition(leftClawOpen);
                    rightClaw.setPosition(rightClawOpen);
                    open = true;
                }
                xButton.reset();
            }

            //Movimiento de la muñeca

            if(gamepad2.dpad_left){
                wrist.setPosition(0.0);
                aButton2.reset();
            }
            if(gamepad2.dpad_up){
                wrist.setPosition(0.3);
                bButton.reset();
            }
            if(gamepad2.dpad_right){
                wrist.setPosition(0.675);
                yButton2.reset();
            }



            // Brazo
            if (gamepad2.left_trigger > 0) /* && armDelay.milliseconds() > 20) */{
                arm.setPower(-linearSlidePowerMultiplier);
            }
            else if (gamepad2.right_trigger > 0)/* && armDelay.milliseconds() > 20)*/ {
                arm.setPower(linearSlidePowerMultiplier);
            }
            else {
                arm.setPower(0);
            }


            telemetry.addData("Invert", invert);
            telemetry.addData("Left Claw Position", leftClaw.getPosition());
            telemetry.addData("Right Claw Position", rightClaw.getPosition());
            telemetry.addData("Wrist Position", wrist.getPosition());
            telemetry.addData("Arm position", arm.getCurrentPosition());
            telemetry.addData("Slow Mode", adjust == 4);
            telemetry.update();
        }
    }
}

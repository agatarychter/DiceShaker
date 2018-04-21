package com.example.agatarychter.diceshaker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView dice;
    private Random random;
    private static final int DICE_NUMBER = 6;
    private static final float LIGHT_TENT = 50;
    private float xAcc, yAcc, zAcc;
    private float xPrevious, yPrevious, zPrevious;
    private boolean firstUpdate = true;
    private boolean shakeInitiated = false;
    private float shakeThreshold = 1.00f;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private SensorManager sensorManager;
    private boolean dayLight;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSensors();
        initViews();
        random = new Random();
        dayLight = true;
        initXYZ();
    }

    private void initViews() {
        dice = findViewById(R.id.dice_1);
        relativeLayout = findViewById(R.id.relative_layout);
    }

    private void initSensors(){
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initXYZ(){
        xAcc = SensorManager.GRAVITY_EARTH;
        yAcc = SensorManager.GRAVITY_EARTH;
        zAcc = SensorManager.GRAVITY_EARTH;
    }

    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void executeShake() {
        final Animation anim = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake);
        final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                rollDice();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
        anim.setAnimationListener(animationListener);
        dice.startAnimation(anim);
    }

    private void rollDice(){
        int randomNumber = random.nextInt(DICE_NUMBER)+1;
        if(dayLight){
            if(randomNumber==1)
                dice.setImageResource(R.drawable.ic_white_1);
            else if(randomNumber==2)
                dice.setImageResource(R.drawable.ic_white_2);
            else if(randomNumber==3)
                dice.setImageResource(R.drawable.ic_white_3);
            else if(randomNumber==4)
                dice.setImageResource(R.drawable.ic_white_4);
            else if(randomNumber==5)
                dice.setImageResource(R.drawable.ic_white_5);
            else
                dice.setImageResource(R.drawable.ic_white_6);
        }
        else {
            if (randomNumber == 1)
                dice.setImageResource(R.drawable.ic_black_1);
            else if (randomNumber == 2)
                dice.setImageResource(R.drawable.ic_black_2);
            else if (randomNumber == 3)
                dice.setImageResource(R.drawable.ic_black_3);
            else if (randomNumber == 4)
                dice.setImageResource(R.drawable.ic_black_4);
            else if (randomNumber == 5)
                dice.setImageResource(R.drawable.ic_black_5);
            else
                dice.setImageResource(R.drawable.ic_black_6);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
            boolean previousDayLight = dayLight;
            float lightFloat = sensorEvent.values[0];
            dayLight = !(lightFloat < LIGHT_TENT);
            if(previousDayLight!=dayLight) {
                setColors(dayLight);
            }
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            updateAccelParams(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            if (!shakeInitiated && isAccelerationChanged()) {
                shakeInitiated = true;
            } else if (shakeInitiated && isAccelerationChanged()) {
                executeShake();
            } else if (shakeInitiated && !isAccelerationChanged()) {
                shakeInitiated = false;
            }
        }
    }

    private boolean isAccelerationChanged() {
        float deltaX = Math.abs(xPrevious-xAcc);
        float deltaY = Math.abs(yPrevious - yAcc);
        float deltaZ = Math.abs(zPrevious - zAcc);

        return (deltaX> shakeThreshold && deltaY> shakeThreshold) || (deltaX> shakeThreshold && deltaY> shakeThreshold)
                || (deltaY> shakeThreshold && deltaZ> shakeThreshold);
    }

    private void updateAccelParams(float xNew, float yNew, float zNew) {
        if(firstUpdate){
            xPrevious = xNew;
            yPrevious = yNew;
            zPrevious = zNew;
            firstUpdate = false;
        }
        else{
            xPrevious = xAcc;
            yPrevious = yAcc;
            zPrevious = zAcc;
        }
        xAcc = xNew;
        yAcc = yNew;
        zAcc = zNew;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void setColors(boolean dayLight){
        if (dayLight) {
            dice.setImageResource(R.drawable.ic_white_1);
            relativeLayout.setBackgroundColor(getColor(R.color.white));
        } else {
            relativeLayout.setBackgroundColor(getColor(R.color.black));
            dice.setImageResource(R.drawable.ic_black_1);
        }
    }
}
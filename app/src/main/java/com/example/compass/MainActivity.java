package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView txtValue;

    private ImageView imageView;

    //For compass change style using button

    private static Button btnChangeCompass;
    private int current_image;
    int[] images = {R.drawable.compass1, R.drawable.compass4, R.drawable.compass6, R.drawable.compass7, R.drawable.compass8, R.drawable.compass10, R.drawable.compass2};

    //End compass change style using button

    //ConstraintLayout mLayout; //For background animation

    private SensorManager sensorManager;
    private Sensor accelerometerSensor, magnetometerSensor;

    private float[]  lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    boolean isLastAccelerometerArrayCopied = false;
    boolean isLastMagnetometerArrayCopied = false;

    long lastUpdatedTime = 0;
    float currentDegree = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Compass change style using button
        buttonClick();
        //End Compass change style using button


        //Background animation
//        mLayout = findViewById(R.id.mlayout);
//        AnimationDrawable animationDrawable = (AnimationDrawable) mLayout.getBackground();
//        animationDrawable.setEnterFadeDuration(7000);
//        animationDrawable.setExitFadeDuration(11000);
//        animationDrawable.start();
        //End Background animation

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        txtValue = findViewById(R.id.txtValue);
        imageView = findViewById(R.id.imageView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Back Button
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

             public void onBackPressed() {
                super.onBackPressed();
//                 overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
    //End Back Button

    //For Compass change style using button
    public void buttonClick(){
        imageView = (ImageView) findViewById(R.id.imageView);
        btnChangeCompass=(Button) findViewById(R.id.btnChanceCompass);
        btnChangeCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_image++;
                current_image=current_image % images.length;
                imageView.setImageResource(images[current_image]);
            }
        });
    }
    //End Compass change style using button



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor == accelerometerSensor)
        {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            isLastAccelerometerArrayCopied=true;
        }
        else if(sensorEvent.sensor == magnetometerSensor)
        {
            System.arraycopy(sensorEvent.values , 0 , lastMagnetometer, 0, sensorEvent.values.length);
            isLastMagnetometerArrayCopied = true;
        }

        if(isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied && System.currentTimeMillis() - lastUpdatedTime > 250)
        {
            SensorManager.getRotationMatrix(rotationMatrix, null , lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadians);

            RotateAnimation rotateAnimation = new RotateAnimation(currentDegree, -azimuthInDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);

            currentDegree = -azimuthInDegree;
            lastUpdatedTime = System.currentTimeMillis();


            int x = (int) azimuthInDegree;
            txtValue.setText(x + "°");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, magnetometerSensor);
    }

}
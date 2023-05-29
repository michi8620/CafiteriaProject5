package com.example.cafiteriaproject5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {
    private static final int SHAKE_THRESHOLD = 1000; // Adjust this threshold as per your requirement
    private static final int SHAKE_TIMEOUT = 500; // Adjust this timeout as per your requirement

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private OnShakeListener listener;
    private long lastShakeTime;

    public interface OnShakeListener {
        void onShake();
    }

    public ShakeDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.listener = listener;
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = x * x + y * y + z * z;

            long currentTime = System.currentTimeMillis();
            if (acceleration > SHAKE_THRESHOLD) {
                if (currentTime - lastShakeTime > SHAKE_TIMEOUT) {
                    lastShakeTime = currentTime;
                    if (listener != null) {
                        listener.onShake();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}

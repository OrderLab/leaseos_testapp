package com.example.yigonghu.leaseos_testapp.sensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service implements SensorEventListener {

	private static final String TAG = "SensorService";
	
	private static SensorManager sm;

	private int minRequestTime = 5; //in seconds
	private float minRequestDisplacement = 0; //in meters
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		System.out.println("Sensor: On Create called!");


        sm = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);

        assert sm != null;
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

		System.out.println("sensor requested");
		// Open the database for use
		//locationTable = new LocationTable(this);
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy - why are we being destroyed???");
		//sm.unregisterListener(this);
		System.out.println("sensor updates removed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		// We want this service to restart as soon as possible
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "Someone tried to bind to out location poller");
		// We do not allow anyone to bind to this, returning null
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy){}

	@Override
	public void onSensorChanged(SensorEvent event) {
	}

}
package com.example.yigonghu.leaseos_testapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import org.w3c.dom.ProcessingInstruction;

public class LongHoldingService extends Service {
    private final static int MINUTE_PER_MILLISECOND = 60*1000;
    private final static String TAG = "LongHoldingService";
    private final static long HOLDING_RATE = 1 * MINUTE_PER_MILLISECOND;
    public final static String ACTION_PREFIX = "edu.jhu.order.leaseos_testapp.action";
    private final static String WAKELOCK_HOLDING_STATS = ACTION_PREFIX + ".HOLDING_STATS";
    private final static int MSG_HOLDING_STATS = 1;
    private long mHoldingTime = 0;
    private PowerManager.WakeLock mWakelock;

    @Override
    public void onCreate() {
        Log.d(TAG, "Starting service...");
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(WAKELOCK_HOLDING_STATS);
        registerReceiver(mActionReceiver, ifilter);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LeaseOS_testapp");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d(TAG, "The intent is not null");
            String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
            mHoldingTime = Integer.parseInt(message) * MINUTE_PER_MILLISECOND;
        } else {
            Log.d(TAG, "The intent is null");
            mHoldingTime = 1 * MINUTE_PER_MILLISECOND;
        }

        schedulAlarm();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stoping service...");
        unregisterReceiver(mActionReceiver);
        cancelAlarm();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable mHoldingWakelock = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "The holding time is " + mHoldingTime + "ms");
            if (mHoldingTime == 0) {
                mWakelock.acquire();
            } else {
                mWakelock.acquire(mHoldingTime);
            }
        }
    };

    private void schedulAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0,
                new Intent(WAKELOCK_HOLDING_STATS), 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                HOLDING_RATE + mHoldingTime, operation);
    }

    private void cancelAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0,
                new Intent(WAKELOCK_HOLDING_STATS), 0);
        am.cancel(operation);
    }

    private final BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            if (actionStr.equalsIgnoreCase(WAKELOCK_HOLDING_STATS)) {
                mHandler.sendEmptyMessage(MSG_HOLDING_STATS);
            }
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HOLDING_STATS:
                    Thread t = new Thread(mHoldingWakelock);
                    t.start();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };
}

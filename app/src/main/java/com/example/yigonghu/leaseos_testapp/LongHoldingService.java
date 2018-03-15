/*
 *  @author Yigong Hu <hyigong1@jhu.edu>
 *
 *  The LeaseOS Project
 *
 *  Copyright (c) 2018, Johns Hopkins University - Order Lab.
 *      All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.yigonghu.leaseos_testapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class LongHoldingService extends Service {
    private final static String TAG = "LongHoldingService";
    public final static String ACTION_PREFIX = "edu.jhu.order.leaseos_testapp.action";
    public final static String WAKELOCK_HOLDING_STATS = ACTION_PREFIX + ".HOLDING_STATS";
    public final static String PARAMETER_CHANGE = ACTION_PREFIX + ".PARAMETER_CHANGE";
    public final static String EXTRA_MESSAGE = ACTION_PREFIX + ".EXTRA_MESSAGE";
    private final static int MSG_HOLDING_STATS = 1;
    private long mHoldTime = 0;
    private long mWaitTime = 0;
    private PowerManager.WakeLock mWakelock;

    @Override
    public void onCreate() {
        Log.d(TAG, "Starting service...");

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LeaseOS_testapp");

        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(WAKELOCK_HOLDING_STATS);
        ifilter.addAction(PARAMETER_CHANGE);
        registerReceiver(mActionReceiver, ifilter);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d(TAG, "The intent is not null");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String holdTime = sharedPref.getString("rate_limit_window", "1");
            mHoldTime = (long) (Float.parseFloat(holdTime) * TimeUtils.MILLIS_PER_MINUTE);
            String waitTime = sharedPref.getString("wait_window", "1");
            mWaitTime = (long) (Float.parseFloat(waitTime) * TimeUtils.MILLIS_PER_MINUTE);
            schedulAlarm();
        }
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
            Log.d(TAG, "The holding time is " + mHoldTime / 1000 + " seconds and the wait time is " + mWaitTime / 1000 + " seconds");
            if (mHoldTime < 0) {
                mWakelock.acquire();
            } else {
                mWakelock.acquire(mHoldTime);
            }
        }
    };

    private void schedulAlarm() {
        Log.d(TAG, "The holding time is " + mHoldTime / 1000 + " seconds and the wait time is " + mWaitTime / 1000 + " seconds");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0,
                new Intent(WAKELOCK_HOLDING_STATS),0 );
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                mHoldTime+mWaitTime, operation);
    }

    private void cancelAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0,
                new Intent(WAKELOCK_HOLDING_STATS), 0);
        am.cancel(operation);
    }


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

    private final BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            if(actionStr.equalsIgnoreCase(WAKELOCK_HOLDING_STATS)) {
                mHandler.sendEmptyMessage(MSG_HOLDING_STATS);
            }else if (actionStr.equalsIgnoreCase(PARAMETER_CHANGE)) {
                int actionType = intent.getIntExtra(EXTRA_MESSAGE, WakelockFragment.NO_CHANGE);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                switch (actionType) {
                    case WakelockFragment.HOLD_TIME_CHANGE:
                        String holdTime = sharedPref.getString("rate_limit_window", "1");
                        mHoldTime = (long) (Float.parseFloat(holdTime) * TimeUtils.MILLIS_PER_MINUTE);
                        Log.d(TAG, "The holding time changes to " + mHoldTime / 1000 + " seconds");
                        break;
                    case WakelockFragment.WAIT_TIME_CHANGE:
                        String waitTime = sharedPref.getString("wait_window", "1");
                        mWaitTime = (long) (Float.parseFloat(waitTime) * TimeUtils.MILLIS_PER_MINUTE);
                        Log.d(TAG, "The wait time changes to " + mWaitTime / 1000 + " seconds");
                        break;
                    default:
                        Log.d(TAG, "Nothing changed");
                }
            }
        }
    };

}

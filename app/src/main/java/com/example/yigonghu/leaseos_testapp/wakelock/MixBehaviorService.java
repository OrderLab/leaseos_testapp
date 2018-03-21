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

package com.example.yigonghu.leaseos_testapp.wakelock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.yigonghu.leaseos_testapp.TimeUtils;

/**
 * Created by yigonghu on 3/20/18.
 */

public class MixBehaviorService extends Service {
    private final static String TAG = "MixBehaviorService";
    public final static String ACTION_PREFIX = "leaseos_testapp.wakelock.mixbehavior.action";
    public final static String WAKELOCK_HOLDING_STATS = ACTION_PREFIX + ".HOLDING_STATS";
    public final static String PARAMETER_CHANGE = ACTION_PREFIX + ".PARAMETER_CHANGE";
    public final static String BEHAVIOR_CHANGE = ACTION_PREFIX + ".BEHAVIOR_CHANGE";
    public final static String EXTRA_MESSAGE = ACTION_PREFIX + ".EXTRA_MESSAGE";
    public final static int LONG_HOLD = 1;
    public final static int NORMAL = 4;

    private PowerManager.WakeLock mWakelock;
    private HandlerThread mHandlerThread;
    private long mHoldTime = 0;
    private long mWaitTime = 0;
    private int count = 0;

    private boolean mStartNormal;
    private boolean mStartLongHold;
    private boolean mStartLowUtility;
    private boolean mStartHighDamage;

    private int mLongHoldNumber;
    private int mLowUtilityNumber;
    private int mHighDamageNumber;
    private int mNormalNumber;

    private Runnable mNormalBehavior = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Start Normal behavior. The holding time is " + mHoldTime / 1000 + " seconds and the wait time is " + mWaitTime / 1000 + " seconds");
            count++;
            if (mHoldTime < 0) {
                mWakelock.acquire();
            } else {
                mWakelock.acquire(mHoldTime);
            }

            if (count < mNormalNumber) {
                scheduleNormal(false, true);
            } else {
                count = 0;
                scheduleNextTurn(NORMAL);
            }

            long base = System.currentTimeMillis();
            long now = base;
            while (now - base <= mHoldTime/2) {
                int x1 = 1;
                int x2 = 1;
                int x3 = x1 + x2;
                for (int i = 0; i < 1000; i++) {
                    x1 = x2;
                    x2 = x3;
                    x3 = x1+x2;
                }
            }
        }
    };

    private Runnable mLongHoldBehavior = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Start Long Holding behavior. The holding time is " + mHoldTime / 1000 + " seconds and the wait time is " + mWaitTime / 1000 + " seconds");
            count++;
            if (mHoldTime < 0) {
                mWakelock.acquire();
            } else {
                mWakelock.acquire(mHoldTime);
            }

            if (count < mLongHoldNumber) {
                //  Log.d(TAG,"1");
                scheduleLongHold(false);
            } else {
                count = 0;
                scheduleNextTurn(LONG_HOLD);
            }
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "Starting mixed behavior service...");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LeaseOS_testapp_mixbehavior");
        mWakelock.setReferenceCounted(false);

        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(BEHAVIOR_CHANGE);
        ifilter.addAction(PARAMETER_CHANGE);
        registerReceiver(mActionReceiver, ifilter);

        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();

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
            mStartNormal = false;
            mStartLongHold = false;
            mStartLowUtility = false;
            mStartHighDamage = false;
            String normalNumber = sharedPref.getString("normal_number", "0");
            mNormalNumber = Integer.parseInt(normalNumber);
            String longHoldNumber = sharedPref.getString("long_hold_number", "0");
            mLongHoldNumber = Integer.parseInt(longHoldNumber);
            String lowUtilityNumber = sharedPref.getString("low_utility_number", "0");
            mLowUtilityNumber = Integer.parseInt(lowUtilityNumber);
            String highDamageNumber = sharedPref.getString("high_damage_number", "0");
            mHighDamageNumber = Integer.parseInt(highDamageNumber);

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stoping mix behavior service...");
        unregisterReceiver(mActionReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void scheduleMixBehavior(boolean immediate) {
        Log.d(TAG, "The Long Hold is " + mStartLongHold + ", and the Normal is " + mStartNormal);
        if (mStartLongHold) {
            scheduleLongHold(immediate);
        }
        if (mStartLongHold && mStartNormal) {
            scheduleNormal(immediate, false);
        } else if (!mStartLongHold && mStartNormal) {
            scheduleNormal(immediate, true);
        }
        count = 0;
    }

    public void scheduleLongHold(boolean immediate) {
        if (immediate) {
            // Log.d(TAG, "Immediate delay for long hold");
            mHandler.postDelayed(mLongHoldBehavior, 0);
        } else {
            //  Log.d(TAG, "Short delay for long hold, Delay for " + (mHoldTime + mWaitTime)/1000 + " seconds");
            mHandler.postDelayed(mLongHoldBehavior, mHoldTime + mWaitTime);
        }
    }

    public void scheduleNormal(boolean immediate, boolean noDelay) {
        if (!noDelay && immediate) {
            // Log.d(TAG, "Long delay for normal. Delay for " + (mHoldTime + mWaitTime)*mLongHoldNumber/1000 + " seconds");
            mHandler.postDelayed(mNormalBehavior, (mHoldTime + mWaitTime) * mLongHoldNumber);
            return;
        }

        if (!noDelay && !immediate) {
            // Log.d(TAG, "Long delay for normal not immediate. Delay for " + (mHoldTime + mWaitTime)*(mLongHoldNumber+1)/1000 + " seconds");
            mHandler.postDelayed(mNormalBehavior, (mHoldTime + mWaitTime) * (mLongHoldNumber + 1));
            return;
        }

        if (noDelay && immediate) {
            //  Log.d(TAG, "Immediate delay for normal");
            mHandler.postDelayed(mNormalBehavior, 0);
            return;
        }

        if (noDelay && !immediate) {
            // Log.d(TAG, "Short delay for normal, Delay for " + (mHoldTime + mWaitTime)/1000 + " seconds");
            mHandler.postDelayed(mNormalBehavior, mHoldTime + mWaitTime);
            return;
        }
    }

    public void scheduleNextTurn(int type) {
        switch (type) {
            case LONG_HOLD:
                if (mStartLongHold && !mStartNormal) {
                    scheduleMixBehavior(false);
                }
                break;
            case NORMAL:
                if (mStartNormal) {
                    scheduleMixBehavior(false);
                }
                break;
        }
    }

    public void cancelMixBehavior() {
        if (mHandler != null) {
            Log.d(TAG, "Cancelling mix behavior");
            mHandler.removeCallbacks(mLongHoldBehavior);
            mHandler.removeCallbacks(mNormalBehavior);
        }
    }

    private final BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            if (actionStr.equalsIgnoreCase(PARAMETER_CHANGE)) {
                int actionType = intent.getIntExtra(EXTRA_MESSAGE, WakelockFragment.NO_CHANGE);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                switch (actionType) {
                    case WakelockFragment.HOLD_TIME_CHANGE:
                        String holdTime = sharedPref.getString("rate_limit_window", "1");
                        mHoldTime = (long) (Float.parseFloat(holdTime) * TimeUtils.MILLIS_PER_MINUTE);
                        Log.d(TAG, "The holding time changes to " + mHoldTime / 1000 + " seconds");
                        cancelMixBehavior();
                        scheduleMixBehavior(true);
                        break;
                    case WakelockFragment.WAIT_TIME_CHANGE:
                        String waitTime = sharedPref.getString("wait_window", "1");
                        mWaitTime = (long) (Float.parseFloat(waitTime) * TimeUtils.MILLIS_PER_MINUTE);
                        Log.d(TAG, "The wait time changes to " + mWaitTime / 1000 + " seconds");
                        cancelMixBehavior();
                        scheduleMixBehavior(true);
                        break;
                    case WakelockFragment.LONGHOLD_NUMBER_CHANGE:
                        String longHoldNumber = sharedPref.getString("long_hold_number", "0");
                        mLongHoldNumber = Integer.parseInt(longHoldNumber);
                        Log.d(TAG, "The long hold number changes to " + mLongHoldNumber);
                        cancelMixBehavior();
                        scheduleMixBehavior(true);
                        break;
                    case WakelockFragment.NORMAL_NUMBER_CHANGE:
                        String normalNumber = sharedPref.getString("normal_number", "0");
                        mNormalNumber = Integer.parseInt(normalNumber);
                        Log.d(TAG, "The normal number changes to " + mNormalNumber);
                        cancelMixBehavior();
                        scheduleMixBehavior(true);
                        break;
                    default:
                        Log.d(TAG, "Nothing changed");
                }
            } else if (actionStr.equalsIgnoreCase(BEHAVIOR_CHANGE)) {
                int actionType = intent.getIntExtra(EXTRA_MESSAGE, WakelockFragment.NO_CHANGE);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                switch (actionType) {
                    case WakelockFragment.LONGHOLD_CHANGE:
                        mStartLongHold = sharedPref.getBoolean("start_long_hold", false);
                        cancelMixBehavior();
                        scheduleMixBehavior(true);
                        break;
                    case WakelockFragment.NORMAL_CHANGE:
                        mStartNormal = sharedPref.getBoolean("start_normal", false);
                        cancelMixBehavior();
                        scheduleMixBehavior(true);
                        break;
                }
            }
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    Log.d(TAG, "Unknown message");
            }
        }
    };

}

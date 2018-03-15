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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by yigonghu on 3/13/18.
 */

public class WakelockFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String EXTRA_MESSAGE = "com.example.yigonghu.leaseos_testapp.RECEIVER";
    private static final String TAG = "WakelockFragment";
    private boolean behaviorEnabled;
    private boolean startLongHold;
    private Intent intent = new Intent(LongHoldingService.PARAMETER_CHANGE);

    public static final int NO_CHANGE = -1;
    public static final int HOLD_TIME_CHANGE = 0;
    public static final int WAIT_TIME_CHANGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.wakelock_behavior);

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("test_mode")) {
            boolean value = sharedPreferences.getBoolean("test_mode", false);
            Log.d(TAG, "The enable is changed " + value);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("test_mode", value);
            editor.commit();
        } else if (key.equals("behavior_type_window")) {
            String value = sharedPreferences.getString("behavior_type_window", "Normal");
            Log.d(TAG, "The behavior type is changed " + value);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("behavior_type_window", value);
            editor.commit();
        } else if (key.equals("rate_limit_window")) {
            String value = sharedPreferences.getString("rate_limit_window", "1");
            Log.d(TAG, "The hold time is changed " + value);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("rate_limit_window", value);
            editor.commit();
            noteHoldTimeChange();
        } else if (key.equals("wait_window")) {
            String value = sharedPreferences.getString("wait_window", "1");
            long window = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            Log.d(TAG, "The wait time type is changed " + value);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("wait_window", value);
            editor.commit();
            noteWaitTimeChange();
        } else if (key.equals("start_long_hold")) {
            boolean value = sharedPreferences.getBoolean("start_long_hold", false);
            Log.d(TAG, "The long hold behavior is enable " + value);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("start_long_hold", value);
            editor.commit();
            ManageBehavior();
        }
    }

    private void noteHoldTimeChange() {
        if (startLongHold) {
            Log.d(TAG, "Note the hold time change");
            intent.putExtra(LongHoldingService.EXTRA_MESSAGE, HOLD_TIME_CHANGE);
            getActivity().sendBroadcast(intent);
        }
    }

    private void noteWaitTimeChange() {
        if (startLongHold) {
            intent.putExtra(LongHoldingService.EXTRA_MESSAGE, WAIT_TIME_CHANGE);
            getActivity().sendBroadcast(intent);
        }
    }


    private void ManageBehavior() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        behaviorEnabled  = sharedPref.getBoolean("test_mode", false);
        Log.d(TAG, "The enable is " + behaviorEnabled);
        if (behaviorEnabled) {
            String value = sharedPref.getString("behavior_type_window", "Normal");
            switch (value) {
                case "Long Holding":
                    startLongHold = sharedPref.getBoolean("start_long_hold", false);
                    if (startLongHold) {
                        Intent intent = new Intent(getActivity(), LongHoldingService.class);
                        getActivity().startService(intent);
                        break;
                    } else {
                        Intent intent = new Intent(getActivity(), LongHoldingService.class);
                        getActivity().stopService(intent);
                    }
                default:
                    Log.d(TAG, "Unknown type of behavior");

            }

        }
    }
}

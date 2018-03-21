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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.yigonghu.leaseos_testapp.BehaviorType;
import com.example.yigonghu.leaseos_testapp.R;
import com.example.yigonghu.leaseos_testapp.TimeUtils;


/**
 * Created by yigonghu on 3/13/18.
 */

public class WakelockFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String EXTRA_MESSAGE = "com.example.yigonghu.leaseos_testapp.RECEIVER";
    private static final String TAG = "WakelockFragment";
    private boolean behaviorEnabled;
    private boolean mixbehaviorEnabled;
    private boolean startLongHold;
    private Intent mIntentLongHold = new Intent(LongHoldingService.PARAMETER_CHANGE);

    public static final int NO_CHANGE = -1;

    public static final int HOLD_TIME_CHANGE = 0;
    public static final int WAIT_TIME_CHANGE = 1;
    public static final int LONGHOLD_NUMBER_CHANGE = 2;
    public static final int NORMAL_NUMBER_CHANGE = 3;

    public static final int LONGHOLD_CHANGE = 1;
    public static final int NORMAL_CHANGE = 4;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.wakelock_behavior);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        behaviorEnabled = false;
        mixbehaviorEnabled = false;
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
            behaviorEnabled = sharedPreferences.getBoolean("test_mode", false);
            Log.d(TAG, "The behavior enable is changed " + behaviorEnabled);
        } else if (key.equals("mix_behavior")) {
            mixbehaviorEnabled = sharedPreferences.getBoolean("mix_behavior", false);
            Log.d(TAG, "The mix enable is changed " + mixbehaviorEnabled);
            noteMixBehaviorEnable();
        } else if (key.equals("rate_limit_window")) {
            String value = sharedPreferences.getString("rate_limit_window", "1");
            Log.d(TAG, "The hold time is changed " + value);
            noteHoldTimeChanged();
        } else if (key.equals("wait_window")) {
            String value = sharedPreferences.getString("wait_window", "1");
            long window = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            Log.d(TAG, "The wait time type is changed " + value);
            noteWaitTimeChanged();
        } else if (key.equals("start_long_hold")) {
            boolean value = sharedPreferences.getBoolean("start_long_hold", false);
            Log.d(TAG, "The long hold behavior is enable " + value);
            BehaviorManager(BehaviorType.LongHolding);
        } else if (key.equals("start_normal")) {
            boolean value = sharedPreferences.getBoolean("start_normal", false);
            Log.d(TAG, "The normal behavior is enable " + value);
            BehaviorManager(BehaviorType.Normal);
        } else if (key.equals("long_hold_number")) {
            String value = sharedPreferences.getString("long_hold_number", "0");
            Log.d(TAG, "The long hold number is changed to " + value);
            noteLongHoldNumberChanged();
        } else if (key.equals("normal_number")) {
            String value = sharedPreferences.getString("normal_number", "0");
            Log.d(TAG, "The normal number is changed to " + value);
            noteNormalNumberChanged();
        }
    }

    private void noteLongHoldNumberChanged() {
        if (mixbehaviorEnabled) {
            Intent intent = new Intent(MixBehaviorService.PARAMETER_CHANGE);
            intent.putExtra(MixBehaviorService.EXTRA_MESSAGE, LONGHOLD_NUMBER_CHANGE);
            getActivity().sendBroadcast(intent);
        }
    }

    private void noteNormalNumberChanged() {
        if (mixbehaviorEnabled) {
            Intent intent = new Intent(MixBehaviorService.PARAMETER_CHANGE);
            intent.putExtra(MixBehaviorService.EXTRA_MESSAGE, NORMAL_NUMBER_CHANGE);
            getActivity().sendBroadcast(intent);
        }
    }

    private void noteHoldTimeChanged() {
        Log.d(TAG, "Note the hold time change");
        if (behaviorEnabled && !mixbehaviorEnabled) {
            mIntentLongHold.putExtra(LongHoldingService.EXTRA_MESSAGE, HOLD_TIME_CHANGE);
            getActivity().sendBroadcast(mIntentLongHold);
        } else if (behaviorEnabled && mixbehaviorEnabled) {
            Intent intent = new Intent(MixBehaviorService.PARAMETER_CHANGE);
            intent.putExtra(MixBehaviorService.EXTRA_MESSAGE, HOLD_TIME_CHANGE);
            getActivity().sendBroadcast(intent);
        }
    }

    private void noteWaitTimeChanged() {
        if (behaviorEnabled && !mixbehaviorEnabled) {
            mIntentLongHold.putExtra(LongHoldingService.EXTRA_MESSAGE, WAIT_TIME_CHANGE);
            getActivity().sendBroadcast(mIntentLongHold);
        } else if (mixbehaviorEnabled) {
            Intent intent = new Intent(MixBehaviorService.PARAMETER_CHANGE);
            intent.putExtra(MixBehaviorService.EXTRA_MESSAGE, WAIT_TIME_CHANGE);
            getActivity().sendBroadcast(intent);
        }
    }

    private void noteMixBehaviorEnable() {
        if (mixbehaviorEnabled) {
            Intent intent = new Intent(getActivity(), MixBehaviorService.class);
            getActivity().startService(intent);
        } else {
            Intent intent = new Intent(getActivity(), LongHoldingService.class);
            getActivity().stopService(intent);
        }
    }

    private void noteMixBehaviorChange(BehaviorType type) {
        Intent intent = new Intent(MixBehaviorService.BEHAVIOR_CHANGE);
        switch (type) {
            case FrequencyAsking:
                break;
            case LongHolding:
                intent.putExtra(MixBehaviorService.EXTRA_MESSAGE, LONGHOLD_CHANGE);
                getActivity().sendBroadcast(intent);
                break;
            case LowUtility:
                break;
            case HighDamage:
                break;
            case Normal:
                intent.putExtra(MixBehaviorService.EXTRA_MESSAGE, NORMAL_CHANGE);
                getActivity().sendBroadcast(intent);
                break;
        }
    }

    private void BehaviorManager(BehaviorType type) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        behaviorEnabled = sharedPref.getBoolean("test_mode", false);
        mixbehaviorEnabled = sharedPref.getBoolean("mix_behavior", false);
        Log.d(TAG, "The enable is " + behaviorEnabled);
        if (behaviorEnabled) {
            if (!mixbehaviorEnabled) {
                switch (type) {
                    case FrequencyAsking:
                    case LongHolding:
                        startLongHold = sharedPref.getBoolean("start_long_hold", false);
                        if (startLongHold) {
                            Intent intent = new Intent(getActivity(), LongHoldingService.class);
                            getActivity().startService(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), LongHoldingService.class);
                            getActivity().stopService(intent);
                        }
                        break;
                    case LowUtility:
                        break;
                    case HighDamage:
                        break;
                    case Normal:
                }
            } else {
                switch (type) {
                    case FrequencyAsking:
                        break;
                    case LongHolding:
                        noteMixBehaviorChange(BehaviorType.LongHolding);
                        break;
                    case LowUtility:
                        break;
                    case HighDamage:
                        break;
                    case Normal:
                        noteMixBehaviorChange(BehaviorType.Normal);
                }

            }
        }

    }
}


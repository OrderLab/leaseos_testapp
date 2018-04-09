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

package com.example.yigonghu.leaseos_testapp.location;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.yigonghu.leaseos_testapp.BehaviorType;
import com.example.yigonghu.leaseos_testapp.R;
import com.example.yigonghu.leaseos_testapp.TimeUtils;
import com.example.yigonghu.leaseos_testapp.wakelock.LongHoldingService;


/**
 * Created by suyiliu on 4/9/18.
 */

public class LocationFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String EXTRA_MESSAGE = "com.example.yigonghu.leaseos_testapp.RECEIVER";
    private static final String TAG = "LocationFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("here2");
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), LocationPollerService.class);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        //getPreferenceScreen().getSharedPreferences()
        //        .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        System.out.println("hereeee stopping location service.");
        super.onPause();
        Intent intent = new Intent(getActivity(), LocationPollerService.class);
        getActivity().startService(intent);
        //getPreferenceScreen().getSharedPreferences()
        //        .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

}


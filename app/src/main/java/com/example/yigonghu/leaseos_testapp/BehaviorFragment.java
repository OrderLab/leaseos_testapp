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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yigonghu.leaseos_testapp.location.LocationBehaviorActivity;
import com.example.yigonghu.leaseos_testapp.wakelock.WakelockBehaviorActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yigonghu on 3/12/18.
 */

public class BehaviorFragment extends Fragment {
    private static final String TAG = "BehaviorFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Start create behavior fragment");
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        super.onCreate(savedInstanceState);

        List<ListBehaviorSetting> newLists = new ArrayList<ListBehaviorSetting>();

        newLists.add(new ListBehaviorSetting("Wakelock Behavior","Start wakelock Disruptive Behavior"));
        newLists.add(new ListBehaviorSetting("Location Behavior","Start location Disruptive Behavior"));
        newLists.add(new ListBehaviorSetting("Sensor Behavior","Start sensor Damagege Behavior"));

        ArrayAdapter<ListBehaviorSetting> adapter = new ListViewBehaviorSettingAdapter(getActivity().getApplicationContext(), R.layout.listview_behavior_layout, newLists);
        final ListView list = (ListView) view.findViewById(R.id.item_listViewTwo);
        list.setAdapter(adapter);
        list.setClickable(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                switch (position) {
                    case 0:
                        System.out.println("hereeeeeeeeee00");
                        Intent intent = new Intent(getActivity(), WakelockBehaviorActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        System.out.println("hereeee0");
                        Intent intentLocation = new Intent(getActivity(), LocationBehaviorActivity.class);
                        startActivity(intentLocation);
                    case 2:
                }

            }
        });
        return view;
    }

}

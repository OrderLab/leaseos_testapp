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

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class SensorFragment extends Fragment {
    private static final String TAG = "SensorFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Start create Sensor fragment");
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        super.onCreate(savedInstanceState);
        List<ListItem> newLists = new ArrayList<ListItem>();

        newLists.add(new ListItem("Sensor Behavior","Frequent Asking Disruptive Behavior"));
        newLists.add(new ListItem("Sensor Behavior","Long Holding Disruptive Behavior"));
        newLists.add(new ListItem("Sensor Behavior","Low Utility Disruptive Behavior"));
        newLists.add(new ListItem("Sensor Behavior","High System Damagege Behavior"));

        ArrayAdapter<ListItem> adapter = new ListViewAdapter(getActivity().getApplicationContext(), R.layout.listview_item_layout, newLists);
        final ListView list = (ListView) view.findViewById(R.id.item_listViewTwo);
        list.setAdapter(adapter);

        list.setClickable(true);
        return view;
    }
}

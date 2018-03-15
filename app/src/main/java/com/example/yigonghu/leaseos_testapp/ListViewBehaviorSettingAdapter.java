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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 */
public class ListViewBehaviorSettingAdapter extends ArrayAdapter<ListBehaviorSetting> {
    int resource;

    public ListViewBehaviorSettingAdapter(Context ctx, int res, List<ListBehaviorSetting> items)
    {
        super(ctx, res, items);
        resource = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        ListBehaviorSetting currentList = getItem(position);

        if (convertView == null) {
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, itemView, true);
        } else {
            itemView = (LinearLayout) convertView;
        }


        TextView title = (TextView) itemView.findViewById(R.id.title);
        title.setText(currentList.getTitle());

        TextView Behavior = (TextView) itemView.findViewById(R.id.Behavior);
        Behavior.setText(currentList.getBehavior());

        ImageView jo = (ImageView) itemView.findViewById(R.id.button3);
        Picasso.with(getContext())
                .load(R.drawable.disruptive)
                .resize(100, 100)
                .centerCrop()
                .into(jo);

        return itemView;
    }
}

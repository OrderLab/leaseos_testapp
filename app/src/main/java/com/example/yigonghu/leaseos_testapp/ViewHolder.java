package com.example.yigonghu.leaseos_testapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by yigonghu on 3/12/18.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView textView;
    public ViewHolder(View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
        this.textView = (TextView) itemView.findViewById(R.id.textView);
    }
}

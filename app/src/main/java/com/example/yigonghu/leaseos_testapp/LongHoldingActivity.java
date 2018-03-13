package com.example.yigonghu.leaseos_testapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by yigonghu on 3/13/18.
 */

public class LongHoldingActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private TextView mTextMessage;
    private TextView mStatusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wakelock_behavior);
        mStatusView = (TextView) findViewById(R.id.holding_time);
        mStatusView.setText("Wait for command...");
        mTextMessage = (TextView) findViewById(R.id.message);
    }

    public void sendHoldingTime(View view) {
        Intent intent = new Intent(this, LongHoldingService.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        mStatusView.setText("Holding time is " + Integer.parseInt(message) + " min");
        startService(intent);
    }
}

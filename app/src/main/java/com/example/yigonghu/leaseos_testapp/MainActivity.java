package com.example.yigonghu.leaseos_testapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private TextView mTextMessage;
    private TextView mStatusView = null;
    private TextView mRecyclerView;
    private TextView mLayoutManager;
    private TextView mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IconData[] data = new IconData[] {
                new IconData("Delete", android.R.drawable.ic_delete),
                new IconData("Alert", android.R.drawable.ic_dialog_alert)
        };

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        IconAdapter adapter = new IconAdapter(data);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



    // mStatusView = (TextView) findViewById(R.id.holding_time);
       // mStatusView.setText("Wait for command...");
       // mTextMessage = (TextView) findViewById(R.id.message);
    }

    /** Called when the user taps the Send button */
    public void sendHoldingTime(View view) {
        Intent intent = new Intent(this, LongHoldingService.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        mStatusView.setText("Holding time is " + Integer.parseInt(message) + " min");
        startService(intent);
    }


}

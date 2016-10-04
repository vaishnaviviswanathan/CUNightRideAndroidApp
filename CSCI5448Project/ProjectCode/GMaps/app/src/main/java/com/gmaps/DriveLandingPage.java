package com.gmaps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DriveLandingPage extends AppCompatActivity {
    Context context;
    public void loadMaps(View view) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(this, ViewMaps.class);
        //finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_landing_page);
    }
}

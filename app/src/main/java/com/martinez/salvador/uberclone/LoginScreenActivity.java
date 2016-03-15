package com.martinez.salvador.uberclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;
import com.firebase.client.Firebase;

public class LoginScreenActivity extends AppCompatActivity {

    Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        mSwitch = (Switch) findViewById(R.id.PassDriver);
        Firebase.setAndroidContext(this);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View v = super.onCreateView(parent, name, context, attrs);
        return v;
    }

    public void onClick(View v) {
        if (mSwitch.isChecked()) {
            //Driver
            Intent intent = new Intent(this, SelectRiderActivity.class);
            startActivity(intent);
        }
        else {
            //Passenger
            Intent intent = new Intent(this, RiderActivity.class);
            startActivity(intent);
        }
    }
}

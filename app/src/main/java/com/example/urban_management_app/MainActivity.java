package com.example.urban_management_app;

import com.google.firebase.FirebaseApp;
import android.app.Application;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            FirebaseApp.initializeApp(this);
        }
    }
}

package com.example.urban_management_app;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

//TODO: expand on this if I have time
public class EducationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = "https://www.dublincity.ie/residential/environment/waste-and-recycling";
        webView.loadUrl(url);
    }
}

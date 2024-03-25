package com.example.urban_management_app;

// necessary imports
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

//TODO: expand on this in future versions
public class EducationActivity extends AppCompatActivity {

    // simple screen that loads DCC Waste Management website
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

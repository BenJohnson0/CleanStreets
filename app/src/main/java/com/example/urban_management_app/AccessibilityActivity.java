package com.example.urban_management_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AccessibilityActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FONT_SIZE_KEY = "fontSize";

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        settings = getSharedPreferences(PREFS_NAME, 0);

        SeekBar fontSizeSeekBar = findViewById(R.id.font_size_seek_bar);
        final TextView exampleTextView = findViewById(R.id.example_text_view);

        int savedFontSize = settings.getInt(FONT_SIZE_KEY, 16);
        fontSizeSeekBar.setProgress(savedFontSize);
        exampleTextView.setTextSize(savedFontSize);

        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                exampleTextView.setTextSize(progress);
                saveFontSize(progress);
                applyAppWideFontSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void saveFontSize(int fontSize) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(FONT_SIZE_KEY, fontSize);
        editor.apply();
    }

    private void applyAppWideFontSize(int fontSize) {
        //todo: if needed
    }
}

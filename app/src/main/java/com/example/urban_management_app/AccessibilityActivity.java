package com.example.urban_management_app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AccessibilityActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FONT_SIZE_KEY = "fontSize";
    private static final String COLOR_INVERSION_KEY = "colorInversion";

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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final CheckBox colorInversionCheckBox = findViewById(R.id.color_inversion_checkbox);
        boolean savedColorInversion = settings.getBoolean(COLOR_INVERSION_KEY, false); // Default: false (no inversion)
        colorInversionCheckBox.setChecked(savedColorInversion);

        applyColorInversion(savedColorInversion);

        colorInversionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applyColorInversion(isChecked);
                saveColorInversion(isChecked);
            }
        });
    }

    private void saveFontSize(int fontSize) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(FONT_SIZE_KEY, fontSize);
        editor.apply();
    }

    private void saveColorInversion(boolean colorInversion) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(COLOR_INVERSION_KEY, colorInversion);
        editor.apply();
    }

    private void applyColorInversion(boolean isInverted) {
        if (isInverted) {
            ColorMatrix colorMatrixInverted = new ColorMatrix(new float[]{
                    -1, 0, 0, 0, 255,
                    0, -1, 0, 0, 255,
                    0, 0, -1, 0, 255,
                    0, 0, 0, 1, 0
            });

            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrixInverted);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            getWindow().getDecorView().getBackground().setColorFilter(colorFilter);
        } else {
            // Remove color inversion
            getWindow().getDecorView().getBackground().setColorFilter(null);
        }
    }
}

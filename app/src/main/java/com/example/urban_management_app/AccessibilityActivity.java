package com.example.urban_management_app;

// necessary imports
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AccessibilityActivity extends AppCompatActivity {

    // declare shared preferences variables
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FONT_SIZE_KEY = "fontSize";
    private Button button_demo;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        settings = getSharedPreferences(PREFS_NAME, 0);

        // initialize button_demo, fontSizeSeekBar, and exampleTextView
        button_demo = findViewById(R.id.button_demo);
        SeekBar fontSizeSeekBar = findViewById(R.id.font_size_seek_bar);
        final TextView exampleTextView = findViewById(R.id.example_text_view);

        // retrieve saved font size from shared preferences
        int savedFontSize = settings.getInt(FONT_SIZE_KEY, 16);
        fontSizeSeekBar.setProgress(savedFontSize);
        exampleTextView.setTextSize(savedFontSize);

        // set on click listener for button_demo to open app tutorial on YouTube
        button_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/VUXDetq8VEg")));
            }
        });

        // set on seek bar change listener to change example text view text size
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

        CheckBox colorSimpleCheckBox = findViewById(R.id.color_simple_checkbox);
        colorSimpleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleColorTheme(isChecked);
            }
        });
    }

    // todo: save font size to shared preferences
    private void saveFontSize(int fontSize) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(FONT_SIZE_KEY, fontSize);
        editor.apply();
    }

    private void applyAppWideFontSize(int fontSize) {
        //todo: accessibility feature to be implemented
    }

    private void toggleColorTheme(boolean isSimplified) {
        if (isSimplified) {
            // apply simplified color theme
            findViewById(R.id.font_size_seek_bar).setBackgroundColor(getResources().getColor(R.color.colorSecondarySimple));
            findViewById(R.id.example_text_view).setBackgroundColor(getResources().getColor(R.color.colorPrimarySimple));
            findViewById(R.id.fontSizeTextView).setBackgroundColor(getResources().getColor(R.color.colorPrimarySimple));
            findViewById(R.id.appColoursTextView).setBackgroundColor(getResources().getColor(R.color.colorPrimarySimple));
            findViewById(R.id.color_simple_checkbox).setBackgroundColor(getResources().getColor(R.color.colorPrimarySimple));
        } else {
            // apply regular color theme
            findViewById(R.id.font_size_seek_bar).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            findViewById(R.id.example_text_view).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            findViewById(R.id.fontSizeTextView).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            findViewById(R.id.appColoursTextView).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            findViewById(R.id.color_simple_checkbox).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}

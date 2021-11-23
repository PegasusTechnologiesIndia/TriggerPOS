package org.phomellolitepos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CrashActivity extends AppCompatActivity {
Button CrashApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        CrashApp=(Button)findViewById(R.id.crashapp);
        CrashApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException("Test Crash");

            }
        });
    }
}
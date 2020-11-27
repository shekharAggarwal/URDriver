package com.urdriver.urdriver;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class CrashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage()));
    }

}

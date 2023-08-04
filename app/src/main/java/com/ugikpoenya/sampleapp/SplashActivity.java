package com.ugikpoenya.sampleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ugikpoenya.master.AdsManager;
import com.ugikpoenya.master.ServerManager;
import com.ugikpoenya.master.ads.AdmobManager;
import com.ugikpoenya.master.ads.FacebookManager;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ServerManager serverManager = new ServerManager();
        serverManager.setBaseUrl("https://master.ugikpoenya.net/api/");
        serverManager.setApiKey("DA8BB129F7C1ED5BD07046961C995A77");

        new AdmobManager().addTestDeviceId("01E84CE45ACAE7D0A79D096213298925");
        new FacebookManager().addTestDeviceId("85546810-d31f-4d61-a1b3-6444f250ca8c");

        serverManager.getItemDelay(this, 3000, () -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return null;
        });

    }
}

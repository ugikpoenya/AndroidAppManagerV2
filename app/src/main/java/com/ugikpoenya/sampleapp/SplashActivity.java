package com.ugikpoenya.sampleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ugikpoenya.appmanager.ServerManager;
import com.ugikpoenya.appmanager.ads.AdmobManager;
import com.ugikpoenya.appmanager.ads.FacebookManager;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ServerManager serverManager = new ServerManager();
        serverManager.setBaseUrl(this, "https://firestore-cms.ugikpoenya.net/api/");
        serverManager.setApiKey(this, "DA8BB129F7C1ED5BD07046961C995A77");

        new AdmobManager().addTestDeviceId("408B66CD374D7D5E90132B65146233B2");
        new FacebookManager().addTestDeviceId("121ee13b-ad34-44f6-add2-300d2fe282cc");

        serverManager.getItemDelay(this, 3000, () -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return null;
        });

    }
}

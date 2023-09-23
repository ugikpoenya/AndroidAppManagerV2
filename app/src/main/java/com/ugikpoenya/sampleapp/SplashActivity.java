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
        serverManager.setBaseUrl(this, "https://master.ugikpoenya.net/api/");
        serverManager.setApiKey(this, "DA8BB129F7C1ED5BD07046961C995A77");

        new AdmobManager().addTestDeviceId("088FD3EB8C7E984A6EEA2CEFCA4E1190");
        new FacebookManager().addTestDeviceId("224cc677-fff2-416b-a5af-156b79bf7c45");

        serverManager.getItemDelay(this, 3000, () -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return null;
        });

    }
}

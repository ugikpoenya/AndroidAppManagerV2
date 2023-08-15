package com.ugikpoenya.sampleapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ugikpoenya.appmanager.AdsManager;
import com.ugikpoenya.appmanager.AppManager;
import com.ugikpoenya.appmanager.ServerManager;
import com.ugikpoenya.appmanager.ads.AdmobManager;
import com.ugikpoenya.sampleapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    AppManager appManager = new AppManager();
    AdsManager adsManager = new AdsManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ServerManager serverManager = new ServerManager();

        serverManager.getPosts(this, (response) -> null);
        serverManager.getAssetFiles(this, (response) -> null);
        serverManager.getAssetFiles(this, "Dian Piesesha", (response) -> null);
        serverManager.getAssetFolders(this, (response) -> null);
        serverManager.getAssetFolders(this, "Muchsin Alatas", (response) -> null);

        appManager.initPrivacyPolicy(this);
        appManager.initDialogRedirect(this);

        adsManager.initBanner(this, binding.lyBannerAds, 0, "home");
        adsManager.initNative(this, binding.lyNativeAds, 0, "home");
    }


    @Override
    public void onBackPressed() {
        new AppManager().exitApp(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AdmobManager().showOpenAdsAdmob(this);
    }


    public void showPrivacyPolicy(View view) {
        appManager.showPrivacyPolicy(this);
    }

    public void shareApp(View view) {
        appManager.shareApp(this, getString(R.string.app_name));
    }

    public void nextApp(View view) {
        appManager.nextApp(this);
    }

    public void rateApp(View view) {
        appManager.rateApp(this);
    }

    public void showInterstitial(View view) {
        adsManager.showInterstitial(this, 0);
    }

    public void resetGDPR(View view) {
        new AdmobManager().resetGDPR();
    }

    public void showRewardedAds(View view) {
        adsManager.showRewardedAds(this, 0, (response) -> {
            Log.d("LOG", "Rewarded ads result " + response.toString());
            return null;
        });
    }
}
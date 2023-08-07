package com.ugikpoenya.sampleapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ugikpoenya.appmanager.AdsManager;
import com.ugikpoenya.appmanager.AppManager;
import com.ugikpoenya.appmanager.Prefs;
import com.ugikpoenya.appmanager.ServerManager;
import com.ugikpoenya.appmanager.ads.AdmobManager;
import com.ugikpoenya.appmanager.model.PostModel;
import com.ugikpoenya.sampleapp.databinding.ActivityMainBinding;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    AppManager appManager = new AppManager();
    AdsManager adsManager = new AdsManager();

    Prefs prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ServerManager serverManager = new ServerManager();

        prefs = new Prefs(this);

        serverManager.getPosts(this, (response) -> {
            prefs.setPost_model_array_list(response);
            for (PostModel p : prefs.getPost_model_array_list()) {
                Log.d("LOG", p.getPost_title());
            }
            return null;
        });
        serverManager.getAssetFiles(this, (response) -> null);
        serverManager.getAssetFolders(this, (response) -> null);

        appManager.initPrivacyPolicy(this);
        appManager.initDialogRedirect(this);

        adsManager.initBanner(this, binding.lyBannerAds, 0, "home");
        adsManager.initNative(this, binding.lyNativeAds, 0, "home");
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
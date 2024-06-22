package com.ugikpoenya.appmanager.ads

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.Prefs
import com.ugikpoenya.appmanager.intervalCounter


private var interstitialAd: MaxInterstitialAd? = null

class AppLovin {

    fun initAppLovinAds(context: Context) {
        val ITEM_MODEL = Prefs(context).ITEM_MODEL
        if (ITEM_MODEL.applovin_native.isEmpty()
            && ITEM_MODEL.applovin_banner.isEmpty()
            && ITEM_MODEL.applovin_interstitial.isEmpty()
            && ITEM_MODEL.applovin_open_ads.isEmpty()
            && ITEM_MODEL.applovin_rewarded_ads.isEmpty()
            && ITEM_MODEL.applovin_sdk_key.isEmpty()
        ) {
            Log.d("LOG", "initAppLovinAds disable")
        } else {
            // Create the initialization configuration
            val initConfig = AppLovinSdkInitializationConfiguration.builder(ITEM_MODEL.applovin_sdk_key, context)
                .setMediationProvider(AppLovinMediationProvider.MAX)
                .build()

            // Initialize the SDK with the configuration
            AppLovinSdk.getInstance(context).initialize(initConfig) { sdkConfig ->
                Log.d("LOG", "initAppLovinAds successfully")
                initInterstitialAppLovin(context)
            }
        }
    }

    fun initAppLovinBanner(context: Context, VIEW: RelativeLayout, ORDER: Int = 0, PAGE: String = "") {
        val ITEM_MODEL = Prefs(context).ITEM_MODEL
        if (ITEM_MODEL.applovin_banner.isEmpty()) {
            Log.d("LOG", "AppLovin Banner ID Not Set")
            AdsManager().initBanner(context, VIEW, ORDER, PAGE)
        } else if (VIEW.childCount == 0) {
            Log.d("LOG", "AppLovin Banner Init")
            val adView = MaxAdView(ITEM_MODEL.applovin_banner, context)
            adView.setListener(object : MaxAdViewAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Banner onAdLoaded")
                }

                override fun onAdDisplayed(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Banner onAdDisplayed")
                }

                override fun onAdHidden(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Banner onAdHidden")
                }

                override fun onAdClicked(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Banner onAdClicked")
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.d("LOG", "AppLovin Banner onAdLoadFailed")
                    VIEW.removeAllViews()
                    AdsManager().initBanner(context, VIEW, ORDER, PAGE)
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    Log.d("LOG", "AppLovin Banner onAdDisplayFailed")
                }

                override fun onAdExpanded(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Banner onAdExpanded")
                }

                override fun onAdCollapsed(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Banner onAdCollapsed")
                }

            })

            val width = ViewGroup.LayoutParams.MATCH_PARENT
            // Banner height on phones and tablets is 50 and 90, respectively
            val heightPx = 90
            adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
            // Set background or background color for banners to be fully functional
            adView.setBackgroundColor(Color.BLACK)
            VIEW.addView(adView)
            adView.loadAd()
        }
    }

    fun initInterstitialAppLovin(context: Context) {
        val ITEM_MODEL = Prefs(context).ITEM_MODEL
        if (ITEM_MODEL.applovin_interstitial.isEmpty()) {
            Log.d("LOG", "AppLovin Interstitial ID set")
        } else {
            Log.d("LOG", "Init AppLovin Interstitial ")
            interstitialAd = MaxInterstitialAd(ITEM_MODEL.applovin_interstitial, context)
            interstitialAd?.setExtraParameter( "container_view_ads", "true" )
            interstitialAd?.setListener(object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Interstitial onAdLoaded")
                }

                override fun onAdDisplayed(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Interstitial onAdDisplayed")
                }

                override fun onAdHidden(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Interstitial onAdHidden")
                }

                override fun onAdClicked(p0: MaxAd) {
                    Log.d("LOG", "AppLovin Interstitial onAdClicked")
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.d("LOG", "AppLovin Interstitial onAdLoadFailed")
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    Log.d("LOG", "AppLovin Interstitial onAdDisplayFailed")
                }
            })

            // Load the first ad
            interstitialAd?.loadAd()
        }
    }

    fun showInterstitialAppLovin(context: Context, ORDER: Int = 0) {
        if (interstitialAd != null && interstitialAd!!.isReady) {
            interstitialAd?.showAd()

            intervalCounter = Prefs(context).ITEM_MODEL.interstitial_interval
            Log.d("LOG", "Interstitial admob Show")
        } else {
            Log.d("LOG", "Interstitial admob not loaded")
            AdsManager().showInterstitial(context, ORDER)
        }
    }

}
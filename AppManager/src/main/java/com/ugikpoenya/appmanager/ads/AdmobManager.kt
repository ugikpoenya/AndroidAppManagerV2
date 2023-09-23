package com.ugikpoenya.appmanager.ads

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.RelativeLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.Prefs
import com.ugikpoenya.appmanager.R
import com.ugikpoenya.appmanager.intervalCounter


var admobRewardedAd: RewardedAd? = null
var admobInterstitial: InterstitialAd? = null
private var appOpenAd: AppOpenAd? = null
private var isShowingOpenAd = false
var consentInformation: ConsentInformation? = null
var ADMOB_TEST_DEVICE_ID: ArrayList<String> = ArrayList()

class AdmobManager {
    fun addTestDeviceId(test_id: String) {
        ADMOB_TEST_DEVICE_ID.add(test_id)
    }

    fun initAdmobAds(context: Context) {
        Log.d("LOG", "Admob test device " + ADMOB_TEST_DEVICE_ID.size)

        if (Prefs(context).ITEM_MODEL.admob_banner.isEmpty()
            && Prefs(context).ITEM_MODEL.admob_interstitial.isEmpty()
            && Prefs(context).ITEM_MODEL.admob_native.isEmpty()
            && Prefs(context).ITEM_MODEL.admob_rewarded_ads.isEmpty()
            && Prefs(context).ITEM_MODEL.admob_open_ads.isEmpty()
        ) {
            Log.d("LOG", "initAdmobAds disable")
        } else {
            MobileAds.initialize(context) {
                Log.d("LOG", "initAdmobAds successfully")
                initInterstitialAdmob(context)
                initRewardedAdmob(context)
                initOpenAdsAdmob(context)
            }

        }
        isShowingOpenAd = false
    }

    fun initAdmobBanner(context: Context, VIEW: RelativeLayout, ORDER: Int = 0, PAGE: String = "") {
        if (Prefs(context).ITEM_MODEL.admob_banner.isEmpty()) {
            Log.d("LOG", "Admob Banner ID Not Set")
            AdsManager().initBanner(context, VIEW, ORDER, PAGE)
        } else if (VIEW.childCount == 0) {
            val outMetrics = Resources.getSystem().displayMetrics
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
            val adView = AdView(context)
            VIEW.addView(adView)
            adView.adUnitId = Prefs(context).ITEM_MODEL.admob_banner
            adView.setAdSize(adSize)
            Log.d("LOG", "Admob Banner Init")
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d("LOG", "Admob Banner loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("LOG", "Admob Banner failed to load")
                    VIEW.removeAllViews()
                    AdsManager().initBanner(context, VIEW, ORDER, PAGE)
                }
            }
        }
    }


    fun initAdmobNative(context: Context, VIEW: RelativeLayout, ORDER: Int = 0, PAGE: String = "") {
        if (Prefs(context).ITEM_MODEL.admob_native.isEmpty()) {
            Log.d("LOG", "Admob Native ID Not Set")
            AdsManager().initNative(context, VIEW, ORDER, PAGE)
        } else if (VIEW.childCount == 0) {
            Log.d("LOG", "Admob Native Init")
            val adLoader = AdLoader.Builder(context, Prefs(context).ITEM_MODEL.admob_native)
                .forNativeAd { nativeAd ->
                    val nativeType = when (PAGE) {
                        "home" -> Prefs(context).ITEM_MODEL.home_native_view
                        "detail" -> Prefs(context).ITEM_MODEL.detail_native_view
                        else -> PAGE
                    }

                    val nativeLayout = if (nativeType == "medium") {
                        R.layout.native_ads_layout_admob_medium
                    } else {
                        R.layout.native_ads_layout_admob_small
                    }
                    Log.d("LOG", "Admob native ads loaded")
                    val adView = (context as Activity).layoutInflater
                        .inflate(nativeLayout, null) as NativeAdView
                    populateAdmobNative(nativeAd, adView)
                    VIEW.addView(adView)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("LOG", "Failed to load Admob native " + adError.message)
                        VIEW.removeAllViews()
                        AdsManager().initNative(context, VIEW, ORDER, PAGE)
                    }
                })
                .build()
            val adRequest = AdRequest.Builder().build()
            adLoader.loadAd(adRequest)
        }
    }


    fun showInterstitialAdmob(context: Context, ORDER: Int = 0) {
        if (admobInterstitial != null) {
            admobInterstitial?.show(context as Activity)
            intervalCounter = Prefs(context).ITEM_MODEL.interstitial_interval
            Log.d("LOG", "Interstitial admob Show")
        } else {
            Log.d("LOG", "Interstitial admob not loaded")
            AdsManager().showInterstitial(context, ORDER)
        }
    }

    fun initInterstitialAdmob(context: Context) {
        if (Prefs(context).ITEM_MODEL.admob_interstitial.isEmpty()) {
            Log.d("LOG", "Admob Interstitial ID set")
        } else {
            Log.d("LOG", "Init Admob Interstitial ")
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                Prefs(context).ITEM_MODEL.admob_interstitial,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("LOG", "Interstitial admob failed to load")
                        admobInterstitial = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("LOG", "Interstitial admob loaded")
                        admobInterstitial = interstitialAd
                        admobInterstitial?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    Log.d("LOG", "Interstitial admob Ad was dismissed")
                                }

                                override fun onAdShowedFullScreenContent() {
                                    Log.d("LOG", "Interstitial admob Ad showed fullscreen content.")
                                    admobInterstitial = null
                                    initInterstitialAdmob(context)
                                }
                            }
                    }
                })
        }
    }


    fun showRewardedAdmob(context: Context, ORDER: Int = 0) {
        if (admobRewardedAd != null) {
            Log.d("LOG", "Rewarded admob Show")
            admobRewardedAd.let { ad ->
                ad?.show((context as Activity)) { rewardItem ->
                    Log.d("LOG", "RewardedAdmob User earned the reward.")
                }
            }
        } else {
            Log.d("LOG", "Rewarded admob not loaded")
            AdsManager().showRewardedAds(context, ORDER)
        }
    }

    fun initRewardedAdmob(context: Context) {
        if (Prefs(context).ITEM_MODEL.admob_rewarded_ads.isEmpty()) {
            Log.d("LOG", "Admob Rewarded ID not set")
        } else {
            Log.d("LOG", "Init Admob Rewarded ")
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(context, Prefs(context).ITEM_MODEL.admob_rewarded_ads, adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("LOG", "Admob Rewarded " + adError.message)
                    admobRewardedAd = null
                }

                override fun onAdLoaded(admobRewarded: RewardedAd) {
                    Log.d("LOG", "Admob Rewarded  was loaded.")
                    admobRewardedAd = admobRewarded
                    admobRewarded.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            Log.d("LOG", "Admob Rewarded was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            Log.d("LOG", "Admob Rewarded dismissed fullscreen content.")
                            admobRewardedAd = null
                            initRewardedAdmob(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            Log.e("LOG", "Admob Rewarded failed to show fullscreen content.")
                        }

                        override fun onAdImpression() {
                            Log.d("LOG", "Admob Rewarded recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d("LOG", "Ad showed fullscreen content.")
                        }
                    }
                }
            })
        }
    }

    // Init open ads
    fun initOpenAdsAdmob(context: Context) {
        val request = AdRequest.Builder().build()
        if (Prefs(context).ITEM_MODEL.admob_open_ads.isNotEmpty() && appOpenAd == null) {
            Log.d("LOG", "Admob Open Ads Init")
            AppOpenAd.load(
                context, Prefs(context).ITEM_MODEL.admob_open_ads, request,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Log.d("LOG", "Admob Open Ads Loaded")
                        appOpenAd = ad
                        showOpenAdsAdmob(context)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.d("LOG", "Admob Open Ads Filed " + loadAdError.message)
                    }
                }
            )
        }
    }

    fun showOpenAdsAdmob(context: Context) {
        if (appOpenAd == null) Log.d("LOG", "Admob Open Ads  null.")
        if (isShowingOpenAd) Log.d("LOG", "Admob Open Ads  Sudah tampil tadi, pisan ae.. ndak tuman.")

        if (appOpenAd !== null && !isShowingOpenAd) {
            Log.d("LOG", "Admob Open Ads  Will show ad.")
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        Log.d("LOG", "Admob Open Ads  Dismissed")
                        initOpenAdsAdmob(context)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                    override fun onAdShowedFullScreenContent() {
                        Log.d("LOG", "Admob Open Ads  Showed")
                        isShowingOpenAd = true
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd!!.show(context as Activity)
        }
    }

    fun resetGDPR() {
        consentInformation?.reset()
    }

    // GDPR Init
    fun initGdpr(context: Context, function: () -> (Unit)) {
        Log.d("LOG", "Init GDPR")
        val params = ConsentRequestParameters.Builder()


        if (ADMOB_TEST_DEVICE_ID.size > 0) {
            val debugSettings = ConsentDebugSettings.Builder(context)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            ADMOB_TEST_DEVICE_ID.forEach {
                debugSettings.addTestDeviceHashedId(it)
            }
            params.setConsentDebugSettings(debugSettings.build())
        }

        consentInformation = UserMessagingPlatform.getConsentInformation(context)
        consentInformation?.requestConsentInfoUpdate(context as Activity, params.build(), {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(context) { loadAndShowError: FormError? ->
                if (loadAndShowError != null) {
                    Log.d("LOG", String.format("%s: %s", loadAndShowError.errorCode, loadAndShowError.message))
                }

                if (consentInformation!!.canRequestAds()) {
                    Log.d("LOG", "GDPR canRequestAds")
                    initAdmobAds(context)
                }

                function()
            }
        }, { requestConsentError: FormError ->
            Log.d("LOG", String.format("%s: %s", requestConsentError.errorCode, requestConsentError.message))
        })
    }
}
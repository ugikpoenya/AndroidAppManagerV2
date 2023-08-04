package com.ugikpoenya.master.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.RelativeLayout
import com.ugikpoenya.master.AdsManager
import com.ugikpoenya.master.ITEM_MODEL
import com.ugikpoenya.master.R
import com.ugikpoenya.master.intervalCounter
import com.unity3d.ads.*
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

class UnityManager {
    fun initUnityAds(context: Context) {
        if (ITEM_MODEL.unity_game_id.isEmpty()) {
            Log.d("LOG", "initUnityAds disable")
        } else {
            if (ITEM_MODEL.unity_test_mode) {
                Log.d("LOG", "Init Unity Ads Test Mode ")
            } else {
                Log.d("LOG", "Init Unity Ads Production ")
            }

            UnityAds.initialize(
                context,
                ITEM_MODEL.unity_game_id,
                ITEM_MODEL.unity_test_mode,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        Log.d("LOG", "onUnityAdsReady onInitializationComplete")
                    }

                    override fun onInitializationFailed(
                        p0: UnityAds.UnityAdsInitializationError?,
                        p1: String?
                    ) {
                        Log.d("LOG", "onUnityAdsReady onInitializationFailed")
                    }
                })
        }
    }

    fun initUnityBanner(context: Context, VIEW: RelativeLayout, ORDER: Int = 0, PAGE: String = "") {
        if (ITEM_MODEL.unity_banner.isEmpty()) {
            Log.d("LOG", "Unity Banner ID Not Set")
            AdsManager().initBanner(context, VIEW, ORDER, PAGE)
        } else if (VIEW.childCount == 0) {
            Log.d("LOG", "Unity banner init")
            val bannerView =
                BannerView(context as Activity, ITEM_MODEL.unity_banner, UnityBannerSize.getDynamicSize(context))
            bannerView.listener = object : BannerView.IListener {
                override fun onBannerLeftApplication(p0: BannerView?) {
                    Log.d("LOG", "Unity onBannerLeftApplication ")
                }

                override fun onBannerClick(p0: BannerView?) {
                    Log.d("LOG", "Unity onBannerClick ")
                }

                override fun onBannerLoaded(p0: BannerView?) {
                    Log.d("LOG", "Unity onBannerLoaded ")
                    VIEW.addView(p0)
                }

                override fun onBannerFailedToLoad(p0: BannerView?, p1: BannerErrorInfo?) {
                    Log.d("LOG", "Unity onBannerFailedToLoad ")
                    VIEW.clearAnimation()
                    AdsManager().initBanner(context, VIEW, ORDER, PAGE)
                }
            }
            bannerView.load()
        }
    }


    fun showInterstitialUnity(context: Context, ORDER: Int = 0) {
        if (ITEM_MODEL.unity_interstitial.isEmpty()) {
            Log.d("LOG", "Unity Interstitial ID set")
        } else {
            Log.d("LOG", "Init Unity Ads Interstitial ")
            UnityAds.load(ITEM_MODEL.unity_interstitial, object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(p0: String?) {
                    Log.d("LOG", "Interstitial onUnityAdsAdLoaded $p0")
                    UnityAds.show(
                        context as Activity,
                        ITEM_MODEL.unity_interstitial,
                        UnityAdsShowOptions(),
                        object : IUnityAdsShowListener {
                            override fun onUnityAdsShowFailure(
                                p0: String?,
                                p1: UnityAds.UnityAdsShowError?,
                                p2: String?
                            ) {
                                Log.d("LOG", "Interstitial onUnityAdsShowFailure")
                                AdsManager().showInterstitial(context, ORDER)
                            }

                            override fun onUnityAdsShowStart(p0: String?) {
                                Log.d("LOG", "Interstitial onUnityAdsShowStart")
                            }

                            override fun onUnityAdsShowClick(p0: String?) {
                                Log.d("LOG", "Interstitial onUnityAdsShowClick")
                            }

                            override fun onUnityAdsShowComplete(
                                p0: String?,
                                p1: UnityAds.UnityAdsShowCompletionState?
                            ) {
                                Log.d("LOG", "Interstitial onUnityAdsShowComplete")
                            }
                        }
                    )
                }

                override fun onUnityAdsFailedToLoad(
                    p0: String?,
                    p1: UnityAds.UnityAdsLoadError?,
                    p2: String?
                ) {
                    Log.d("LOG", "Interstitial onUnityAdsFailedToLoad")
                }
            })
        }
    }

    fun showRewardedUnity(context: Context, ORDER: Int = 0, function: (response: Boolean?) -> (Unit)) {
        if (ITEM_MODEL.unity_rewarded_ads.isEmpty()) {
            Log.d("LOG", "Unity RewardedAds ID set")
            AdsManager().showRewardedAds(context, ORDER, function)
        } else {
            Log.d("LOG", "Init Unity Ads RewardedAds ")
            UnityAds.load(ITEM_MODEL.unity_rewarded_ads, object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(p0: String?) {
                    Log.d("LOG", "RewardedAds onUnityAdsAdLoaded $p0")
                    UnityAds.show(
                        context as Activity,
                        ITEM_MODEL.unity_rewarded_ads,
                        UnityAdsShowOptions(),
                        object : IUnityAdsShowListener {
                            override fun onUnityAdsShowFailure(
                                p0: String?,
                                p1: UnityAds.UnityAdsShowError?,
                                p2: String?
                            ) {
                                Log.d("LOG", "RewardedAds onUnityAdsShowFailure")
                                AdsManager().showRewardedAds(context, ORDER, function)
                            }

                            override fun onUnityAdsShowStart(p0: String?) {
                                Log.d("LOG", "RewardedAds onUnityAdsShowStart")
                            }

                            override fun onUnityAdsShowClick(p0: String?) {
                                Log.d("LOG", "RewardedAds onUnityAdsShowClick")
                            }

                            override fun onUnityAdsShowComplete(
                                p0: String,
                                p1: UnityAds.UnityAdsShowCompletionState
                            ) {
                                Log.d("LOG", "RewardedAds onUnityAdsShowComplete")
                                if (p1 == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                                    function(true)
                                } else {
                                    function(false)
                                }
                            }
                        }
                    )
                }

                override fun onUnityAdsFailedToLoad(
                    p0: String?,
                    p1: UnityAds.UnityAdsLoadError?,
                    p2: String?
                ) {
                    Log.d("LOG", "RewardedAds onUnityAdsFailedToLoad")
                }
            })
        }
    }
}
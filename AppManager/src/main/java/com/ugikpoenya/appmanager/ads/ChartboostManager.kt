package com.ugikpoenya.appmanager.ads

import android.content.Context
import android.util.Log
import android.widget.RelativeLayout
import com.chartboost.sdk.Chartboost
import com.chartboost.sdk.ads.Banner
import com.chartboost.sdk.ads.Interstitial
import com.chartboost.sdk.ads.Rewarded
import com.chartboost.sdk.callbacks.BannerCallback
import com.chartboost.sdk.callbacks.InterstitialCallback
import com.chartboost.sdk.callbacks.RewardedCallback
import com.chartboost.sdk.events.CacheError
import com.chartboost.sdk.events.CacheEvent
import com.chartboost.sdk.events.ClickError
import com.chartboost.sdk.events.ClickEvent
import com.chartboost.sdk.events.DismissEvent
import com.chartboost.sdk.events.ImpressionEvent
import com.chartboost.sdk.events.RewardEvent
import com.chartboost.sdk.events.ShowError
import com.chartboost.sdk.events.ShowEvent
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.Prefs

var chartboostInterstitial: Interstitial? = null
var chartboostRewarded : Rewarded? = null
class ChartboostManager {
    fun initChartboostAds(context: Context) {
        if (Prefs(context).ITEM_MODEL.chartboost_app_id.isEmpty()
            && Prefs(context).ITEM_MODEL.chartboost_app_signature.isEmpty()
        ) {
            Log.d("LOG", "ChartboostAds disable")
        } else {
            Chartboost.startWithAppId(context, Prefs(context).ITEM_MODEL.chartboost_app_id, Prefs(context).ITEM_MODEL.chartboost_app_signature) { startError ->
                if (startError == null) {
                    Log.d("LOG", "Chartboost SDK is initialized")
                    initInterstitialChartboost(context)
                    initRewardedChartboost()
                } else {
                    Log.d("LOG", "Chartboost SDK initialized with error: ${startError.code.name}")
                }
            }
        }
    }

    var chartboostBanner: Banner? = null
    fun initChartboostBanner(context: Context, VIEW: RelativeLayout, ORDER: Int = 0, PAGE: String = "") {
        if (VIEW.childCount == 0) {
            chartboostBanner = Banner(context, "banner", Banner.BannerSize.STANDARD, object : BannerCallback {
                override fun onAdLoaded(cacheEvent: CacheEvent, cacheError: CacheError?) {
                    Log.d("LOG", "Chartboost Banner onAdLoaded")
                    if(cacheError==null){
                        chartboostBanner?.show()
                    }else{
                        VIEW.removeAllViews()
                        AdsManager().initBanner(context, VIEW, ORDER, PAGE)
                    }
                }

                override fun onAdRequestedToShow(showEvent: ShowEvent) {
                    Log.d("LOG", "Chartboost Banner onAdRequestedToShow")
                }

                override fun onAdShown(showEvent: ShowEvent, showError: ShowError?) {
                    Log.d("LOG", "Chartboost Banner onAdShown")
                }

                override fun onAdClicked(clickEvent: ClickEvent, clickError: ClickError?) {
                    Log.d("LOG", "Chartboost Banner onAdClicked")
                }

                override fun onImpressionRecorded(impressionEvent: ImpressionEvent) {
                    Log.d("LOG", "Chartboost Banner onImpressionRecorded")
                }
            }, null)
            VIEW.addView(chartboostBanner)
            chartboostBanner?.cache()
        }
    }

    fun showInterstitialChartboost(context: Context, ORDER: Int = 0) {
        if (chartboostInterstitial!==null && chartboostInterstitial!!.isCached()) {
            chartboostInterstitial?.show()
            Log.d("LOG", "showInterstitialChartboost")
        }else{
            Log.d("LOG", "Interstitial Chartboost not loaded")
            AdsManager().showInterstitial(context, ORDER)
        }
    }

    fun initInterstitialChartboost(context: Context) {
        Log.d("LOG", "initInterstitialChartboost")
        chartboostInterstitial = Interstitial("interstitial", object : InterstitialCallback {
            override fun onAdClicked(event: ClickEvent, error: ClickError?) {
                Log.d("LOG", "Chartboost Interstitial onAdClicked")
            }

            override fun onAdDismiss(event: DismissEvent) {
                Log.d("LOG", "Chartboost Interstitial onAdDismiss")
                initInterstitialChartboost(context)
            }

            override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
                Log.d("LOG", "Chartboost Interstitial onAdLoaded")
            }

            override fun onAdRequestedToShow(event: ShowEvent) {
                Log.d("LOG", "Chartboost Interstitial onAdRequestedToShow")
            }

            override fun onAdShown(event: ShowEvent, error: ShowError?) {
                Log.d("LOG", "Chartboost Interstitial onAdShown")
            }

            override fun onImpressionRecorded(event: ImpressionEvent) {
                Log.d("LOG", "Chartboost Interstitial onImpressionRecorded")
            }
        }, null)
        chartboostInterstitial?.cache()
    }


    fun showRewardedChartboost(context: Context, ORDER: Int = 0) {
        if (chartboostRewarded!==null && chartboostRewarded!!.isCached()) {
            chartboostRewarded?.show()
            Log.d("LOG", "showRewardedChartboost")
        }else{
            Log.d("LOG", "Rewarded Chartboost not loaded")
            AdsManager().showInterstitial(context, ORDER)
        }
    }

    fun initRewardedChartboost(){
        chartboostRewarded = Rewarded("rewarded", object : RewardedCallback {
            override fun onAdClicked(event: ClickEvent, error: ClickError?) {
                Log.d("LOG", "Chartboost Rewarded onAdClicked")
            }

            override fun onAdDismiss(event: DismissEvent) {
                Log.d("LOG", "Chartboost Rewarded onAdDismiss")
                initRewardedChartboost()
            }

            override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
                Log.d("LOG", "Chartboost Rewarded onAdLoaded")
            }

            override fun onAdRequestedToShow(event: ShowEvent) {
                Log.d("LOG", "Chartboost Rewarded onAdRequestedToShow")
            }

            override fun onAdShown(event: ShowEvent, error: ShowError?) {
                Log.d("LOG", "Chartboost Rewarded onAdShown")
            }

            override fun onImpressionRecorded(event: ImpressionEvent) {
                Log.d("LOG", "Chartboost Rewarded onImpressionRecorded")
            }

            override fun onRewardEarned(event: RewardEvent) {
                Log.d("LOG", "Chartboost Rewarded onRewardEarned")
            }

        }, null)
        chartboostRewarded?.cache()
    }
}
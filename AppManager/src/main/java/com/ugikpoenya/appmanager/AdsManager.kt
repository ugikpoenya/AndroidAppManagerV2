package com.ugikpoenya.appmanager

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import com.ugikpoenya.appmanager.ads.AdmobManager
import com.ugikpoenya.appmanager.ads.FacebookManager
import com.ugikpoenya.appmanager.ads.UnityManager

var intervalCounter = 0
var ORDER_ADMOB: Int = 0
var ORDER_FACEBOOK: Int = 1
var ORDER_UNITY: Int = 2

class AdsManager {
    fun initAds(context: Context, function: () -> (Unit)) {
        Log.d("LOG", "Ads Manager initAds")
        if (ITEM_MODEL.admob_gdpr) {
            AdmobManager().initGdpr(context, function)
        } else {
            AdmobManager().initAdmobAds(context)
            function()
        }
        FacebookManager().initFacebookAds(context)
        UnityManager().initUnityAds(context)
    }


    fun initBanner(context: Context, view: RelativeLayout, ORDER: Int = 0, PAGE: String = "") {
        if (view.childCount == 0) {
            Log.d("LOG", "initBanner $ORDER")
            var priority: String? = ""
            if (PAGE.lowercase() == "home") priority = ITEM_MODEL.home_priority
            if (PAGE.lowercase() == "detail") priority = ITEM_MODEL.detail_priority
            if (priority.isNullOrEmpty()) priority = DEFAULT_PRIORITY

            val array = priority.split(",").map { it.toInt() }
            if (array.contains(ORDER)) {
                when {
                    array[ORDER] == ORDER_ADMOB -> AdmobManager().initAdmobBanner(context, view, ORDER + 1, PAGE)
                    array[ORDER] == ORDER_FACEBOOK -> FacebookManager().initFacebookBanner(context, view, ORDER + 1, PAGE)
                    array[ORDER] == ORDER_UNITY -> UnityManager().initUnityBanner(context, view, ORDER + 1, PAGE)
                    else -> initBanner(context, view, ORDER + 1, PAGE)
                }
            }
        }
    }

    fun initNative(context: Context, view: RelativeLayout, ORDER: Int = 0, PAGE: String = "") {
        if (view.childCount == 0) {
            Log.d("LOG", "initNative $ORDER")
            var priority: String? = ""
            if (PAGE.lowercase() == "home") priority = ITEM_MODEL.home_priority
            if (PAGE.lowercase() == "detail") priority = ITEM_MODEL.detail_priority
            if (priority.isNullOrEmpty()) priority = DEFAULT_PRIORITY

            val array = priority.split(",").map { it.toInt() }
            if (array.contains(ORDER)) {
                when {
                    array[ORDER] == ORDER_ADMOB -> AdmobManager().initAdmobNative(context, view, ORDER + 1, PAGE)
                    array[ORDER] == ORDER_FACEBOOK -> FacebookManager().initFacebookNative(context, view, ORDER + 1, PAGE)
                    else -> initNative(context, view, ORDER + 1, PAGE)
                }
            }
        }
    }

    fun showInterstitial(context: Context, ORDER: Int = 0) {
        if (intervalCounter <= 0) {
            Log.d("LOG", "Show  Interstitial $ORDER")
            var priority: String? = ITEM_MODEL.interstitial_priority
            if (priority.isNullOrEmpty()) priority = DEFAULT_PRIORITY
            val array = priority.split(",").map { it.toInt() }
            if (array.contains(ORDER)) {
                when {
                    array[ORDER] == ORDER_ADMOB -> AdmobManager().showInterstitialAdmob(context, ORDER + 1)
                    array[ORDER] == ORDER_FACEBOOK -> FacebookManager().showInterstitialFacebook(context, ORDER + 1)
                    array[ORDER] == ORDER_UNITY -> UnityManager().showInterstitialUnity(context, ORDER + 1)
                    else -> showInterstitial(context, ORDER + 1)
                }
            }
        } else {
            intervalCounter--
        }
    }

    fun showRewardedAds(context: Context, ORDER: Int = 0, function: (response: Boolean?) -> (Unit)) {
        Log.d("LOG", "Show  RewardedAds $ORDER")
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_loading)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.show()
        dialog.window?.attributes = lp

        var priority: String? = ITEM_MODEL.interstitial_priority
        if (priority.isNullOrEmpty()) priority = DEFAULT_PRIORITY
        val array = priority.split(",").map { it.toInt() }
        if (array.contains(ORDER)) {
            when {
                array[ORDER] == ORDER_ADMOB -> AdmobManager().showRewardedAdmob(context, ORDER + 1) {
                    Log.d("LOG", "showRewardedAdmob result " + it.toString())
                    dialog.dismiss()
                    function(it)
                }

                array[ORDER] == ORDER_FACEBOOK -> FacebookManager().showRewardedFacebook(context, ORDER + 1) {
                    Log.d("LOG", "showRewardedFacebook result " + it.toString())
                    dialog.dismiss()
                    function(it)
                }

                array[ORDER] == ORDER_UNITY -> UnityManager().showRewardedUnity(context, ORDER + 1) {
                    Log.d("LOG", "showRewardedUnity result " + it.toString())
                    dialog.dismiss()
                    function(it)
                }

                else -> showRewardedAds(context, ORDER) {
                    Log.d("LOG", "All rewarded result false")
                    dialog.dismiss()
                    function(false)
                }
            }
        } else {
            Log.d("LOG", "All rewarded null")
            dialog.dismiss()
            function(false)
        }
    }
}
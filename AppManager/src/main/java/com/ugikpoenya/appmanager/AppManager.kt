package com.ugikpoenya.appmanager

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View.GONE
import android.view.Window
import android.view.WindowManager
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AppManager {
    fun exitApp(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.setContentView(R.layout.dialog_exit)
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val btn_yes = (dialog.findViewById(R.id.btn_yes)) as AppCompatButton
        val btn_no = (dialog.findViewById(R.id.btn_no)) as AppCompatButton

        btn_yes.setOnClickListener {
            dialog.dismiss()
            (context as Activity).finish()
        }
        btn_no.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.attributes = lp
    }

    fun initOneSignal(context: Context) {
        val OneSignalAppId = Prefs(context).ITEM_MODEL.ONESIGNAL_APP_ID
        if (OneSignalAppId.isNotEmpty()) {
            Log.d("LOG", "initOneSignal")
            // Verbose Logging set to help debug issues, remove before releasing your app.

            if (BuildConfig.DEBUG) {
                OneSignal.Debug.logLevel = LogLevel.VERBOSE
            }

            // OneSignal Initialization
            OneSignal.initWithContext(context, OneSignalAppId)

            // requestPermission will show the native Android notification permission prompt.
            // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
            CoroutineScope(Dispatchers.IO).launch {
                OneSignal.Notifications.requestPermission(true)
            }
        }
    }


    fun initAppMain(context: Context) {
        initDialogRedirect(context)
        initOneSignal(context)
    }

    fun initAppMain(context: Context, lyBanner: RelativeLayout) {
        initAppMain(context)
        AdsManager().initBanner(context, lyBanner, 0, "home")
    }

    fun showPrivacyPolicy(context: Context) {
        val privacyPolicy = Prefs(context).PRIVACY_POLICY
        Log.d("LOG", "Open privacyPolicy $privacyPolicy")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicy))
        context.startActivity(intent)
    }

    fun initDialogRedirect(context: Context) {
        if (URLUtil.isValidUrl(Prefs(context).ITEM_MODEL.redirect_url)) {
            val uri = Uri.parse(Prefs(context).ITEM_MODEL.redirect_url)
            val id = uri.getQueryParameter("id")

            if (!id.isNullOrEmpty()) {
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
                dialog.setContentView(R.layout.dialog_redirect)
                if (Prefs(context).ITEM_MODEL.redirect_cancelable) dialog.setCancelable(true)
                else dialog.setCancelable(false)

                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window?.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT

                if (Prefs(context).ITEM_MODEL.redirect_image_url.isNullOrEmpty()) {
                    (dialog.findViewById(R.id.imageView) as ImageView).visibility = GONE
                } else {
                    Picasso.get().load(Prefs(context).ITEM_MODEL.redirect_image_url).resize(50, 50).centerCrop().into((dialog.findViewById(R.id.imageView) as ImageView))
                }

                (dialog.findViewById(R.id.txtTitle) as TextView).text = Prefs(context).ITEM_MODEL.redirect_title
                (dialog.findViewById(R.id.txtContent) as TextView).text = Prefs(context).ITEM_MODEL.redirect_content

                val btnUpdate = dialog.findViewById(R.id.btnUpdate) as Button
                val intent = (context as Activity).packageManager.getLaunchIntentForPackage(id)

                if (intent == null) {
                    btnUpdate.text = Prefs(context).ITEM_MODEL.redirect_button
                } else {
                    btnUpdate.text = "Open"
                }

                (dialog.findViewById(R.id.btnUpdate) as Button).setOnClickListener {
                    if (intent == null) {
                        Log.d("LOG", "Open URL " + Prefs(context).ITEM_MODEL.redirect_url)
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW, Uri.parse(Prefs(context).ITEM_MODEL.redirect_url)
                            )
                        )
                    } else {
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        context.startActivity(intent)
                    }
                }
                (dialog.findViewById(R.id.btnClose) as Button).setOnClickListener {
                    dialog.dismiss()
                    if (!Prefs(context).ITEM_MODEL.redirect_cancelable) {
                        context.finish()
                    }
                }
                dialog.show()
                dialog.window?.attributes = lp
            }
        }
    }

    fun rateApp(context: Context) {
        val packageName = (context as Activity).packageName
        val rateUrl = "https://play.google.com/store/apps/details?id=$packageName"
        Log.d("LOG", "Open URL $rateUrl")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rateUrl))
        context.startActivity(intent)
    }

    fun shareApp(context: Context, appName: String?) {
        val packageName = (context as Activity).packageName
        val rateUrl = "https://play.google.com/store/apps/details?id=$packageName"
        val contentShare: String = context.resources.getString(R.string.SHARE_APP_TEXT, appName, rateUrl)

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, contentShare)
        sendIntent.type = "text/plain"
        context.startActivity(Intent.createChooser(sendIntent, "Share using..."))
    }

    fun nextApp(context: Context) {
        if (Prefs(context).ITEM_MODEL.more_app.isNullOrEmpty()) {
            rateApp(context)
        } else {
            Log.d("LOG", "Open URL " + Prefs(context).ITEM_MODEL.more_app)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Prefs(context).ITEM_MODEL.more_app))
            context.startActivity(intent)
        }
    }
}
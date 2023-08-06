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
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.squareup.picasso.Picasso

var PRIVACY_POLICY_TITLE = "Privacy Policy"
var PRIVACY_POLICY_ACCEPT = "ACCEPT"
var PRIVACY_POLICY_DECLINE = "DECLINE"
var PRIVACY_POLICY_CONTENT = "By using this Application, you agree with Terms of Conditions, Cookie Policy and Privacy Policy and agree to have your personal data, behavior transferred and processed outside the EU."


class AppManager {
    fun setPrivacyPolicyTitle(value: String) {
        PRIVACY_POLICY_TITLE = value
    }

    fun setPrivacyPolicyAccept(value: String) {
        PRIVACY_POLICY_ACCEPT = value
    }

    fun setPrivacyPolicyDecline(value: String) {
        PRIVACY_POLICY_DECLINE = value
    }

    fun setPrivacyPolicyContent(value: String) {
        PRIVACY_POLICY_CONTENT = value
    }

    fun initPrivacyPolicy(context: Context) {
        if (!Prefs(context).privacy_policy) {
            showPrivacyPolicy(context)
        }
    }

    fun showPrivacyPolicy(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT

        if (Prefs(context).ITEM_MODEL.privacy_policy.isNullOrEmpty()) {
            dialog.setContentView(R.layout.dialog_privacy_policy)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        } else {
            dialog.setContentView(R.layout.dialog_privacy_policy_web)
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
        }

        val tv_title = (dialog.findViewById(R.id.tv_title)) as TextView
        val bt_accept = (dialog.findViewById(R.id.bt_accept)) as AppCompatButton
        val bt_decline = (dialog.findViewById(R.id.bt_decline)) as AppCompatButton

        tv_title.text = PRIVACY_POLICY_TITLE
        bt_accept.text = PRIVACY_POLICY_ACCEPT
        bt_decline.text = PRIVACY_POLICY_DECLINE

        if (Prefs(context).ITEM_MODEL.privacy_policy.isNullOrEmpty()) {
            val tv_content = (dialog.findViewById(R.id.tv_content)) as TextView
            tv_content.movementMethod = LinkMovementMethod.getInstance()
            tv_content.text = PRIVACY_POLICY_CONTENT
        } else {
            val webView = (dialog.findViewById(R.id.webView)) as WebView
            webView.loadUrl(Prefs(context).ITEM_MODEL.privacy_policy.toString())
        }
        bt_accept.setOnClickListener {
            Prefs(context).privacy_policy = true
            dialog.dismiss()
        }
        bt_decline.setOnClickListener {
            Prefs(context).privacy_policy = false
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.attributes = lp
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
        val contentShare = "Get $appName application on Google Play \n $rateUrl"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, contentShare)
        sendIntent.type = "text/plain"
        context.startActivity(Intent.createChooser(sendIntent, "Share using..."))
    }

    fun nextApp(context: Context) {
        val packageName = (context as Activity).packageName
        if (!URLUtil.isValidUrl(Prefs(context).ITEM_MODEL.more_app)) {
            Prefs(context).ITEM_MODEL.more_app = "https://play.google.com/store/apps/details?id=$packageName"
        }
        Log.d("LOG", "Open URL " + Prefs(context).ITEM_MODEL.more_app)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Prefs(context).ITEM_MODEL.more_app))
        context.startActivity(intent)
    }
}
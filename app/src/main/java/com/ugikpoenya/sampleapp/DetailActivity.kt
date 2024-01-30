package com.ugikpoenya.sampleapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.model.PostModel
import com.ugikpoenya.sampleapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AdsManager().initBanner(this, binding.lyBannerAds,0,"detail")
        AdsManager().initNative(this, binding.lyNativeAds,0,"detail")


        val bundle = intent.extras
        val postModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getSerializable("postModel", PostModel::class.java) as PostModel
        } else {
            @Suppress("DEPRECATION")
            bundle?.getSerializable("postModel") as PostModel
        }
        title = postModel.post_title

        if (postModel.post_image.isNullOrEmpty()) {
            binding.imageView.visibility = GONE
        } else {
            binding.imageView.visibility = VISIBLE
            postModel.showPostImage(binding.imageView)
        }

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        binding.webView.clearCache(true)
        var html = postModel.post_content!!
        var text = html
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (html.length > 17500) text =
                html.substring(0, 17500) + ".........................................."
        }
        binding.webView.loadData(text, "text/html", "utf-8")



    }
}
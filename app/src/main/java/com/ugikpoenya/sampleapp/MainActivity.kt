package com.ugikpoenya.sampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.AppManager
import com.ugikpoenya.appmanager.FirebaseManager
import com.ugikpoenya.appmanager.Prefs
import com.ugikpoenya.appmanager.ServerManager
import com.ugikpoenya.appmanager.holder.AdsViewHolder
import com.ugikpoenya.sampleapp.databinding.ActivityMainBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val appManager = AppManager()
    val adsManager = AdsManager()
    val serverManager = ServerManager()
    val firebaseManager = FirebaseManager()
    val groupAdapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Get Category
        serverManager.getCategories(this).forEach {
            Log.d("LOG", it.categori_id + " / " + it.categori)
        }

        firebaseManager.getPosts(this, Prefs(this).ITEM_MODEL.asset_url) { response ->
            response?.forEach {
                Log.d("LOG", it.key + " / " + it.post_title)
            }
        }

        Log.d("LOG_FB", Prefs(this).ITEM_MODEL.asset_url)
        firebaseManager.getStorage(this, Prefs(this).ITEM_MODEL.asset_url) { response ->
            response?.files?.forEach {
                Log.d("LOG_FB_name", it.name.toString())
                Log.d("LOG_FB_path", it.path.toString())
                Log.d("LOG_FB_url", it.url.toString())
                Log.d("LOG_FB_meta", it.metadata?.id.toString())
                Log.d("LOG_FB_meta", it.metadata?.title.toString())
                Log.d("LOG_FB_meta", it.metadata?.category.toString())
                Log.d("LOG_FB_meta", it.metadata?.content.toString())
            }

            response?.folder?.forEach {
                Log.d("LOG_FB_FD", "----------------------------------------")
                Log.d("LOG_FB_FD", it.key.toString())
                it.files?.forEach { file ->
                    Log.d("LOG_FB_name", file.name.toString())
                    Log.d("LOG_FB_path", file.path.toString())
                    Log.d("LOG_FB_url", file.url.toString())
                }

                it.folder?.forEach { fd ->
                    Log.d("LOG_FB_FD", "==================================")
                    Log.d("LOG_FB_FD", fd.key.toString())
                    fd.files?.forEach { file ->
                        Log.d("LOG_FB_name", file.name.toString())
                        Log.d("LOG_FB_path", file.path.toString())
                        Log.d("LOG_FB_url", file.url.toString())
                    }
                }
            }
        }

        serverManager.getPosts(this) { posts ->
            posts?.forEach {
                Log.d("LOG", it.post_title.toString())
                it.categories?.forEach { cat ->
                    Log.d("LOG", cat.categori_id + " / " + cat.categori)
                }
            }
        }

        serverManager.getAssetFiles(this) { files -> null }
        serverManager.getAssetFiles(this, "Islami") { files -> null }

        serverManager.getAssetFolders(this) { folders -> null }
        serverManager.getAssetFolders(this, "Islami") { folders -> null }

        serverManager.getAssets(this) { files, folders -> null }
        serverManager.getAssets(this, "Islami") { files, folders -> null }

        serverManager.getFolder(this, "Mp3") { files, folders -> null }

        appManager.initAppMain(this, binding.lyBannerAds)

        val listLayoutManager = LinearLayoutManager(this)
        listLayoutManager.orientation = RecyclerView.VERTICAL
        listLayoutManager.generateDefaultLayoutParams()
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, listLayoutManager.orientation)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.layoutManager = listLayoutManager
        binding.recyclerView.adapter = groupAdapter

        groupAdapter.add(ItemViewHolder("Posts") {
            startActivity(Intent(this, PostsActivity::class.java))
        })

        groupAdapter.add(ItemViewHolder("Privacy Policys") {
            appManager.showPrivacyPolicy(this)
        })

        groupAdapter.add(AdsViewHolder(this, 0, "home"))


        groupAdapter.add(ItemViewHolder("Rate App") {
            appManager.rateApp(this)
        })

//        groupAdapter.add(AdsViewHolder(this, 0, "detail"))

        groupAdapter.add(ItemViewHolder("More App") {
            appManager.nextApp(this)
        })

//        groupAdapter.add(AdsViewHolder(this, 0, "small"))

        groupAdapter.add(ItemViewHolder("Share") {
            appManager.shareApp(this, getString(R.string.app_name))
        })

//        groupAdapter.add(AdsViewHolder(this, 0, "medium"))

        groupAdapter.add(ItemViewHolder("Interstitial") {
            adsManager.showInterstitial(this, 0)
        })

        groupAdapter.add(ItemViewHolder("Rewarded Ads") {
            adsManager.showRewardedAds(this, 0)
        })

        groupAdapter.add(ItemViewHolder("Reset GDPR") {
            AdsManager().resetGDPR()
        })
    }

    override fun onBackPressed() {
        AppManager().exitApp(this)
    }

    override fun onStart() {
        super.onStart()
        AdsManager().showOpenAds(this)
    }
}
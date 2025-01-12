package com.ugikpoenya.sampleapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.Manifest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.AppManager
import com.ugikpoenya.appmanager.ServerManager
import com.ugikpoenya.appmanager.holder.AdsViewHolder
import com.ugikpoenya.masterguidev4.DownloadCallback
import com.ugikpoenya.masterguidev4.VolleyDownload
import com.ugikpoenya.sampleapp.databinding.ActivityMainBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val appManager = AppManager()
    val adsManager = AdsManager()
    val serverManager = ServerManager()

    val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val PERMISSION_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Get Category
        serverManager.getCategories(this)?.forEach {
            Log.d("LOG", " categories "+it.category_key + " / " + it.title+ " / " + it.category)
            serverManager.getPostResponse(this, "categories/"+it.category_key) { postResponse->
                Log.d("LOG", "postResponse categories:  " + postResponse?.data?.size)
                postResponse?.data?.forEach { post->
                    Log.d("LOG", "postResponse categories Title:  " + post.post_title)
                }

            }
        }

        serverManager.getPosts(this) { posts ->
            posts?.forEach {
                Log.d("LOG", it.post_title.toString())
//                it.categories?.forEach { cat ->
//                    Log.d("LOG", cat.category_key + " / " + cat.category_key)
//                }
            }
        }
//
//        serverManager.getStorage(this) { storageModel ->
//            storageModel?.files?.forEach {
//                Log.d("LOG", it.name.toString())
//            }
//            storageModel?.folders?.forEach {
//                Log.d("LOG", it.folder_name.toString()+" / "+it.files?.size.toString())
//            }
//        }

//        serverManager.getAssetFiles(this) { files -> null }
//        serverManager.getAssetFiles(this, "Islami") { files -> null }
//
//        serverManager.getAssetFolders(this) { folders -> null }
//        serverManager.getAssetFolders(this, "Islami") { folders -> null }
//
//        serverManager.getAssets(this) { files, folders -> null }
//        serverManager.getAssets(this, "Islami") { files, folders -> null }
//
//        serverManager.getFolder(this, "Mp3") { files, folders -> null }

        appManager.initAppMain(this, binding.lyBannerAds)

        val listLayoutManager = LinearLayoutManager(this)
        listLayoutManager.orientation = RecyclerView.VERTICAL
        listLayoutManager.generateDefaultLayoutParams()
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, listLayoutManager.orientation)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.layoutManager = listLayoutManager
        binding.recyclerView.adapter = groupAdapter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }

        groupAdapter.add(ItemViewHolder("Download") {
            val dirPath = Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_DOWNLOADS
            var fileDownload = "https://firebasestorage.googleapis.com/v0/b/ugikpoenya-app-manager.appspot.com/o/How%20to%20Draw%20Cinnamoroll.mp4?alt=media&token=920e07a5-2429-4858-88e8-baba547d3faa"
            val file = File(fileDownload)
            var filePath = URLDecoder.decode(file.name, StandardCharsets.UTF_8.toString())
            filePath = filePath.substringBefore("?")
            filePath = filePath.replace("/", "_")

            filePath = dirPath + "/" + filePath

            VolleyDownload(this, fileDownload, filePath, object : DownloadCallback {
                override fun onProgressUpdate(progress: Int) {
                    runOnUiThread {
                        Log.d("LOG", "Progress ${progress}%")
                    }
                }

                override fun onDownloadComplete(filePath: String) {
                    runOnUiThread {
                        Log.d("LOG", filePath)
                        Toast.makeText(applicationContext, "Download completed!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onDownloadFailed(errorMessage: String) {
                    runOnUiThread {
                        Log.e("LOG", "Error: ${errorMessage}")
                        Toast.makeText(applicationContext, "Download failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        })

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("LOG", "PERMISSION_GRANTED")
            } else {
                // Handle permission denial
            }
        }
    }

}
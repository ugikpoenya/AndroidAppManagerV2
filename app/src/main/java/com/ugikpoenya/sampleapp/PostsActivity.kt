package com.ugikpoenya.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.ServerManager
import com.ugikpoenya.appmanager.holder.AdsViewHolder
import com.ugikpoenya.sampleapp.databinding.ActivityPostsBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class PostsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostsBinding

    val adsManager = AdsManager()
    val serverManager = ServerManager()
    val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adsManager.initBanner(this, binding.lyBannerAds, 0, "detail")

        val listLayoutManager = LinearLayoutManager(this)
        listLayoutManager.orientation = RecyclerView.VERTICAL
        listLayoutManager.generateDefaultLayoutParams()
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, listLayoutManager.orientation)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.layoutManager = listLayoutManager
        binding.recyclerView.adapter = groupAdapter

        var index=0
        serverManager.getPosts(this) { response ->
            response?.forEach {
                groupAdapter.add(PostViewHolder(it))

                if(index==1) groupAdapter.add(AdsViewHolder(this, 0, "detail"))
                if(index==5) groupAdapter.add(AdsViewHolder(this, 0, "small"))
                if(index==10) groupAdapter.add(AdsViewHolder(this, 0, "medium"))
                index++
            }
        }
    }


}
package com.ugikpoenya.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.ServerManager
import com.ugikpoenya.appmanager.holder.AdsViewHolder
import com.ugikpoenya.appmanager.holder.LoadingViewHolder
import com.ugikpoenya.appmanager.tools.EndlessRecyclerViewScrollListener
import com.ugikpoenya.sampleapp.databinding.ActivityPostsBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class PostsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostsBinding

    val adsManager = AdsManager()
    val serverManager = ServerManager()
    val groupAdapter = GroupAdapter<GroupieViewHolder>()
    var isLoading = false
    var itemIndex = 0
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

        binding.recyclerView.addOnScrollListener(object : EndlessRecyclerViewScrollListener(listLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                if (!isLoading) {
                    getPosts()
                }
            }
        })

        getPosts()
    }

    fun getPosts() {
        isLoading = true
        if (itemIndex > 0) {
            groupAdapter.add(LoadingViewHolder())
        }
        serverManager.getPosts(this) { response ->
            if (itemIndex > 0) {
                groupAdapter.remove(groupAdapter.getItem(groupAdapter.groupCount - 1))
            }
            response?.forEach {
                groupAdapter.add(PostViewHolder(it))
                if (itemIndex == 1) groupAdapter.add(AdsViewHolder(this, 0, "home"))
                if (itemIndex == 5) groupAdapter.add(AdsViewHolder(this, 0, "detail"))
                itemIndex++
            }
            isLoading = false
        }
    }
}
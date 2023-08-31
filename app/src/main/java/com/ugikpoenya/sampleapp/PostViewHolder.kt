package com.ugikpoenya.sampleapp

import android.view.View
import com.ugikpoenya.appmanager.model.PostModel
import com.ugikpoenya.sampleapp.databinding.ItemPostBinding
import com.xwray.groupie.viewbinding.BindableItem


class PostViewHolder(var postModel: PostModel) : BindableItem<ItemPostBinding>() {
    override fun getLayout(): Int = R.layout.item_post
    override fun initializeViewBinding(view: View): ItemPostBinding =
        ItemPostBinding.bind(view)

    override fun bind(viewHolder: ItemPostBinding, position: Int) {
        viewHolder.txtTitle.text = postModel.post_title
        postModel.showPostImage(viewHolder.imageView)
    }
}

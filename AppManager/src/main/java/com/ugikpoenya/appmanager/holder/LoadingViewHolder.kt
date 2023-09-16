package com.ugikpoenya.appmanager.holder


import android.view.View
import com.ugikpoenya.appmanager.R
import com.ugikpoenya.appmanager.databinding.ItemLoadingViewBinding
import com.xwray.groupie.viewbinding.BindableItem


class LoadingViewHolder() : BindableItem<ItemLoadingViewBinding>() {
    override fun getLayout(): Int = R.layout.item_loading_view
    override fun initializeViewBinding(view: View): ItemLoadingViewBinding =
        ItemLoadingViewBinding.bind(view)

    override fun bind(viewHolder: ItemLoadingViewBinding, position: Int) {
    }


}

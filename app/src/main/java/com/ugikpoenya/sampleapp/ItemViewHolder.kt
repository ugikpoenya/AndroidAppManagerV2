package com.ugikpoenya.sampleapp

import android.view.View
import com.ugikpoenya.sampleapp.databinding.ItemButtonBinding
import com.xwray.groupie.viewbinding.BindableItem


class ItemViewHolder(var title: String, var function: () -> (Unit)) : BindableItem<ItemButtonBinding>() {
    override fun getLayout(): Int = R.layout.item_button
    override fun initializeViewBinding(view: View): ItemButtonBinding =
        ItemButtonBinding.bind(view)

    override fun bind(viewHolder: ItemButtonBinding, position: Int) {
        viewHolder.btnTitle.text = title
        viewHolder.btnTitle.setOnClickListener {
            function()
        }
    }
}

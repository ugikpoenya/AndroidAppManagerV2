package com.ugikpoenya.appmanager.holder


import android.app.Activity
import android.view.View
import com.ugikpoenya.appmanager.AdsManager
import com.ugikpoenya.appmanager.R
import com.ugikpoenya.appmanager.databinding.ItemAdsViewBinding
import com.xwray.groupie.viewbinding.BindableItem


class AdsViewHolder(var activity: Activity, var ORDER: Int, var PAGE: String) : BindableItem<ItemAdsViewBinding>() {
    override fun getLayout(): Int = R.layout.item_ads_view
    override fun initializeViewBinding(view: View): ItemAdsViewBinding =
        ItemAdsViewBinding.bind(view)

    override fun bind(viewHolder: ItemAdsViewBinding, position: Int) {
        AdsManager().initNative(activity, viewHolder.lyNativeAds, ORDER, PAGE)
    }


}

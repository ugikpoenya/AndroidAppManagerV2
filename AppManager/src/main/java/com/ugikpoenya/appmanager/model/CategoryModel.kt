package com.ugikpoenya.appmanager.model

import java.io.Serializable

class CategoryModel : Serializable {
    var category: String? = null
    var category_asset: String? = null
    var category_audio: String? = null
    var category_image: String? = null
    var category_video: String? = null
    var category_content: String? = null
    var posts: ArrayList<PostModel>? = null
}


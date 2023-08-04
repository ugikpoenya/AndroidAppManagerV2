package com.ugikpoenya.appmanager.model

import java.io.Serializable

class PostModel : Serializable {
    var id: Int? = null
    var post_id: Int? = null
    var post_date: String? = null
    var post_title: String? = null
    var post_content: String? = null
    var post_image: String? = null
    var post_status: String? = null
    var post_type: String? = null
    var post_parent: Int? = null
    var item: ArrayList<PostModel>? = null
    var post_item: ArrayList<PostModel>? = null
}



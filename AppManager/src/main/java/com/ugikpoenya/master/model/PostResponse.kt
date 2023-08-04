package com.ugikpoenya.master.model

import java.io.Serializable

class PostResponse : Serializable {
    var status: Boolean? = false
    var message: String? = null
    var data: ArrayList<PostModel>? = null
    var post: PostModel? = null
    var files: ArrayList<String>? = null
    var folders: Map<String, ArrayList<String>>? = null
}


package com.ugikpoenya.appmanager.room


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ugikpoenya.appmanager.model.PostModel
import java.io.Serializable

@Entity(tableName = "table_posts")
class TablePosts : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id:Int? = null

    @ColumnInfo(name = "post_id")
    var post_id:Int?=null

    @ColumnInfo(name = "post_date")
    var post_date:String?=null

    @ColumnInfo(name = "post_title")
    var post_title:String?=null

    @ColumnInfo(name = "post_content")
    var post_content:String?=null

    @ColumnInfo(name = "post_image")
    var post_image:String?=null

    @ColumnInfo(name = "post_status")
    var post_status:String?=null

    @ColumnInfo(name = "post_type")
    var post_type:String?=null

    @ColumnInfo(name = "post_parent")
    var post_parent:Int?=null

    fun getPost():PostModel{
        var  postModel = PostModel()
        postModel.id=id
        postModel.post_id=post_id
        postModel.post_date=post_date
        postModel.post_title=post_title
        postModel.post_content=post_content
        postModel.post_image=post_image
        postModel.post_status=post_status
        postModel.post_type=post_type
        postModel.post_parent=post_parent
        return postModel
    }

    fun setPost(postModel: PostModel){
        id=postModel.id
        post_id=postModel.post_id
        post_date=postModel.post_date
        post_title=postModel.post_title
        post_content=postModel.post_content
        post_image=postModel.post_image
        post_status=postModel.post_status
        post_type=postModel.post_type
        post_parent=postModel.post_parent
    }

}
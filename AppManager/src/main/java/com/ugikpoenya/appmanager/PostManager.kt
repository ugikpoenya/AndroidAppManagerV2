package com.ugikpoenya.appmanager

import android.content.Context
import android.util.Log
import com.ugikpoenya.appmanager.model.PostModel

class PostManager {
    fun setPosts(context: Context, posts: List<PostModel>) {
        Prefs(context).post_model_array_list = posts
    }

    fun getPosts(context: Context): List<PostModel> {
        return Prefs(context).post_model_array_list
    }

    fun getPosts(context: Context, filter: String): List<PostModel> {
        return Prefs(context).post_model_array_list
            .filter { it -> ((it.post_title + " " + it.post_content).lowercase().contains(filter.lowercase())) }
    }

    fun getPostsType(context: Context, post_type: String?): List<PostModel> {
        return Prefs(context).post_model_array_list
            .filter { it -> (it.post_type == post_type) }
    }

    fun getPostsType(context: Context, post_type: String?, filter: String): List<PostModel> {
        return Prefs(context).post_model_array_list
            .filter { it -> (it.post_type == post_type) }
            .filter { it -> ((it.post_title + " " + it.post_content).lowercase().contains(filter.lowercase())) }
    }

    fun addPost(context: Context, postModel: PostModel, position: Int) {
        val posts = ArrayList<PostModel>()
        posts.addAll(Prefs(context).post_model_array_list)
        posts.add(position, postModel)
        Prefs(context).post_model_array_list = posts
    }

    fun getPostById(context: Context, post_id: Int?): PostModel? {
        return Prefs(context).post_model_array_list.find { it.post_id == post_id }
    }

    fun getPostById(context: Context, post_id: Int?, post_type: String?): PostModel? {
        return Prefs(context).post_model_array_list.find { it.post_id == post_id && it.post_type == post_type }
    }

    fun updatePost(context: Context, postModel: PostModel): Boolean {
        val posts = ArrayList<PostModel>()
        posts.addAll(Prefs(context).post_model_array_list)
        val post = posts.find { it.post_id == postModel.post_id && it.post_type == postModel.post_type }
        if (post == null) {
            return false
        } else {
            Log.d("", "Update : " + post.post_id + " ->" + post.post_title)
            posts[posts.indexOf(post)] = postModel
            Prefs(context).post_model_array_list = posts
            return true
        }
    }

    fun deletePost(context: Context, postModel: PostModel): Boolean {
        return deletePost(context, postModel.post_id, postModel.post_type)
    }

    fun deletePost(context: Context, post_id: Int?): Boolean {
        val posts = ArrayList<PostModel>()
        posts.addAll(Prefs(context).post_model_array_list)
        val post = posts.find { it.post_id == post_id }
        if (post == null) {
            return false
        } else {
            Log.d("", "Delete : " + post.post_id + " ->" + post.post_title)
            posts.remove(post)
            Prefs(context).post_model_array_list = posts
            return true
        }
    }

    fun deletePost(context: Context, post_id: Int?, post_type: String?): Boolean {
        val posts = ArrayList<PostModel>()
        posts.addAll(Prefs(context).post_model_array_list)
        val post = posts.find { it.post_id == post_id && it.post_type == post_type }
        if (post == null) {
            return false
        } else {
            Log.d("", "Delete : " + post.post_id + " ->" + post.post_title)
            posts.remove(post)
            Prefs(context).post_model_array_list = posts
            return true
        }
    }
}
package com.ugikpoenya.appmanager.room


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ugikpoenya.appmanager.model.PostModel


@Dao
interface DAO {
    /* table notification transaction ----------------------------------------------------------- */

    @Query("SELECT * FROM table_posts WHERE post_title LIKE  '%'||:filter||'%' OR post_content LIKE  '%'||:filter||'%'")
    fun getPosts(filter: String): List<TablePosts>

    @Query("SELECT * FROM table_posts WHERE post_type=:post_type AND (post_title LIKE  '%'||:filter||'%' OR post_content LIKE  '%'||:filter||'%')")
    fun getPostsType(post_type: String, filter: String): List<TablePosts>

    @Query("SELECT * FROM table_posts WHERE post_parent=:post_id AND (post_title LIKE  '%'||:filter||'%' OR post_content LIKE  '%'||:filter||'%')")
    fun getPostsParent(post_id: Int, filter: String): List<TablePosts>

    @Query("SELECT * FROM table_posts WHERE id=:id")
    fun getPost(id: Int): TablePosts

    @get:Query("SELECT COUNT(id) FROM table_posts")
    val getPostsCount: Int

    @Query("SELECT COUNT(id) FROM table_posts  WHERE post_type=:post_type ")
    fun getPostsTypeCount(post_type: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(tablePosts: TablePosts)

    @Query("DELETE FROM table_posts")
    fun removeAllPosts()

    @Query("DELETE FROM table_posts WHERE post_type=:post_type ")
    fun removeAllPostsType(post_type: String)

    @Query("DELETE FROM table_posts WHERE id = :id")
    fun deletePost(id: Int)

    @Delete
    fun delete(tablePosts: TablePosts)

}

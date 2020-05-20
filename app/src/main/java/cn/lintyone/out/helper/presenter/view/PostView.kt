package cn.lintyone.out.helper.presenter.view

import com.haoge.easyandroid.mvp.MVPView

interface PostView : MVPView {

    fun onUploadImage(image: String) {}

    fun onCreatePost() {}

    fun noMore() {}

    fun onLoadPosts(list: List<PostsQuery.Post>) {}

    fun onPost(post: PostQuery.Post) {}

    fun onCommentsByPost(list: List<PostQuery.CommentByPost>) {}

    fun onComments(list: List<CommentsQuery.CommentByPost>) {}

    fun refreshComment(){}

    fun onRefreshPost() {}
}
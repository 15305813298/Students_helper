package cn.lintyone.out.helper.presenter

import CreatePostMutation
import cn.lintyone.out.helper.api.ApolloFactory
import cn.lintyone.out.helper.api.RetrofitFactory
import cn.lintyone.out.helper.api.Upload
import cn.lintyone.out.helper.common.execute
import cn.lintyone.out.helper.common.okHttpExecute
import cn.lintyone.out.helper.presenter.view.PostView
import com.haoge.easyandroid.mvp.MVPPresenter
import okhttp3.MultipartBody
import type.CommentInput
import type.PostInput

class PostPresenter(view: PostView) : MVPPresenter<PostView>(view) {

    fun uploadImage(image: MultipartBody.Part) {
        RetrofitFactory.getInstance().create(Upload::class.java)
            .image(image)
            .okHttpExecute {
                getView().onUploadImage(it.message!!)
            }
    }

    fun createPost(title: String, content: String, image: String) {
        val postInput = PostInput.builder()
            .title(title)
            .content(content)
        if (image.isNotEmpty()) {
            postInput.image(image)
        }
        val mutate = CreatePostMutation.builder()
            .postInput(postInput.build())
            .build()
        ApolloFactory.instance.mutate(mutate)
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.createPost().toString())
                    getView().onCreatePost()
                    getView().onRefreshPost()
                }
            }
    }

    fun posts(page: Int, filter: String) {
        val query = PostsQuery.builder()
            .filter(filter)
            .page(page)
            .build()
        ApolloFactory.instance.query(query)
            .execute { ok, res ->
                if (ok) {
                    if (res.data()?.posts()!!.size <= 0) {
                        getView().noMore()
                        return@execute
                    }
                    getView().onLoadPosts(res.data()?.posts()!!)
                }
            }
    }

    fun post(id: Int, page: Int) {
        val query = PostQuery.builder()
            .id(id)
            .page(page)
            .build()
        ApolloFactory.instance.query(query)
            .execute { ok, res ->
                if (ok) {
                    getView().onPost(res.data()?.post()!!)
                    getView().onCommentsByPost(res.data()?.commentByPost()!!)
                }
            }
    }

    fun comment(postId: Int, content: String, commentId: Int? = null) {
        val commentInput = CommentInput.builder()
            .targetId(commentId)
            .content(content)
            .postId(postId)
            .build()
        val mutate = CreateCommentMutation.builder()
            .commentInput(commentInput)
            .build()
        ApolloFactory.instance.mutate(mutate)
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.createComment().toString())
                    getView().refreshComment()
                }
            }
    }

    fun comments(id: Int, page: Int) {
        val query = CommentsQuery.builder()
            .id(id)
            .page(page)
            .build()
        ApolloFactory.instance.query(query)
            .execute { ok, res ->
                if (ok) {
                    if (res.data()?.commentByPost()!!.size <= 0) {
                        getView().noMore()
                        return@execute
                    }
                    getView().onComments(res.data()?.commentByPost()!!)
                }
            }
    }
}
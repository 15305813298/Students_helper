package cn.lintyone.out.helper.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.common.Constant
import cn.lintyone.out.helper.presenter.PostPresenter
import cn.lintyone.out.helper.presenter.view.PostView
import cn.lintyone.out.helper.ui.binder.CommentBinder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drakeet.multitype.MultiTypeAdapter
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.visible
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : BaseActivity<PostPresenter>(), PostView {
    override fun createPresenter(): PostPresenter {
        return PostPresenter(this)
    }

    private val item = ArrayList<Any>()
    private val adapter = MultiTypeAdapter(item)
    private val manager = LinearLayoutManager(this)
    private var page = 0
    private var id = 0
    lateinit var builder: QMUIDialog.EditTextDialogBuilder


    override fun initView() {
        setContentView(R.layout.activity_post)
        mRV.adapter = adapter
        mRV.layoutManager = manager

        id = intent.getStringExtra("id").toInt()

        adapter.register(CommentBinder(mPresenter, id))


        mPresenter?.post(id, page++)
        mRefresh.setOnRefreshListener {
            page = 0
            item.clear()
            adapter.notifyDataSetChanged()
            mPresenter?.post(id, page++)
        }
        mRefresh.setOnLoadMoreListener {
            mPresenter?.comments(id, page++)
        }
        mComment.click {
            builder = QMUIDialog.EditTextDialogBuilder(this)
                .setTitle("请输入评论内容")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        mPresenter?.getView()?.toastMessage("评论内容不得为空")
                    } else {
                        mPresenter?.comment(
                            id,
                            builder.editText.text.toString()
                        )
                        dialog.cancel()
                    }
                })
            builder.show()
        }
    }

    override fun onPost(post: PostQuery.Post) {
        Glide.with(this)
            .load(if (post.user().avatar() != null) Constant.SERVER + post.user().avatar() else R.drawable.default_avatar)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(180)))
            .into(mAvatar)
        mTitle.text = post.title()
        mContent.text = post.content()
        mCountComment.text = post.countComment().toString()
        mCountView.text = post.countView().toString()
        mName.text = post.user().nickName() ?: post.user().userName()
        if (post.image() != null) {
            Glide.with(this)
                .load(Constant.SERVER + post.image())
                .into(mImage)
            mImage.visible()
        }
    }

    override fun onComments(list: List<CommentsQuery.CommentByPost>) {
        item.addAll(list)
        adapter.notifyDataSetChanged()
        mRefresh.finishRefresh(true)
        mRefresh.finishLoadMore(true)
    }

    override fun noMore() {
        mRefresh.finishRefresh(1, true, true)
        mRefresh.finishLoadMore(1, true, true)
    }

    override fun onCommentsByPost(list: List<PostQuery.CommentByPost>) {
        item.addAll(list)
        adapter.notifyDataSetChanged()
        mRefresh.finishRefresh(true)
        mRefresh.finishLoadMore(true)
    }

    override fun refreshComment() {
        page = 0
        item.clear()
        adapter.notifyDataSetChanged()
        mPresenter?.post(id, page++)
        mRefresh.finishRefresh(true)
        mRefresh.finishLoadMore(true)
    }
}
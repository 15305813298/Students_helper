package cn.lintyone.out.helper.ui.binder

import PostQuery
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.common.Constant
import cn.lintyone.out.helper.presenter.PostPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drakeet.multitype.ItemViewBinder
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.gone
import com.lxj.androidktx.core.visible
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentBinder<T>(val mPresenter: PostPresenter? = null, val postId: Int? = null) :
    ItemViewBinder<T, CommentBinder.ViewHolder<T>>() {

    class ViewHolder<T>(
        val v: View,
        val mPresenter: PostPresenter? = null,
        val postId: Int? = null
    ) :
        RecyclerView.ViewHolder(v) {
        lateinit var builder: QMUIDialog.EditTextDialogBuilder

        fun loadData(item: T) {
            when (item) {
                is PostQuery.CommentByPost -> {
                    v.mContent.text = item.content()
                    v.mName.text = item.user().nickName() ?: item.user().userName()
                    v.mTime.text = item.createdAt().toString()
                    Glide.with(v)
                        .load(if (item.user().avatar() == null) R.drawable.default_avatar else Constant.SERVER + item.user().avatar())
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(180)))
                        .into(v.mAvatar)
                    v.mComment.click {
                        builder = QMUIDialog.EditTextDialogBuilder(v.context)
                            .setTitle("请输入评论内容")
                            .setCancelable(true)
                            .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                                if (builder.editText.text.isEmpty()) {
                                    mPresenter?.getView()?.toastMessage("评论内容不得为空")
                                } else {
                                    mPresenter?.comment(
                                        postId!!,
                                        builder.editText.text.toString(),
                                        item.id().toInt()
                                    )
                                    dialog.cancel()
                                }
                            })
                        builder.show()
                    }
                    if (item.comment() != null) {
                        v.mYinyong.text =  (item.comment()!!.user().nickName() ?: item.comment()!!.user().userName()) + " : " + item.comment()!!.content()
                        v.mYinYongBox.visible()
                    }
                }
                is CommentsByUserQuery.CommentByUser ->{
                    v.mContent.text = item.content()
                    v.mName.text = item.user().nickName() ?: item.user().userName()
                    v.mTime.text = item.createdAt().toString()
                    Glide.with(v)
                        .load(if (item.user().avatar() == null) R.drawable.default_avatar else Constant.SERVER + item.user().avatar())
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(180)))
                        .into(v.mAvatar)
                    v.mComment.gone()
                    if (item.comment() != null) {
                        v.mYinyong.text =  (item.comment()!!.user().nickName() ?: item.comment()!!.user().userName()) + " : " + item.comment()!!.content()
                        v.mYinYongBox.visible()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, item: T) {
        holder.setIsRecyclable(false)
        holder.loadData(item)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder<T> {
        return ViewHolder(
            inflater.inflate(R.layout.item_comment, parent, false),
            mPresenter,
            postId
        )
    }
}
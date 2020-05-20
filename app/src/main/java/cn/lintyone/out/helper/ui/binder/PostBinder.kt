package cn.lintyone.out.helper.ui.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.common.Constant
import cn.lintyone.out.helper.ui.activity.PostActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drakeet.multitype.ItemViewBinder
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.startActivity
import com.lxj.androidktx.core.visible
import kotlinx.android.synthetic.main.item_post.view.*

class PostBinder : ItemViewBinder<PostsQuery.Post, PostBinder.ViewHolder>() {

    class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        fun loadData(item: PostsQuery.Post) {
            v.mName.text = item.user().nickName() ?: item.user().userName()
            Glide.with(v)
                .load(if (item.user().avatar() == null) R.drawable.default_avatar else Constant.SERVER + item.user().avatar())
                .apply(RequestOptions.bitmapTransform(RoundedCorners(180)))
                .into(v.mAvatar)
            v.mContent.text = item.content()
            v.mTitle.text = item.title()
            v.mCountComment.text = item.countComment().toString()
            v.mTime.text = item.createdAt().toString()
            if (item.image() != null) {
                Glide.with(v)
                    .load(Constant.SERVER + item.image())
                    .into(v.mImage)
                v.mImage.visible()
            }
            v.click {
                v.startActivity<PostActivity>(
                    bundle = arrayOf(
                        "id" to item.id()
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PostsQuery.Post) {
        holder.setIsRecyclable(false)
        holder.loadData(item)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_post, parent, false))
    }
}
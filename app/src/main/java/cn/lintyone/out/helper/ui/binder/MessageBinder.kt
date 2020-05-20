package cn.lintyone.out.helper.ui.binder

import MessageQuery
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lintyone.out.helper.R
import com.drakeet.multitype.ItemViewBinder
import kotlinx.android.synthetic.main.item_message.view.*
import type.MessageType.*

class MessageBinder : ItemViewBinder<MessageQuery.Message, MessageBinder.ViewHolder>() {

    class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        fun loadData(item: MessageQuery.Message) {
            val type = when (item.type()) {
                SYSTEM -> "系统通知"
                ORDER -> "订单通知"
                COMMENT -> "评论通知"
                `$UNKNOWN` -> ""
            }
            v.mType.text = type
            v.mContent.text = item.content()
            v.mTime.text = item.createdAt().toString()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MessageQuery.Message) {
        holder.setIsRecyclable(false)
        holder.loadData(item)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_message, parent, false))
    }
}
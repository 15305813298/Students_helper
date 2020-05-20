package cn.lintyone.out.helper.presenter.view

import com.haoge.easyandroid.mvp.MVPView

interface ListView : MVPView {

    fun onOrder(list: List<OrdersByUserQuery.OrdersByUser>){}

    fun onComment(list: List<CommentsByUserQuery.CommentByUser>){}

    fun onMessage(list: List<MessageQuery.Message>){}

    fun noMore() {}


}
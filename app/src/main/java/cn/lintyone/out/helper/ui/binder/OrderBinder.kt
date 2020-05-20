package cn.lintyone.out.helper.ui.binder

import OrdersQuery
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.common.model.Login
import cn.lintyone.out.helper.presenter.ListPresenter
import cn.lintyone.out.helper.presenter.OrderPresenter
import com.drakeet.multitype.ItemViewBinder
import com.haoge.easyandroid.easy.EasySharedPreferences
import com.haoge.easyandroid.mvp.MVPPresenter
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.invisible
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import kotlinx.android.synthetic.main.item_order.view.*
import type.OrderStatus
import type.OrderType

class OrderBinder<T>(private val mPresenter: MVPPresenter<*>) :
    ItemViewBinder<T, OrderBinder.ViewHolder<T>>() {

    class ViewHolder<T>(val view: View, private val mPresenter: MVPPresenter<*>) :
        RecyclerView.ViewHolder(view) {
        private val login = EasySharedPreferences.load(Login::class.java)
        lateinit var builder: QMUIDialog.EditTextDialogBuilder
        fun setData(item: T) {
            when (item) {
                is OrdersQuery.Order -> {
                    val color = when (item.type()) {
                        OrderType.EXPRESS -> "#6699FF"
                        OrderType.EAT -> "#66FFCC"
                        OrderType.BUY -> "#CC6699"
                        OrderType.RUN -> "#00CC66"
                        OrderType.`$UNKNOWN` -> ""
                    }
                    val name = when (item.type()) {
                        OrderType.EXPRESS -> "代取快递"
                        OrderType.EAT -> "代取餐"
                        OrderType.BUY -> "代购"
                        OrderType.RUN -> "代跑腿"
                        OrderType.`$UNKNOWN` -> ""
                    }
                    view.mTitle.text = Html.fromHtml(
                        "${item.postUser().nickName()
                            ?: item.postUser().userName()} : <font color='$color'>$name</font>" + if (login.id == item.pickUser()?.id()) "[${item.successCode()}]" else ""
                    )
                    if (login.id == item.postUser().id() || login.id == item.pickUser()?.id()) {
                        view.mPick.invisible()
                    } else {
                        view.mPick.click {
                            (mPresenter as OrderPresenter).pick(item.id())
                        }
                    }
                    view.mOffer.text = "感谢费 ${item.offer()} 元"
                    view.mAddress.text = "收件地址: ${item.address().description()}"
                    view.mPickAddress.text = "取件地址: ${item.pickAddress()}"
                    view.mDescription.text = "订单描述: ${item.description()}"
                    view.mSchoolName.text = item.school().name()
                    view.mTime.text = item.createdAt().toString()
                }
                is OrdersByUserQuery.OrdersByUser -> {
                    val color = when (item.type()) {
                        OrderType.EXPRESS -> "#6699FF"
                        OrderType.EAT -> "#66FFCC"
                        OrderType.BUY -> "#CC6699"
                        OrderType.RUN -> "#00CC66"
                        OrderType.`$UNKNOWN` -> ""
                    }
                    val name = when (item.type()) {
                        OrderType.EXPRESS -> "代取快递"
                        OrderType.EAT -> "代取餐"
                        OrderType.BUY -> "代购"
                        OrderType.RUN -> "代跑腿"
                        OrderType.`$UNKNOWN` -> ""
                    }
                    view.mTitle.text = Html.fromHtml(
                        "${item.postUser().nickName()
                            ?: item.postUser().userName()} : <font color='$color'>$name</font>"
                    )
                    if (item.status() != OrderStatus.RECEIVED) {
                        view.mPick.invisible()
                    } else {
                        view.mPick.text = "确认完成任务"
                        view.mPick.click {
                            builder = QMUIDialog.EditTextDialogBuilder(view.context)
                                .setTitle("请输入完成码(向接单人询问)")
                                .setCancelable(true)
                                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                                    if (builder.editText.text.isEmpty()) {
                                        mPresenter.getView().toastMessage("反馈内容不得为空")
                                    } else {
                                        (mPresenter as ListPresenter).orderSuccess(
                                            builder.editText.text.toString(),
                                            item.id().toInt()
                                        )
                                        dialog.cancel()
                                    }
                                })
                            builder.show()
                        }
                    }
                    view.mOffer.text = "感谢费 ${item.offer()} 元"
                    view.mAddress.text = "收件地址: ${item.address().description()}"
                    view.mPickAddress.text = "取件地址: ${item.pickAddress()}"
                    view.mDescription.text = "订单描述: ${item.description()}"
                    view.mSchoolName.text = item.school().name()
                    view.mTime.text = item.createdAt().toString()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, item: T) {
        holder.setIsRecyclable(false)
        holder.setData(item)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder<T> {
        return ViewHolder(inflater.inflate(R.layout.item_order, parent, false), mPresenter)
    }
}
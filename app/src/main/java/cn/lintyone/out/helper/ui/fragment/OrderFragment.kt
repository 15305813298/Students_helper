package cn.lintyone.out.helper.ui.fragment

import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.presenter.HomePresenter
import cn.lintyone.out.helper.presenter.OrderPresenter
import cn.lintyone.out.helper.presenter.view.HomeView
import cn.lintyone.out.helper.presenter.view.OrderView
import cn.lintyone.out.helper.ui.activity.CreateOrderActivity
import cn.lintyone.out.helper.ui.binder.OrderBinder
import com.drakeet.multitype.MultiTypeAdapter
import com.gyf.immersionbar.ImmersionBar
import com.lxj.androidktx.core.startActivity
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.fragment_order.*
import type.OrderInput
import type.OrderType

class OrderFragment : BaseFragment<OrderPresenter>(R.layout.fragment_order), OrderView {
    override fun createPresenter(): OrderPresenter {
        return OrderPresenter(this)
    }

    private val item = ArrayList<Any>()
    private val adapter = MultiTypeAdapter(item)
    private val manager = LinearLayoutManager(context)
    private var page = 0

    override fun init() {
        mRV.layoutManager = manager
        mRV.adapter = adapter
        adapter.register(OrderBinder(mPresenter!!))

        mTitleBar.setListener { v, action, _ ->
            when (action) {
                CommonTitleBar.ACTION_RIGHT_TEXT -> {
                    showMenu(v)
                }
            }
        }
        mRefresh.setOnRefreshListener {
            page = 0
            item.clear()
            adapter.notifyDataSetChanged()
            mPresenter?.orders(page++)
        }
        mRefresh.setOnLoadMoreListener {
            mPresenter?.orders(page++)
        }

        mPresenter?.orders(page++)
    }

    private fun showMenu(view: View) {
        val menu = PopupMenu(view.context, view)
        menu.menuInflater.inflate(R.menu.menu_create_order, menu.menu)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.mExpress -> {
                    view.startActivity<CreateOrderActivity>(
                        bundle = arrayOf(
                            "type" to OrderType.EXPRESS
                        )
                    )
                }
                R.id.mBuy -> {
                    view.startActivity<CreateOrderActivity>(
                        bundle = arrayOf(
                            "type" to OrderType.BUY
                        )
                    )
                }
                R.id.mEat -> {
                    view.startActivity<CreateOrderActivity>(
                        bundle = arrayOf(
                            "type" to OrderType.EAT
                        )
                    )
                }
                R.id.mRun -> {
                    view.startActivity<CreateOrderActivity>(
                        bundle = arrayOf(
                            "type" to OrderType.RUN
                        )
                    )
                }
            }
            false
        }
        menu.show()
    }

    override fun onOrderList(list: OrdersQuery.Data) {
        item.addAll(list.orders())
        adapter.notifyDataSetChanged()
        mRefresh.finishLoadMore(true)
        mRefresh.finishRefresh(true)
        if (list.hasPick()) {
            mTitleBar.centerTextView.text = "所接单"
        } else {
            mTitleBar.centerTextView.text = "接单栏"
        }
    }

    override fun noMore() {
        mRefresh.finishLoadMore(1, true, true)
        mRefresh.finishRefresh(1, true, true)
    }

    override fun onPick() {
        page = 0
        item.clear()
        adapter.notifyDataSetChanged()
        mPresenter?.orders(page++)
    }
}
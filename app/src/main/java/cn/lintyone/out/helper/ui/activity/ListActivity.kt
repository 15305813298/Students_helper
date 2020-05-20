package cn.lintyone.out.helper.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.presenter.ListPresenter
import cn.lintyone.out.helper.presenter.view.ListView
import cn.lintyone.out.helper.ui.binder.CommentBinder
import cn.lintyone.out.helper.ui.binder.MessageBinder
import cn.lintyone.out.helper.ui.binder.OrderBinder
import com.drakeet.multitype.MultiTypeAdapter
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.fragment_order.*

class ListActivity : BaseActivity<ListPresenter>(), ListView {
    override fun createPresenter(): ListPresenter {
        return ListPresenter(this)
    }

    private val item = ArrayList<Any>()
    private val adapter = MultiTypeAdapter(item)
    private val manager = LinearLayoutManager(this)
    private var page = 0
    private var type = ""

    override fun initView() {
        setContentView(R.layout.activity_list)
        mRV.layoutManager = manager
        mRV.adapter = adapter
        adapter.register(OrdersByUserQuery.OrdersByUser::class, OrderBinder(mPresenter!!))
        adapter.register(MessageQuery.Message::class, MessageBinder())
        adapter.register(CommentsByUserQuery.CommentByUser::class, CommentBinder())

        mTitleBar.setListener { v, action, extra ->
            when (action) {
                CommonTitleBar.ACTION_LEFT_BUTTON -> {
                    finish()
                }
            }
        }
        type = intent.getStringExtra("type")

        when (type) {
            "comment" -> {
                mPresenter?.comment(page++)
                mTitleBar.centerTextView.text = "我的评论"
            }
            "message" -> {
                mPresenter?.message(page++)
                mTitleBar.centerTextView.text = "我的消息"
            }
            "order" -> {
                mPresenter?.orderByUser(page++)
                mTitleBar.centerTextView.text = "我的订单"
            }
        }

        mRefresh.setOnLoadMoreListener {
            when (type) {
                "comment" -> mPresenter?.comment(page++)
                "message" -> mPresenter?.message(page++)
                "order" -> mPresenter?.orderByUser(page++)
            }
        }
        mRefresh.setOnRefreshListener {
            page = 0
            item.clear()
            adapter.notifyDataSetChanged()
            when (type) {
                "comment" -> mPresenter?.comment(page++)
                "message" -> mPresenter?.message(page++)
                "order" -> mPresenter?.orderByUser(page++)
            }
        }
    }


    override fun noMore() {
        mRefresh.finishRefresh(1, true, true)
        mRefresh.finishLoadMore(1, true, true)
    }

    override fun onComment(list: List<CommentsByUserQuery.CommentByUser>) {
        item.addAll(list)
        adapter.notifyDataSetChanged()
        mRefresh.finishLoadMore(true)
        mRefresh.finishRefresh(true)
    }

    override fun onOrder(list: List<OrdersByUserQuery.OrdersByUser>) {
        item.addAll(list)
        adapter.notifyDataSetChanged()
        mRefresh.finishLoadMore(true)
        mRefresh.finishRefresh(true)
    }

    override fun onMessage(list: List<MessageQuery.Message>) {
        item.addAll(list)
        adapter.notifyDataSetChanged()
        mRefresh.finishLoadMore(true)
        mRefresh.finishRefresh(true)
    }
}
package cn.lintyone.out.helper.ui.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.presenter.PostPresenter
import cn.lintyone.out.helper.presenter.view.PostView
import cn.lintyone.out.helper.ui.binder.PostBinder
import com.drakeet.multitype.MultiTypeAdapter
import com.haoge.easyandroid.easy.EasyLog
import com.haoge.easyandroid.easy.EasyToast
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.fragment_order.*

class PostFragment : BaseFragment<PostPresenter>(R.layout.fragment_post), PostView {
    override fun createPresenter(): PostPresenter {
        return PostPresenter(this)
    }

    private val item = ArrayList<Any>()
    private val adapter = MultiTypeAdapter(item)
    private val manager = LinearLayoutManager(context)
    private var page = 0
    private var filter = ""

    override fun init() {
        mRV.layoutManager = manager
        mRV.adapter = adapter
        adapter.register(PostBinder())

        mTitleBar.setListener { v, action, extra ->
            when (action) {
                CommonTitleBar.ACTION_SEARCH_VOICE -> {
                    mTitleBar.centerSearchEditText.setText("")
                    filter = ""
                    page = 0
                    item.clear()
                    adapter.notifyDataSetChanged()
                    mPresenter?.posts(page++, filter)
                }
                CommonTitleBar.ACTION_SEARCH_SUBMIT -> {
                    filter = extra
                    page = 0
                    item.clear()
                    adapter.notifyDataSetChanged()
                    mPresenter?.posts(page++, filter)
                }
            }
        }
        mRefresh.setOnRefreshListener {
            page = 0
            item.clear()
            adapter.notifyDataSetChanged()
            mPresenter?.posts(page++, filter)
        }
        mRefresh.setOnLoadMoreListener {
            mPresenter?.posts(page++, filter)
        }

        mPresenter?.posts(page++, filter)
    }

    override fun onLoadPosts(list: List<PostsQuery.Post>) {
        item.addAll(list)
        adapter.notifyDataSetChanged()
        mRefresh.finishRefresh(true)
        mRefresh.finishLoadMore(true)
    }

    override fun noMore() {
        mRefresh.finishRefresh(1, true, true)
        mRefresh.finishLoadMore(1, true, true)
    }

    override fun onRefreshPost() {
        page = 0
        item.clear()
        adapter.notifyDataSetChanged()
        mPresenter?.posts(page++, filter)
    }
}
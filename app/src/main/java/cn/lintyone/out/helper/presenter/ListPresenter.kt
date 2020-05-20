package cn.lintyone.out.helper.presenter

import cn.lintyone.out.helper.api.ApolloFactory
import cn.lintyone.out.helper.common.execute
import cn.lintyone.out.helper.presenter.view.ListView
import com.haoge.easyandroid.mvp.MVPPresenter

class ListPresenter(view: ListView) : MVPPresenter<ListView>(view) {

    fun orderSuccess(code: String, id: Int) {
        val mutate = OrderSuccessMutation.builder()
            .code(code)
            .id(id)
            .build()
        ApolloFactory.instance.mutate(mutate)
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.orderSuccess().toString())
                }
            }
    }

    fun orderByUser(page: Int) {
        ApolloFactory.instance.query(OrdersByUserQuery.builder().page(page).build())
            .execute { ok, res ->
                if (ok) {
                    if (res.data()?.ordersByUser()!!.size <= 0) {
                        getView().noMore()
                        return@execute
                    }
                    getView().onOrder(res.data()?.ordersByUser()!!)
                }
            }
    }

    fun message(page: Int) {
        ApolloFactory.instance.query(MessageQuery.builder().page(page).build())
            .execute { ok, res ->
                if (ok) {
                    if (res.data()?.messages()!!.size <= 0) {
                        getView().noMore()
                        return@execute
                    }
                    getView().onMessage(res.data()?.messages()!!)
                }
            }
    }

    fun comment(page: Int) {
        ApolloFactory.instance.query(CommentsByUserQuery.builder().page(page).build())
            .execute { ok, res ->
                if (ok) {
                    if (res.data()?.commentByUser()!!.size <= 0) {
                        getView().noMore()
                        return@execute
                    }
                    getView().onComment(res.data()?.commentByUser()!!)
                }
            }
    }

}
package cn.lintyone.out.helper.presenter

import cn.lintyone.out.helper.api.ApolloFactory
import cn.lintyone.out.helper.common.execute
import cn.lintyone.out.helper.presenter.view.OrderView
import com.haoge.easyandroid.mvp.MVPPresenter
import type.OrderInput
import type.OrderType

class OrderPresenter(view: OrderView) : MVPPresenter<OrderView>(view) {

    fun createOrder(
        addressId: Int,
        pickAddress: String,
        expressName: String,
        description: String,
        expressCode: String,
        offer: Int,
        type: OrderType
    ) {
        val orderInput = OrderInput.builder()
            .addressId(addressId)
            .pickAddress(pickAddress)
            .expressName(expressName)
            .description(description)
            .expressCode(expressCode)
            .offer(offer)
            .type(type)
            .build()
        val mutate = CreateOrderMutation.builder().orderInput(orderInput)
            .build()
        ApolloFactory.instance.mutate(mutate)
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.createOrder().toString())
                    getView().onCreateOrder()
                }
            }
    }

    fun address(id: Int) {
        val query = AddressQuery.builder().id(if (id == 0) null else id).build()
        ApolloFactory.instance.query(query)
            .execute { ok, res ->
                if (ok) {
                    getView().onAddress(res.data()?.address()!!)
                }
            }
    }

    fun school() {
        ApolloFactory.instance.query(SchoolQuery.builder().build())
            .execute { ok, res ->
                if (ok) {
                    getView().onSchool(res.data()?.school()!!)
                }
            }
    }

    fun orders(page: Int) {
        val query = OrdersQuery.builder()
            .page(page).build()
        ApolloFactory.instance.query(query)
            .execute { ok, res ->
                if (ok) {
                    if (res.data()?.orders()?.size == 0) {
                        getView().noMore()
                        return@execute
                    }
                    getView().onOrderList(res.data()!!)
                }
            }
    }

    fun pick(id: String) {
        val mutate = PickOrderMutation.builder().id(id.toInt()).build()
        ApolloFactory.instance.mutate(mutate)
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.pickOrder().toString())
                    getView().onPick()
                }
            }
    }
}
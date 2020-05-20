package cn.lintyone.out.helper.presenter.view

import com.haoge.easyandroid.mvp.MVPView

interface OrderView : MVPView {

    fun onCreateOrder() {}

    fun onAddress(address: AddressQuery.Address) {}

    fun onSchool(school: SchoolQuery.School) {}

    fun onOrderList(list: OrdersQuery.Data) {}

    fun noMore() {}

    fun onPick() {}
}
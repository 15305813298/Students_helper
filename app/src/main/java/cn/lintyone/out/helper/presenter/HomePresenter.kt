package cn.lintyone.out.helper.presenter

import ChangeCityMutation
import cn.lintyone.out.helper.api.ApolloFactory
import cn.lintyone.out.helper.common.execute
import cn.lintyone.out.helper.presenter.view.HomeView
import com.haoge.easyandroid.mvp.MVPPresenter

class HomePresenter(view: HomeView) : MVPPresenter<HomeView>(view) {

    fun changeCity(city: String) {
        val mutate = ChangeCityMutation.builder().city(city)
            .build()
        ApolloFactory.instance.mutate(mutate)
            .execute { _, _ ->
            }
    }
}
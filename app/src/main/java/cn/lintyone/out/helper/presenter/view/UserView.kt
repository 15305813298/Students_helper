package cn.lintyone.out.helper.presenter.view

import com.haoge.easyandroid.mvp.MVPView

interface UserView : MVPView {

    fun success() {}

    fun schoolList(list : List<SchoolsQuery.School>) {}
}
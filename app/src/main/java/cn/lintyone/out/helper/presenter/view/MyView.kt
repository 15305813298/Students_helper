package cn.lintyone.out.helper.presenter.view

import com.haoge.easyandroid.mvp.MVPView

interface MyView : MVPView {

    fun onUser(user: UserInfoQuery.UserInfo)

    fun onChange()
}
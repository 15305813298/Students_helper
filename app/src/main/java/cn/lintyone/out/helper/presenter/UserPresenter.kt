package cn.lintyone.out.helper.presenter

import SchoolsQuery
import cn.lintyone.out.helper.api.ApolloFactory
import cn.lintyone.out.helper.common.execute
import cn.lintyone.out.helper.common.model.Login
import cn.lintyone.out.helper.presenter.view.UserView
import com.haoge.easyandroid.easy.EasySharedPreferences
import com.haoge.easyandroid.mvp.MVPPresenter
import type.CreateUserInput
import type.LoginInput

class UserPresenter(view: UserView) : MVPPresenter<UserView>(view) {

    private val login = EasySharedPreferences.load(Login::class.java)

    fun schools(filter: String) {
        val query = SchoolsQuery.builder().filter(filter).build()
        ApolloFactory.instance.query(query)
            .execute { ok, res ->
                if (ok) {
                    getView().schoolList(res.data()!!.schools())
                }
            }
    }

    fun register(
        userName: String,
        password: String,
        schoolName: String,
        studentId: String,
        phone: String
    ) {
        val createUserInput = CreateUserInput.builder()
            .userName(userName)
            .password(password)
            .schoolName(schoolName)
            .studentId(studentId)
            .phone(phone)
            .build()

        val mutate = RegisterMutation.builder().createUserInput(createUserInput)
            .build()
        ApolloFactory.instance.mutate(mutate)
            .execute { ok, res ->
                if (ok) {
                    login.token = res.data()?.register()?.token()
                    login.id = res.data()?.register()?.id()
                    login.apply()
                    ApolloFactory.instance.build()
                    getView().success()
                }
            }
    }

    fun login(userName: String, password: String) {
        val loginInput = LoginInput.builder()
            .userName(userName)
            .password(password)
            .build()
        val mutate = LoginMutation.builder().loginInput(loginInput)
            .build()
        ApolloFactory.instance.mutate(mutate)
            .execute { ok, res ->
                if (ok) {
                    login.token = res.data()?.login()?.token()
                    login.id = res.data()?.login()?.id()
                    login.apply()
                    ApolloFactory.instance.build()
                    getView().success()
                }
            }
    }


}
package cn.lintyone.out.helper.presenter

import cn.lintyone.out.helper.api.ApolloFactory
import cn.lintyone.out.helper.api.RetrofitFactory
import cn.lintyone.out.helper.api.Upload
import cn.lintyone.out.helper.common.execute
import cn.lintyone.out.helper.common.okHttpExecute
import cn.lintyone.out.helper.presenter.view.MyView
import com.haoge.easyandroid.mvp.MVPPresenter
import okhttp3.MultipartBody
import type.ForgotPasswordInput

class MyPresenter(view: MyView) : MVPPresenter<MyView>(view) {

    fun userInfo() {
        val query = UserInfoQuery.builder().build()
        ApolloFactory.instance.query(query)
            .execute { ok, res ->
                if (ok) {
                    getView().onUser(res.data()?.userInfo()!!)
                }
            }
    }

    fun feedback(content: String) {
        val mutate = FeedbackMutation.builder()
            .content(content)
            .build()
        ApolloFactory.instance.mutate(mutate)
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.createFeedback().toString())
                }
            }
    }

    fun avatar(avatar: MultipartBody.Part) {
        RetrofitFactory.getInstance().create(Upload::class.java)
            .avatar(avatar)
            .okHttpExecute {
                updateAvatar(it.message!!)
            }
    }

    private fun updateAvatar(avatar: String) {
        ApolloFactory.instance.mutate(ChangeAvatarMutation.builder().avatar(avatar).build())
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.changeAvatar().toString())
                    getView().onChange()
                }
            }
    }

    fun changeName(name: String) {
        ApolloFactory.instance.mutate(ChangeNameMutation.builder().name(name).build())
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.changeName().toString())
                    getView().onChange()
                }
            }
    }

    fun changepwd(password: String) {
        val pwd = ForgotPasswordInput.builder().password(password).build()
        ApolloFactory.instance.mutate(ResetPasswordMutation.builder().forgotPasswordInput(pwd).build())
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.resetPassword().toString())
                    getView().onChange()
                }
            }
    }

    fun addBalance(money: Int) {
        ApolloFactory.instance.mutate(AddBalanceMutation.builder().money(money).build())
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.addBalance().toString())
                    userInfo()
                }
            }
    }

    fun setAddress(address: String) {
        ApolloFactory.instance.mutate(CreateAddressMutation.builder().description(address).build())
            .execute { ok, res ->
                if (ok) {
                    getView().toastMessage(res.data()?.createAddress().toString())
                }
            }
    }
}
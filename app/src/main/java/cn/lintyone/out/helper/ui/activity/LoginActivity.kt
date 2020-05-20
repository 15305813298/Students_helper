package cn.lintyone.out.helper.ui.activity

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.presenter.UserPresenter
import cn.lintyone.out.helper.presenter.view.UserView
import com.gyf.immersionbar.ImmersionBar
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.startActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<UserPresenter>(), UserView {
    override fun createPresenter(): UserPresenter {
        return UserPresenter(this)
    }

    override fun initView() {
        setContentView(R.layout.activity_login)
        ImmersionBar.with(this)
            .statusBarColor("#FFFFFF")
            .statusBarDarkFont(true)
            .keyboardEnable(true)
            .init()
        mRegister.click {
            startActivity<RegisterActivity>(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        mLogin.click {
            if (mUserName.text.isEmpty() || mPassword.text.isEmpty()) {
                toastMessage("用户名密码不得为空")
                return@click
            }
            mPresenter?.login(mUserName.text.toString(), mPassword.text.toString())
        }
    }

    override fun success() {
        toastMessage("登录成功")
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            currentFocus?.windowToken,
            0
        )
        startActivity<MainActivity>(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
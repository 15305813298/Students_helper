package cn.lintyone.out.helper.ui.activity

import SchoolsQuery
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.presenter.UserPresenter
import cn.lintyone.out.helper.presenter.view.UserView
import com.gyf.immersionbar.ImmersionBar
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.startActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity<UserPresenter>(), UserView {
    override fun createPresenter(): UserPresenter {
        return UserPresenter(this)
    }

    override fun initView() {
        setContentView(R.layout.activity_register)
        ImmersionBar.with(this)
            .statusBarColor("#FFFFFF")
            .statusBarDarkFont(true)
            .keyboardEnable(true)
            .init()

        mLogin.click {
            startActivity<LoginActivity>(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        mSchoolName.threshold = 2
        mSchoolName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val current = s.toString()
                if (current.isNotEmpty()) {
                    mPresenter?.schools(current)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mRegister.click {
            if (mUserName.text.isEmpty() || mPassword.text.isEmpty() || mSchoolName.text.isEmpty() || mStudentID.text.isEmpty() || mPhone.text.isEmpty()) {
                toastMessage("信息输入有误")
                return@click
            }
            mPresenter?.register(
                mUserName.text.toString(),
                mPassword.text.toString(),
                mSchoolName.text.toString(),
                mStudentID.text.toString(),
                mPhone.text.toString()
            )
        }
    }

    override fun success() {
        toastMessage("注册成功")
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            currentFocus?.windowToken,
            0
        )
        startActivity<MainActivity>(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun schoolList(list: List<SchoolsQuery.School>) {
        val items = ArrayList<String>()
        for (item in list) {
            items.add(item.name())
        }
        val adapter =
            ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items)
        mSchoolName.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }
}
package cn.lintyone.out.helper.ui.fragment

import UserInfoQuery
import android.content.Intent
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.api.ApolloFactory
import cn.lintyone.out.helper.common.Constant
import cn.lintyone.out.helper.common.model.Login
import cn.lintyone.out.helper.presenter.MyPresenter
import cn.lintyone.out.helper.presenter.view.MyView
import cn.lintyone.out.helper.ui.activity.ListActivity
import cn.lintyone.out.helper.ui.activity.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.haoge.easyandroid.easy.EasyLog
import com.haoge.easyandroid.easy.EasyPhoto
import com.haoge.easyandroid.easy.EasySharedPreferences
import com.haoge.easyandroid.easy.EasyToast
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.startActivity
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.fragment_my.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class MyFragment : BaseFragment<MyPresenter>(R.layout.fragment_my), MyView {
    override fun createPresenter(): MyPresenter {
        return MyPresenter(this)
    }

    private val login = EasySharedPreferences.load(Login::class.java)
    lateinit var builder: QMUIDialog.EditTextDialogBuilder
    lateinit var message: QMUIDialog.MessageDialogBuilder
    var balance = "0"

    override fun init() {
        mExit.click {
            message = QMUIDialog.MessageDialogBuilder(getHostActivity())
                .setMessage("是否退出登录?")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认退出") { _, _ ->
                    login.token = null
                    login.apply()
                    ApolloFactory.instance.build()
                    startActivity<LoginActivity>(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            message.show()
        }
        mFeedback.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("提交反馈")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("反馈内容不得为空")
                    } else {
                        mPresenter?.feedback(builder.editText.text.toString())
                        dialog.cancel()
                    }
                })
            builder.show()
        }
        mChangeUser.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("修改昵称")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认修改") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("昵称不得为空")
                    } else {
                        mPresenter?.changeName(builder.editText.text.toString())
                        dialog.cancel()
                    }
                })
            builder.show()
        }
        mChangepwd.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("修改密码")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认修改") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("密码不得为空")
                    } else {
                        mPresenter?.changepwd(builder.editText.text.toString())
                        dialog.cancel()
                    }
                })
            builder.show()
        }
        mAvatar.click {
            val photo = EasyPhoto()
                .setCrop(true)
            photo.setCallback {
                val requestBody = it.asRequestBody()
                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("avatar", it.name, requestBody)
                    .build()
                mPresenter?.avatar(multipartBody.parts[0])
            }
            photo.setError {
                EasyLog.DEFAULT.e(it)
                EasyToast.DEFAULT.show(it.message)
            }
            photo.selectPhoto(getHostActivity())
        }

        mComment.click {
            startActivity<ListActivity>(bundle = arrayOf("type" to "comment"))
        }
        mMessage.click {
            startActivity<ListActivity>(bundle = arrayOf("type" to "message"))
        }
        mOrder.click {
            startActivity<ListActivity>(bundle = arrayOf("type" to "order"))
        }
        mBalance.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("当前可用余额 $balance 是否充值?")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("充值金额不得为空")
                    } else {
                        var number = 0
                        try {
                            number = builder.editText.text.toString().toInt()
                            if (number < 1) {
                                toastMessage("充值金额输入有误")
                                return@QMUIDialogAction
                            }
                            mPresenter?.addBalance(number)
                        } catch (e: Exception) {
                            toastMessage("充值金额输入有误")
                        }
                        dialog.cancel()
                    }
                })
            builder.show()
        }
        mSetAddress.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("设置地址")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("地址不得为空")
                    } else {
                        mPresenter?.setAddress(builder.editText.text.toString())
                        dialog.cancel()
                    }
                })
            builder.show()
        }

        mPresenter?.userInfo()
    }

    override fun onUser(user: UserInfoQuery.UserInfo) {
        Glide.with(this)
            .load(if (user.avatar() != null) Constant.SERVER + user.avatar() else R.drawable.default_avatar)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(180)))
            .into(mAvatar)
        mNickName.text = user.nickName() ?: user.userName()
        balance = user.balance()
    }

    override fun onChange() {
        mPresenter?.userInfo()
    }


}
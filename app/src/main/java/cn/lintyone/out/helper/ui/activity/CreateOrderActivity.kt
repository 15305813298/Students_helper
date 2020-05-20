package cn.lintyone.out.helper.ui.activity

import AddressQuery
import SchoolQuery
import android.annotation.SuppressLint
import android.content.Context
import android.view.inputmethod.InputMethodManager
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.presenter.OrderPresenter
import cn.lintyone.out.helper.presenter.view.OrderView
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.gone
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import kotlinx.android.synthetic.main.activity_create_order.*
import type.OrderType

class CreateOrderActivity : BaseActivity<OrderPresenter>(), OrderView {
    override fun createPresenter(): OrderPresenter {
        return OrderPresenter(this)
    }

    lateinit var type: OrderType
    var addressId = 0
    var schoolId = 0
    var pickAddress = ""
    var expressName = ""
    var expressCode = ""
    var offer = 0

    lateinit var builder: QMUIDialog.EditTextDialogBuilder


    @SuppressLint("SetTextI18n")
    override fun initView() {
        setContentView(R.layout.activity_create_order)

        type = (intent.extras!!["type"]) as OrderType

        when (type) {
            OrderType.EXPRESS -> {
                mTitleBar.centerTextView.text = "快递代取"
            }
            OrderType.RUN -> {
                mTitleBar.centerTextView.text = "代跑腿"
                mDescription.hint = "请详细说明时间 物品以及 注意事项"
                mExpressName.gone()
                mExpressCode.gone()
            }
            OrderType.BUY -> {
                mTitleBar.centerTextView.text = "代购"
                mDescription.hint = "请详细说明时间 物品以及 注意事项"
                mExpressName.gone()
                mExpressCode.gone()
            }
            OrderType.EAT -> {
                mTitleBar.centerTextView.text = "代取餐"
                mDescription.hint = "请详细说明时间 物品以及 注意事项"
                mExpressName.gone()
                mExpressCode.gone()
            }
            else -> mTitleBar.centerTextView.text = "这是啥?"
        }

        mCreate.click {
            if (!checkData()) {
                toastMessage("信息输入有误哦")
                return@click
            }
            mPresenter?.createOrder(
                addressId,
                pickAddress,
                expressName,
                mDescription.text.toString(),
                expressCode,
                offer,
                type
            )
        }
        mAddress.click {
            toastMessage("请到'我的'页面修改默认地址")
        }
        mPickAddress.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("请输入取件地址")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("取件地址不得为空")
                    } else {
                        pickAddress = builder.editText.text.toString()
                        mPickAddressText.text = builder.editText.text
                        dialog.cancel()
                    }
                })
            builder.show()
        }
        mExpressName.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("请输入快递公司(例如:中通)")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("快递公司不得为空")
                    } else {
                        expressName = builder.editText.text.toString()
                        mExpressNameText.text = builder.editText.text
                        dialog.cancel()
                    }
                })
            builder.show()
        }
        mExpressCode.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("请输入取件码")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("取件码不得为空")
                    } else {
                        expressCode = builder.editText.text.toString()
                        mExpressCodeText.text = builder.editText.text
                        dialog.cancel()
                    }
                })
            builder.show()
        }
        mOffer.click {
            builder = QMUIDialog.EditTextDialogBuilder(getHostActivity())
                .setTitle("请输入爱心小费")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认提交") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("爱心小费不得为空")
                    } else {
                        try {
                            this.offer = builder.editText.text.toString().toInt()
                            if (this.offer < 1) {
                                toastMessage("爱心小费输入有误")
                                this.offer = 0
                                return@QMUIDialogAction
                            }
                            mOfferText.text = builder.editText.text.toString() + " 元"
                            mTotalOfferText.text = builder.editText.text.toString() + " 元"
                        } catch (e: Exception) {
                            toastMessage("爱心小费输入有误")
                        }
                        dialog.cancel()
                    }
                })
            builder.show()
        }

        mPresenter?.address(addressId)
        mPresenter?.school()
    }

    override fun onCreateOrder() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            currentFocus?.windowToken,
            0
        )
        this.finish()
    }

    private fun checkData(): Boolean {
        return if (type == OrderType.EXPRESS) {
            !(addressId == 0 || schoolId == 0 || pickAddress.isEmpty() || expressName.isEmpty() || expressCode.isEmpty() || mDescription.text.isEmpty() || offer == 0)
        } else {
            !(addressId == 0 || schoolId == 0 || mDescription.text.isEmpty() || offer == 0)
        }
    }

    override fun onAddress(address: AddressQuery.Address) {
        this.addressId = address.id().toInt()
        mAddressText.text = address.description()
    }

    override fun onSchool(school: SchoolQuery.School) {
        mSchoolNameText.text = school.name()
        this.schoolId = school.id().toInt()
    }
}
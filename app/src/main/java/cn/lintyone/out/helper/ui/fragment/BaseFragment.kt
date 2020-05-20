package cn.lintyone.out.helper.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.haoge.easyandroid.easy.EasyToast
import com.haoge.easyandroid.mvp.MVPPresenter
import com.haoge.easyandroid.mvp.MVPView

abstract class BaseFragment<out P : MVPPresenter<*>>(private val layout: Int) : Fragment(),
    MVPView {

    private lateinit var mView: View

    val mPresenter: P? by lazy { return@lazy createPresenter() }

    abstract fun createPresenter(): P

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(layout, container, false)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    abstract fun init()

    override fun toastMessage(message: String) {
        EasyToast.DEFAULT.show(message)
    }

    override fun toastMessage(resId: Int) {
        EasyToast.DEFAULT.show(resId)
    }

    override fun getHostActivity(): Activity {
        return this.activity as Activity
    }

    override fun showLoadingDialog() {

    }

    override fun hideLoadingDialog() {
    }
}
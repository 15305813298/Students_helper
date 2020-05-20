package cn.lintyone.out.helper.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.haoge.easyandroid.easy.EasyLog
import com.haoge.easyandroid.easy.EasyToast
import com.haoge.easyandroid.mvp.MVPDispatcher
import com.haoge.easyandroid.mvp.MVPPresenter
import com.haoge.easyandroid.mvp.MVPView

abstract class BaseActivity<out P : MVPPresenter<*>> : AppCompatActivity(), MVPView {

    private val mMvpDispatcher by lazy { return@lazy MVPDispatcher() }

    val mPresenter: P? by lazy { return@lazy createPresenter() }

    abstract fun createPresenter(): P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mPresenter != null) {
            mMvpDispatcher.addPresenter(mPresenter!!)
        }
        mMvpDispatcher.dispatchOnCreate(intent?.extras)
        initView()
    }

    override fun onStart() {
        super.onStart()
        mMvpDispatcher.dispatchOnStart()
    }

    override fun onResume() {
        super.onResume()
        mMvpDispatcher.dispatchOnResume()
    }

    override fun onPause() {
        super.onPause()
        mMvpDispatcher.dispatchOnPause()
    }

    override fun onStop() {
        super.onStop()
        mMvpDispatcher.dispatchOnStop()
    }

    override fun onRestart() {
        super.onRestart()
        mMvpDispatcher.dispatchOnRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMvpDispatcher.dispatchOnDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mMvpDispatcher.dispatchOnActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMvpDispatcher.dispatchOnSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mMvpDispatcher.dispatchOnRestoreInstanceState(savedInstanceState)
    }

    override fun showLoadingDialog() {

    }

    override fun hideLoadingDialog() {
    }

    override fun getHostActivity(): Activity {
        return this
    }

    override fun toastMessage(message: String) {
        EasyToast.DEFAULT.show(message)
    }

    override fun toastMessage(resId: Int) {
        EasyToast.DEFAULT.show(resId)
    }

    abstract fun initView()

    fun log(s: Any) {
        EasyLog.DEFAULT.d(s)
    }

}
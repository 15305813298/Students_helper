package cn.lintyone.out.helper

import android.app.Application
import com.haoge.easyandroid.EasyAndroid
import com.lxj.androidktx.AndroidKtxConfig
import io.reactivex.disposables.CompositeDisposable

class App : Application() {

    companion object {
        val compositeDisposable = CompositeDisposable()
    }


    override fun onCreate() {
        super.onCreate()

        EasyAndroid.init(this)
        AndroidKtxConfig.init(context = applicationContext)
    }
}
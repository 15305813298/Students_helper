package cn.lintyone.out.helper.common

import cn.lintyone.out.helper.App
import com.apollographql.apollo.api.Response
import com.haoge.easyandroid.easy.EasyLog
import com.haoge.easyandroid.easy.EasyToast
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * 对[Disposable]新增[addTo]函数,用户绑定[CompositeDisposable],统一管理Rx,减少内存泄露的问题
 */
fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

/**
 * 对[Single]类型增加[execute]函数,用于快速设定RxObservable订阅线程
 */
fun <T> Single<T>.execute(call: (ok: Boolean, res: T) -> Unit) =
    this.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe({
        if ((it as Response<*>).hasErrors()) {
            EasyLog.DEFAULT.e(it)
            var err = ""
            for (error in it.errors()) {
                err += error.message()?.substringAfter(":") + ","
            }
            EasyToast.DEFAULT.show(err)
            call(false, it)
        } else {
            call(true, it)
        }
    }, {
        EasyLog.DEFAULT.e(it)
    }).addTo(App.compositeDisposable)


/**
 * 对[Observable]类型增加[execute]函数,用于快速设定RxObservable订阅线程
 */
fun <T> Observable<T>.execute(call: (ok: Boolean, res: T) -> Unit) =
    this.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe({
            if ((it as Response<*>).hasErrors()) {
                EasyLog.DEFAULT.e(it)
                var err = ""
                for (error in it.errors()) {
                    err += error.message()?.substringAfter(":") + ","
                }
                EasyToast.DEFAULT.show(err)
                call(false, it)
            } else {
                call(true, it)
            }
        }, {
            EasyLog.DEFAULT.e(it)
        }).addTo(App.compositeDisposable)


fun <T> Observable<T>.okHttpExecute(call: (res: T) -> Unit) {
    this.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe({
            call(it)
        }, {
            EasyLog.DEFAULT.e(it)
        }).addTo(App.compositeDisposable)
}

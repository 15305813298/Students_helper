package cn.lintyone.out.helper.ui.fragment

import android.graphics.Typeface
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.common.model.Ip
import cn.lintyone.out.helper.common.model.Login
import cn.lintyone.out.helper.common.weather.Weather
import cn.lintyone.out.helper.presenter.HomePresenter
import cn.lintyone.out.helper.presenter.view.HomeView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.haoge.easyandroid.easy.EasyLog
import com.haoge.easyandroid.easy.EasySharedPreferences
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.runOnUIThread
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.EditTextDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import java.io.IOException

class HomeFragment : BaseFragment<HomePresenter>(R.layout.fragment_home), HomeView {
    override fun createPresenter(): HomePresenter {
        return HomePresenter(this)
    }

    private val login = EasySharedPreferences.load(Login::class.java)
    private val client = OkHttpClient()
    lateinit var builder: EditTextDialogBuilder

    override fun init() {
        val font = Typeface.createFromAsset(getHostActivity().assets, "myfont.ttf")
        mTmp.typeface = font
        mWeatherText.typeface = font
        mRefresh.click {
            mRefresh.animate().rotation(360f).duration = 500
            getWeather()
        }
        mCityBtn.click {
            builder = EditTextDialogBuilder(getHostActivity())
                .setTitle("修改城市")
                .setCancelable(true)
                .addAction(QMUIDialogAction("确认修改") { dialog, _ ->
                    if (builder.editText.text.isEmpty()) {
                        toastMessage("城市不得为空")
                    } else {
                        mPresenter?.changeCity(builder.editText.text.toString())
                        login.city = builder.editText.text.toString()
                        login.apply()
                        getWeather()
                        dialog.cancel()
                    }
                })
            builder.show()
        }
        mLocate.click {
            QMUIDialog.MessageDialogBuilder(getHostActivity())
                .setMessage("是否使用网络位置?")
                .addAction(QMUIDialogAction("取消") { dialog, _ ->
                    dialog.cancel()
                })
                .addAction(QMUIDialogAction("确定") { dialog, _ ->
                    login.city = null
                    login.apply()
                    getWeather()
                    dialog.cancel()
                })
                .show()
        }
        getWeather()
    }

    fun getWeather() {
        if (login.city == null) {
            val request = Request.Builder()
                .url("https://tianqiapi.com/api?version=v61&appid=26613674&appsecret=eAIlg4Mc")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    toastMessage("城市数据请求错误")
                }

                override fun onResponse(call: Call, response: Response) {
                    val res = response.body?.string()
                    val ip = Gson().fromJson<Ip>(res, Ip::class.java)
                    login.city = ip.city
                    login.apply()
                    getWeather()
                }
            })
        } else {
            if (login.token != null) {
                mPresenter?.changeCity(login.city!!)
            }
            runOnUIThread {
                mCity.text = login.city
            }
            val request = Request.Builder()
                .url("https://way.jd.com/he/freeweather?appkey=3d0f98e1a07d213c075c5c0cf44be1e4&city=" + login.city)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    toastMessage("天气数据请求错误")
                }

                override fun onResponse(call: Call, response: Response) {
                    val res = response.body?.string()
                    val gson = Gson()
                    try {
                        val weather = gson.fromJson<Weather>(res, Weather::class.java)
                        val today = weather.result.heWeather5[0].daily_forecast[1]
                        val weatherText =
                            if (today.cond.txt_d != today.cond.txt_n) today.cond.txt_d + "转" + today.cond.txt_n else today.cond.txt_d
                        val weatherImage = when (today.cond.txt_d) {
                            "暴雪" -> R.drawable.baoxue
                            "暴雨" -> R.drawable.baoyu
                            "大暴雨" -> R.drawable.dabaoyu
                            "大雪" -> R.drawable.daxue
                            "大雨" -> R.drawable.dayu
                            "冻雨" -> R.drawable.dongyu
                            "多云" -> R.drawable.duoyun
                            "雷阵雨" -> R.drawable.leizhenyu
                            "雷阵雨伴有冰雹" -> R.drawable.leizhenyubanyoubingbao
                            "晴" -> R.drawable.qing
                            "特大暴雨" -> R.drawable.tedabaoyu
                            "雾" -> R.drawable.wu
                            "小雨" -> R.drawable.xiaoyu
                            "小雪" -> R.drawable.xiaoxue
                            "阴" -> R.drawable.yintian
                            "雨夹雪" -> R.drawable.yujiaxue
                            "阵雨" -> R.drawable.zhenyu
                            "中雪" -> R.drawable.zhongxue
                            "中雨" -> R.drawable.zhongyu
                            else -> R.drawable.wu
                        }
                        val tmp = today.tmp.min + " ~ " + today.tmp.max + " °C"
                        runOnUIThread {
                            Glide.with(this@HomeFragment)
                                .load(weatherImage)
                                .into(mWeatherImg)
                            mWeatherText.text = weatherText
                            mTmp.text = tmp
                        }
                        EasyLog.DEFAULT.d(weatherText)
                    } catch (e: Exception) {
                        toastMessage("天气数据请求错误")
                    }
                }
            })
        }
    }


}
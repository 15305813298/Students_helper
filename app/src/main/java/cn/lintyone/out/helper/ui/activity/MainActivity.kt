package cn.lintyone.out.helper.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.common.model.Login
import cn.lintyone.out.helper.ui.fragment.*
import com.gyf.immersionbar.ImmersionBar
import com.haoge.easyandroid.easy.*
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.gone
import com.lxj.androidktx.core.startActivity
import com.lxj.androidktx.core.visible
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val login = EasySharedPreferences.load(Login::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EasyLog.DEFAULT.d(login)
        if (login.token.isNullOrEmpty()) {
            startActivity<LoginActivity>(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return
        }
        permission()


        val fragmentList = arrayListOf(
            HomeFragment(),
            OrderFragment(),
            PostFragment(),
            MyFragment()
        )
        mVp.adapter = VpAdapter(supportFragmentManager, fragmentList)
        mVp.offscreenPageLimit = 4
        mNav.setupWithViewPager(mVp)
        ImmersionBar.with(this@MainActivity)
            .statusBarColor("#41cbff")
            .statusBarDarkFont(false)
            .init()
        mNav.itemTextColor = ContextCompat.getColorStateList(
            this@MainActivity,
            R.color.anti_color_bottom_nav
        )
        mNav.itemIconTintList = ContextCompat.getColorStateList(
            this@MainActivity,
            R.color.anti_color_bottom_nav
        )
        mNav.setBackgroundColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.home_nav_background
            )
        )
        mVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        ImmersionBar.with(this@MainActivity)
                            .statusBarColor("#41cbff")
                            .statusBarDarkFont(false)
                            .init()
                        mNav.itemTextColor = ContextCompat.getColorStateList(
                            this@MainActivity,
                            R.color.anti_color_bottom_nav
                        )
                        mNav.itemIconTintList = ContextCompat.getColorStateList(
                            this@MainActivity,
                            R.color.anti_color_bottom_nav
                        )
                        mNav.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.home_nav_background
                            )
                        )
                        mEdit.gone()
                    }
                    1 -> {
                        ImmersionBar.with(this@MainActivity)
                            .statusBarColor("#FFFFFF")
                            .statusBarDarkFont(true)
                            .init()
                        mNav.itemTextColor = ContextCompat.getColorStateList(
                            this@MainActivity,
                            R.color.color_bottom_nav
                        )
                        mNav.itemIconTintList = ContextCompat.getColorStateList(
                            this@MainActivity,
                            R.color.color_bottom_nav
                        )
                        mNav.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.qmui_config_color_white
                            )
                        )
                        mEdit.gone()
                    }
                    2 -> {
                        ImmersionBar.with(this@MainActivity)
                            .statusBarColor("#FFFFFF")
                            .statusBarDarkFont(true)
                            .init()
                        mNav.itemTextColor = ContextCompat.getColorStateList(
                            this@MainActivity,
                            R.color.color_bottom_nav
                        )
                        mNav.itemIconTintList = ContextCompat.getColorStateList(
                            this@MainActivity,
                            R.color.color_bottom_nav
                        )
                        mNav.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.qmui_config_color_white
                            )
                        )
                        mEdit.visible()
                    }
                    3 -> {
                        ImmersionBar.with(this@MainActivity)
                            .statusBarColor("#FFFFFF")
                            .statusBarDarkFont(true)
                            .init()
                        mNav.itemTextColor = ContextCompat.getColorStateList(
                            this@MainActivity,
                            R.color.color_bottom_nav
                        )
                        mNav.itemIconTintList = ContextCompat.getColorStateList(
                            this@MainActivity,
                            R.color.color_bottom_nav
                        )
                        mNav.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.qmui_config_color_white
                            )
                        )
                        mEdit.gone()
                    }
                }
            }
        })
        mEdit.click {
            startActivity<CreatePostActivity>()
        }
    }

    private fun permission() {
        EasyPermissions.create(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA
        ).rational { permission, chain ->
            val message = "应用需要此权限：$permission"
            AlertDialog.Builder(this)
                .setTitle("权限申请说明")
                .setMessage(message)
                .setNegativeButton("拒绝") { _, _ -> chain.cancel() }
                .setPositiveButton("统一") { _, _ -> chain.process() }
                .show()
            true
        }.callback { grant ->
            if (!grant) {
                EasyToast.DEFAULT.show("权限申请失败，应用退出")
                finish()
            }
        }.alwaysDenyNotifier(object : PermissionAlwaysDenyNotifier() {
            override fun onAlwaysDeny(permissions: Array<String>, activity: Activity) {
                var message = "权限已被默认拒绝，请您前往设置打开"
                message += "\n\n"
                EasyPermissions.getPermissionGroupInfos(permissions, activity).forEach {
                    message += "${it.label} : ${it.desc} \n"
                }
                AlertDialog.Builder(activity)
                    .setTitle("权限申请说明")
                    .setMessage(message)
                    .setPositiveButton("确定") { _, _ -> goSetting(activity) }
                    .setNegativeButton("取消") { _, _ -> cancel(activity) }
                    .show()
            }
        }).request(this)
    }

    private class VpAdapter(
        fragmentManager: FragmentManager,
        private val fragmentList: List<BaseFragment<*>>
    ) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(index: Int): BaseFragment<*> {
            return fragmentList[index]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }


    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 1000) {
                EasyToast.DEFAULT.show("再按一次退出")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                exitProcess(0)
            }
            return true
        }
        return false
    }

}

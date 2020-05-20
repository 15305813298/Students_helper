package cn.lintyone.out.helper.ui.activity

import android.content.Context
import android.view.inputmethod.InputMethodManager
import cn.lintyone.out.helper.R
import cn.lintyone.out.helper.common.Constant
import cn.lintyone.out.helper.presenter.PostPresenter
import cn.lintyone.out.helper.presenter.view.PostView
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.haoge.easyandroid.easy.EasyLog
import com.haoge.easyandroid.easy.EasyPhoto
import com.haoge.easyandroid.easy.EasyToast
import com.lxj.androidktx.core.click
import com.lxj.androidktx.core.gone
import com.lxj.androidktx.core.visible
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.activity_create_order.mTitleBar
import kotlinx.android.synthetic.main.activity_create_post.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class CreatePostActivity : BaseActivity<PostPresenter>(), PostView {
    override fun createPresenter(): PostPresenter {
        return PostPresenter(this)
    }

    private var image = ""

    override fun initView() {
        setContentView(R.layout.activity_create_post)
        ImmersionBar.with(this)
            .keyboardEnable(true)
            .init()

        mTitleBar.setListener { v, action, extra ->
            when (action) {
                CommonTitleBar.ACTION_LEFT_BUTTON -> {
                    this.finish()
                }
                CommonTitleBar.ACTION_RIGHT_TEXT -> {
                    if (mTitle.text.isEmpty() || mContent.text.isEmpty()) {
                        toastMessage("标题和内容不得为空")
                        return@setListener
                    }
                    mPresenter?.createPost(mTitle.text.toString(), mContent.text.toString(), image)
                }
            }
        }
        mAddImage.click {
            val photo = EasyPhoto()
                .setCrop(true)
            photo.setCallback {
                val requestBody = it.asRequestBody()
                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("avatar", it.name, requestBody)
                    .build()
                mPresenter?.uploadImage(multipartBody.parts[0])
            }
            photo.setError {
                EasyLog.DEFAULT.e(it)
                EasyToast.DEFAULT.show(it.message)
            }
            photo.selectPhoto(getHostActivity())
        }

        mRemoveImage.click {
            mImageBox.gone()
            image = ""
        }

    }

    override fun onUploadImage(image: String) {
        this.image = image
        Glide.with(this)
            .load(Constant.SERVER + image)
            .into(mImageView)
        mImageBox.visible()
    }

    override fun onCreatePost() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            currentFocus?.windowToken,
            0
        )
        this.finish()
    }
}
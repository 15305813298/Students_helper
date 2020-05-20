package cn.lintyone.out.helper.common.model

import com.haoge.easyandroid.easy.PreferenceRename
import com.haoge.easyandroid.easy.PreferenceSupport

@PreferenceRename("login_user")
class Login : PreferenceSupport() {
    var id: String? = null
    var token: String? = null
    var city: String? = null
}
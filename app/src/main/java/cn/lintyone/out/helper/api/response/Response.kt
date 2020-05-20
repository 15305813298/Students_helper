package cn.lintyone.out.helper.api.response

class Response<T> {
    var message: String? = null
    var code = 0
    var data: T? = null
}

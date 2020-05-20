package cn.lintyone.out.helper.api

import cn.lintyone.out.helper.api.response.Response
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface Upload {

    @Multipart
    @PUT("upload/avatar")
    fun avatar(@Part avatar: MultipartBody.Part): Observable<Response<String>>

    @Multipart
    @PUT("upload/image")
    fun image(@Part image: MultipartBody.Part): Observable<Response<String>>

}
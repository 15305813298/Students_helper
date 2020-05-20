package cn.lintyone.out.helper.api

import cn.lintyone.out.helper.common.Constant
import cn.lintyone.out.helper.common.model.Login
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.*
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.CacheKeyResolver
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.apollographql.apollo.rx2.rxMutate
import com.apollographql.apollo.rx2.rxQuery
import com.haoge.easyandroid.easy.EasySharedPreferences
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApolloFactory private constructor() {

    companion object {
        val instance: ApolloFactory by lazy { ApolloFactory() }
    }

    private lateinit var apolloClient: ApolloClient
    private val user = EasySharedPreferences.load(Login::class.java)

    init {
        build()
    }

    /**
     * [D]为[Operation.Data]类型
     * 用于构建基于Rx的[ApolloClient.mutate]
     * 返回类型为[Single]
     */
    fun <D : Operation.Data> mutate(data: Mutation<D, D, *>): Single<Response<D>> {
        return instance.apolloClient.rxMutate(data)
    }

    /**
     * [D]为[Operation.Data]类型
     * 用于构建基于Rx的[ApolloClient.query]
     * 返回类型为[Observable]
     */
    fun <D : Operation.Data> query(data: Query<D, D, *>): Observable<Response<D>> {
        return instance.apolloClient.rxQuery(data)
    }

    /**
     * 构建Apollo客户端
     */
    fun build() {
        apolloClient = ApolloClient.builder()
            .serverUrl("${Constant.SERVER}/graphql")
            .okHttpClient(initOkHttpClient())
//            .normalizedCache(initCacheFactory(), initCacheKeyResolver())
            .build()
    }

    /**
     * 初始化OkHttp客户端
     */
    private fun initOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val requestBuilder = it.request()
                    .newBuilder()
                    .addHeader("charset", "utf-8")
                user.token?.apply {
                    requestBuilder.addHeader("token", this)
                }
                val request = requestBuilder.build()
                it.proceed(request)
            }
            .addInterceptor(initLogInterceptor())
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 初始化OkHttp日志拦截器
     */
    private fun initLogInterceptor() =
        HttpLoggingInterceptor().apply { this.level = HttpLoggingInterceptor.Level.BODY }


    /**
     * Apollo缓存
     */
    private fun initCacheFactory(): LruNormalizedCacheFactory =
        LruNormalizedCacheFactory(EvictionPolicy.builder().maxSizeBytes(10 * 1024).build())


    /**
     * 缓存匹配规则
     */
    private fun initCacheKeyResolver(): CacheKeyResolver {
        return object : CacheKeyResolver() {
            override fun fromFieldRecordSet(
                field: ResponseField,
                recordSet: MutableMap<String, Any>
            ): CacheKey {
                if (recordSet.containsKey("id")) {
                    val id = recordSet["id"].toString()
                    return CacheKey.from(id)
                }
                return CacheKey.NO_KEY
            }

            override fun fromFieldArguments(
                field: ResponseField,
                variables: Operation.Variables
            ): CacheKey {
                return CacheKey.NO_KEY
            }
        }

    }


}

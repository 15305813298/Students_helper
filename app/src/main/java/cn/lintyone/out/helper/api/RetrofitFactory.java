package cn.lintyone.out.helper.api;

import com.haoge.easyandroid.easy.EasySharedPreferences;

import java.util.concurrent.TimeUnit;

import cn.lintyone.out.helper.common.Constant;
import cn.lintyone.out.helper.common.model.Login;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() {
        build();
    }

    private static RetrofitFactory instance;

    private Login login = EasySharedPreferences.load(Login.class);

    private static Retrofit retrofit;

    public static synchronized RetrofitFactory getInstance() {
        if (instance == null) {
            instance = new RetrofitFactory();
        }
        return instance;
    }

    private OkHttpClient initOkHttp() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "application/json");
                    if (login.getToken() != null) {
                        requestBuilder.addHeader("token", login.getToken());
                    }
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .addInterceptor(initLogInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    private Interceptor initLogInterceptor() {
        return new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public void build() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.SERVER + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(initOkHttp())
                .build();
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }


}

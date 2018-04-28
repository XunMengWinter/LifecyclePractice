package top.wefor.lifecyclepractice.http;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2018/4/18.
 *
 * @author ice
 */
public class Network {
    public static final String GANK = "http://gank.io/";

    public static ApiService getApiService() {
        return getRetrofit(GANK).build().create(ApiService.class);
    }

    private static Retrofit.Builder getRetrofit(String baseUrl) {
        return new Retrofit.Builder().client(new OkHttpClient().newBuilder().build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));
    }

}

package top.wefor.lifecyclepractice.http;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import top.wefor.lifecyclepractice.model.GankMeizhi;

/**
 * Created on 2018/4/18.
 *
 * @author ice
 */
public class MyApi implements ApiService {
    public static final String GANK = "http://gank.io/";

    public static ApiService getApiService() {
        return getRetrofit(GANK).build().create(ApiService.class);
    }

    public static Retrofit.Builder getRetrofit(String baseUrl) {
        return new Retrofit.Builder().client(new OkHttpClient().newBuilder().build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    @Override
    public Observable<GankMeizhi> getGankMeizhi(int count) {
        return getApiService()
                .getGankMeizhi(count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

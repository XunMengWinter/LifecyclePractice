package top.wefor.lifecyclepractice.http;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import top.wefor.lifecyclepractice.model.GankMeizhi;

/**
 * Created on 2018/4/18.
 *
 * @author ice
 */
public interface ApiService {
    @GET("/api/random/data/福利/{count}")
    Observable<GankMeizhi> getGankMeizhi(@Path("count") int count);
}

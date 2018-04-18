package top.wefor.lifecyclepractice.rx;

import android.util.Log;

import io.reactivex.Observer;

/**
 * 一个需要在回调时判断生命周期是否安全的RxObserver。
 * Created on 2018/4/18.
 *
 * @author ice
 */
@Deprecated
public abstract class OldObserver<T> implements Observer<T> {

    public static final String TAG = "OldObserver";

    public OldObserver() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        onEnd();
    }

    @Override
    public void onComplete() {
        onEnd();
    }

    protected void onEnd() {
        Log.i(TAG, "onEnd");
    }

}

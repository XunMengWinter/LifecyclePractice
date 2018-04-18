package top.wefor.lifecyclepractice.rx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created on 2018/4/18.
 *
 * @author ice
 */
public abstract class SafeObserver<T> implements LifecycleObserver, Observer<T> {

    public static final String TAG = "SafeObserver";

    private Lifecycle mLifecycle;
    private Disposable mDisposable;


    public SafeObserver() {
        //unSafe
    }

    public SafeObserver(Lifecycle lifecycle) {
        //lifecycle safe.
        mLifecycle = lifecycle;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        if (mLifecycle != null)
            mLifecycle.addObserver(this);
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
        onDisPose();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDisPose() {
        Log.i(TAG, "dispose");
        if (mLifecycle != null)
            mLifecycle.removeObserver(this);
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }
}

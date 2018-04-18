### Lifecycle简介
官方简介如此：
> Lifecycle-aware components perform actions in response to a change in the lifecycle status of another component, such as activities and fragments. These components help you produce better-organized, and often lighter-weight code, that is easier to maintain.

生命周期感知组件可以响应另一个组件生命周期的变化（例如Activity和Fragment的生命周期状态更改）。 这些(实现了Lifecyc的)组件可帮助你构建组织性更好、更轻、更易于的代码。

听起来很强大吧，先来来看看使用效果吧。

### Lifecycle网络回调示例
常规RxJava&Retrofit网络回调：
```
    @Deprecated
    private void getData(){
        new MyApi().getGankMeizhi(20)
                .subscribe(new OldObserver<GankMeizhi>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //需记录然后手动释放该Disposable。
                    }

                    @Override
                    public void onNext(GankMeizhi gankMeizhi) {
                        //需判断是否可以安全地执行UI刷新。
                        if (isFinishing()){
                            return;
                        }
                        if (!gankMeizhi.error) {
                            mMyAdapter.refreshData(gankMeizhi);
                        }
                    }
                });
    }
```
实现了Lifecycle的RxJava&Retrofit网络安全回调：
```
    private void getDataSafe() {
        new MyApi().getGankMeizhi(20)
                .subscribe(new SafeObserver<GankMeizhi>(getLifecycle()) {
                    @Override
                    public void onNext(GankMeizhi gankMeizhi) {
                        //可以安全地执行UI操作。
                        if (!gankMeizhi.error) {
                            mMyAdapter.refreshData(gankMeizhi);
                        }
                    }
                });
    }
```
可以发现，结合Lifecycle，回调不仅简洁，而且更为安全。Lifecycle可以让开发者更专注地实现功能而非小心翼翼地做生命周期判断。
### 如何实现
首先，我们Lifecycle库引入到项目。在app模块build.gradle文件dependencies内添加以下代码即可：
```
    // Java8 support for Lifecycles
    implementation 'android.arch.lifecycle:common-java8:1.1.1'
```

接着，我们为自定义的RxObserver实现LifecycleObserver接口：
```
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 一个生命周期安全的RxObserver。
 * Created on 2018/4/18.
 *
 * @author ice
 */
public abstract class SafeObserver<T> implements LifecycleObserver, Observer<T> {

    public static final String TAG = "SafeObserver";

    private Lifecycle mLifecycle;
    private Disposable mDisposable;


    /*不传入Lifecycle，手动管理该Observer的生命周期*/
    public SafeObserver() {
        //unSafe
    }

    /*传入Lifecycle，自动保证回调安全*/
    public SafeObserver(Lifecycle lifecycle) {
        //lifecycle safe.
        mLifecycle = lifecycle;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        if (mLifecycle != null)
            mLifecycle.addObserver(this); //加入到lifecycle观察者。
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
        onDisPose(); //RxObserver结束后自动释放。
    }

    /*释放Rx观察者（在Lifecycle的ON_DESTROY事件发生时会自动调用该方法）*/
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDisPose() {
        Log.i(TAG, "dispose");
        if (mLifecycle != null)
            mLifecycle.removeObserver(this);//解除与lifecycle的绑定。
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }
}
```
然后，我们需要为Activity(或Fragment)实现LifecycleOwner接口(AppCompatActivity与v4Fragment已实现)。
我们需要在Activity(或Fragment)生命周期中处理Lifecycle事件（在Base类里做一次即可）：
```
    //AppCompatActivity与v4Fragment自带LifecycleRegistry，所以通过强转获取即可。
    protected LifecycleRegistry mLifecycleRegistry = (LifecycleRegistry) getLifecycle();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //...
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    protected void onPause() {
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }

    public void handleLifecycleEvent(@NonNull Lifecycle.Event event) {
        mLifecycleRegistry.handleLifecycleEvent(event);
    }
```
好了，大功告成！这样一来，订阅该lifecycle的观察者(比如前面的SafeObserver)就能接收到该lifecycle对应Activity(或Fragment)生命周期的回调了。

### 相关资料
本文Demo: [LifecyclePractice](https://github.com/XunMengWinter/LifecyclePractice)

使用了Lifecycle的一个轻图文App: [Now](https://github.com/XunMengWinter/Now)

Lifecycle官方文档: [Handling Lifecycles with Lifecycle-Aware Components](https://developer.android.com/topic/libraries/architecture/lifecycle.html)



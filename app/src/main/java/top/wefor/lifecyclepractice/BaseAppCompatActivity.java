package top.wefor.lifecyclepractice;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created on 2018/4/18.
 *
 * @author ice
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    protected LifecycleRegistry mLifecycleRegistry = (LifecycleRegistry) getLifecycle();

    protected abstract int getLayoutId();

    protected abstract void initView(@Nullable Bundle savedInstanceState);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutId());
        this.initView(savedInstanceState);
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

}

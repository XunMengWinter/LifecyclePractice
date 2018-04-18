package top.wefor.lifecyclepractice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class MainActivity extends BaseAppCompatActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

    }

    public void goGank(View view) {
        startActivity(GankMeizhiActivity.newIntent(this));
    }
}

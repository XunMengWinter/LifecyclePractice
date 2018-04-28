package top.wefor.lifecyclepractice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import top.wefor.lifecyclepractice.http.Network;
import top.wefor.lifecyclepractice.model.GankMeizhi;
import top.wefor.lifecyclepractice.rx.OldObserver;
import top.wefor.lifecyclepractice.rx.SafeObserver;

/**
 * Created on 2018/4/18.
 *
 * @author ice
 */
public class GankMeizhiActivity extends BaseAppCompatActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, GankMeizhiActivity.class);
        return intent;
    }

    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gank_meizhi;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mMyAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mMyAdapter);
        getDataSafe();

        setTitle(getTitle() + " - Meizhi");
    }

    private void getDataSafe() {
        Network.getApiService().getGankMeizhi(20)
                .observeOn(AndroidSchedulers.mainThread())
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

    @Deprecated
    private void getData() {
        Network.getApiService().getGankMeizhi(20)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OldObserver<GankMeizhi>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //需记录然后手动释放该Disposable。
                    }

                    @Override
                    public void onNext(GankMeizhi gankMeizhi) {
                        //需判断是否可以安全地执行UI刷新。
                        if (isFinishing()) {
                            return;
                        }
                        if (!gankMeizhi.error) {
                            mMyAdapter.refreshData(gankMeizhi);
                        }
                    }
                });
    }

    private static class MyAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private GankMeizhi mGankMeizhi;

        public MyAdapter(Context context) {
            mContext = context;
        }

        public void refreshData(GankMeizhi gankMeizhi) {
            mGankMeizhi = gankMeizhi;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_meizhi, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (mGankMeizhi == null) {
                return;
            }
            GankMeizhi.ResultsBean resultsBean = mGankMeizhi.results.get(position);
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.mTextView.setText(resultsBean.desc);
            Glide.with(mContext)
                    .load(resultsBean.url)
                    .into(myViewHolder.mImageView);
        }

        @Override
        public int getItemCount() {
            if (mGankMeizhi == null)
                return 0;
            else
                return mGankMeizhi.results.size();
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.meizhi_iv);
            mTextView = itemView.findViewById(R.id.desc_tv);
        }
    }

}

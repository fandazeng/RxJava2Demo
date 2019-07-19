package zeng.fanda.com.rxjava2demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author 曾凡达
 * @date 2019/7/9
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String INTENT_KEY = "Data";

    protected final String TAG = getClass().getName() + "_TAG";

    protected Observer mObserver;

    protected CompositeDisposable mCompositeDisposable;

    @Nullable
    @BindView(R.id.tv_request)
    TextView mRequestData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayoutId());
        ButterKnife.bind(this);
        if (mRequestData != null) {
            mRequestData.setText(getIntent().getStringExtra(INTENT_KEY));
        }
        mCompositeDisposable = new CompositeDisposable();
        createObserver();
        initData();
    }

    protected abstract int initLayoutId();

    protected abstract void initData();

    private void createObserver() {
        mObserver = new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "订阅线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(Object o) {
                Log.d(TAG, "收到消息==" + o + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "错误:" + e.getMessage() + " == 错误线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "完成" + " == 完成线程为：" + Thread.currentThread().getName());
            }
        };
    }

    protected void addDispose(Disposable... disposable) {
        for (Disposable d : disposable) {
            mCompositeDisposable.add(d);
        }
    }

    protected void clearDispose() {
        mCompositeDisposable.clear();
    }

    protected void deletedispose(Disposable disposable) {
        mCompositeDisposable.delete(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearDispose();
    }
}

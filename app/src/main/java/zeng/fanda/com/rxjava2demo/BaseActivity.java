package zeng.fanda.com.rxjava2demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author 曾凡达
 * @date 2019/7/9
 */
public class BaseActivity extends AppCompatActivity {

    protected final String TAG = getClass().getName() + "_TAG";

    protected Observer mObserver;

    protected TextView tv_navigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_navigation = findViewById(R.id.tv_navigation);
        createObserver();
    }

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
}

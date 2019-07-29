package zeng.fanda.com.rxjava2demo;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 背压相关
 * <p>
 * 异步场景才会出现，被观察者发送事件速度远快于观察者处理的速度，导致溢出
 *
 * @author 曾凡达
 * @date 2019/7/18
 */
public class BackPressureActivity extends BaseActivity {

    private Subscription mSubscription;


    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
//        testBackPressureExpection();
        // 同步
//        testSyncRequested();
        // 异步
//        testAsynRequested();
//        testStrategy(BackpressureStrategy.DROP);
        testStrategy(BackpressureStrategy.LATEST);
//        testStrategy(BackpressureStrategy.ERROR);
//        testStrategyError();
//        testStrategyMissing();
    }

    /**
     * MISSING 指没有用任何策略，会报 MissingBackpressureException
     * 可以通过 onBackpressureDrop() 、onBackpressureBuffer() 等设置策略
     * 如果是系统的操作符，为了避免背压，也可以用 onBackpressureDrop() 、onBackpressureBuffer() 等设置策略
     */
    private void testStrategyMissing() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 129; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.MISSING).onBackpressureBuffer().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

    }

    /**
     * 会报 MissingBackpressureException，默认缓存量为128
     */
    private void testStrategyError() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 129; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * Latest 当缓存满了会丢弃数据，但总能获取到最后最新的数据
     * Drop 当缓存满了会丢弃数据，获取的是丢弃部分数据后缓存的数据，不是最后最新的
     */
    public void testStrategy(BackpressureStrategy strategy) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 10000; i++) {
                    emitter.onNext(i);
                }
            }
        }, strategy).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        request();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }


    /**
     * 异步的情况下，requested 默认为128，即默认可以处理128个数据
     * 下游能够处理，即 requested ！=0 时，才会发送数据
     * 如果等于0，会根据策略 drop 、latest 、buffer 、error 等来处理数据，等待下一次的 request
     * <p>
     * 注意：
     * 同步的时候，下游 request 多少，上游的 requested 就是多少
     * 异步的时候，下游请求的多少跟上游无关，上游的 requested 默认是128，每当取出96个数据时，requested 增加95
     */
    public void testAsynRequested() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "First requested = " + emitter.requested());
                boolean flag;
                for (int i = 0; ; i++) {
                    flag = false;
                    // 等于0就不发射数据了，即使策略是 Error ，也没有超出默认的缓存量
                    while (emitter.requested() == 0) {
                        if (emitter.isCancelled()) {
                            break;
                        }
                        if (!flag) {
                            Log.d(TAG, "Oh no! I can't emit value!");
                            flag = true;
                        }
                    }
                    emitter.onNext(i);
                    Log.d(TAG, "emit " + i + " , requested = " + emitter.requested());
                }
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 测试同步的 requested ，默认为 0
     */
    public void testSyncRequested() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "First requested = " + emitter.requested());
                boolean flag;
                for (int i = 0; ; i++) {
                    flag = false;
                    // 等于0就不发射数据了，即使策略是 Error ，也没有超出默认的缓存量
                    while (emitter.requested() == 0) {
                        if (!flag) {
                            Log.d(TAG, "Oh no! I can't emit value!");
                            flag = true;
                        }
                    }
                    emitter.onNext(i);
                    Log.d(TAG, "emit " + i + " , requested = " + emitter.requested());
                }
            }
        }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        request();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }


    @OnClick({R.id.tv_request})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_request:
                request();
                break;
            default:
                break;
        }
    }

    /**
     * 演示没有背压处理的情况 ，会内存溢出
     */
    private void testBackPressureExpection() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    emitter.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TimeUnit.SECONDS.sleep(2);
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });
    }

    public void request() {
//        mSubscription.request(96); //请求96个事件
//        mSubscription.request(128); //请求128个事件
        mSubscription.request(1000); //请求1000个事件
    }
}

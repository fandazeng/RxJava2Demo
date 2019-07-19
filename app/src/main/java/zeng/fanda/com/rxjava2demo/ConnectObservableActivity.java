package zeng.fanda.com.rxjava2demo;

import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * 连接操作符演示
 *
 * @author 曾凡达
 * @date 2019/7/18
 */
public class ConnectObservableActivity extends BaseActivity {


    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
        testPublish();
//        testReplay();
//        testRefCount();
    }

    /**
     * 跟 publish 类似，也是转换成 ConnectableObservable ,但是观察者无论何时开始订阅，都能收到所有的数据(变成了 Cold ？)
     * <p>
     * 第1个订阅：0->time:12:28:31 == 消息线程为：main
     * 第1个订阅：1->time:12:28:32 == 消息线程为：main
     * 第1个订阅：2->time:12:28:33 == 消息线程为：main
     * 第2个订阅：0->time:12:28:33 == 消息线程为：RxComputationThreadPool-2
     * 第2个订阅：1->time:12:28:33 == 消息线程为：RxComputationThreadPool-2
     * 第2个订阅：2->time:12:28:33 == 消息线程为：RxComputationThreadPool-2
     * 第1个订阅：3->time:12:28:34 == 消息线程为：main
     * 第2个订阅：3->time:12:28:34 == 消息线程为：RxComputationThreadPool-1
     * 第1个订阅：4->time:12:28:35 == 消息线程为：main
     * 第2个订阅：4->time:12:28:35 == 消息线程为：RxComputationThreadPool-1
     * 第2个订阅：5->time:12:28:36 == 消息线程为：RxComputationThreadPool-1
     * 第1个订阅：5->time:12:28:36 == 消息线程为：main
     */
    private void testReplay() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CANADA);
        Observable<Long> oldObservable = Observable.interval(1, TimeUnit.SECONDS).take(6);

        // 转换成 ConnectableObservable
        // 最好先限定缓存的大小，否则缓存的数据会占用很大的内存，可能会造成OOM
        ConnectableObservable<Long> connectableObservable = oldObservable.replay(100);

        // 可以在 subscribe 之前先调用 connect ，等到 subscribe 之后就会马上发射数据了
        connectableObservable.connect();
        Disposable disposable = connectableObservable.subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "第1个订阅：" + aLong + "->time:" + format.format(new Date()) + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });

        // 延迟3秒再订阅
        Disposable disposable2 = connectableObservable.delaySubscription(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "第2个订阅：" + aLong + "->time:" + format.format(new Date()) + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });

        addDispose(disposable, disposable2);
    }

    private void testRefCount() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CANADA);
        Observable<Long> oldObservable = Observable.interval(1, TimeUnit.SECONDS).take(6);

        // 转换成 ConnectableObservable
        ConnectableObservable<Long> connectableObservable = oldObservable.publish();

        // 转换成普通的 Observable，但是保留了 Hot 的特征，
        // 要所有的订阅者都取消订阅后，在重新订阅时数据流才会从头开始发射
        // 如果只有部分订阅者取消，则取消的订阅者或新的订阅者再次订阅的时候，不会再头开始发射，从当前数据发射并共享
        Observable<Long> refCountObservable = connectableObservable.refCount();

        Disposable disposable = connectableObservable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "ConnectableObservable第1个订阅：" + aLong + "->time:" + format.format(new Date()));
            }
        });

        // 延迟3秒再订阅
        Disposable disposable2 = connectableObservable.delaySubscription(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "ConnectableObservable第2个订阅：" + aLong + "->time:" + format.format(new Date()));
            }
        });

        Disposable refCountdisposable = refCountObservable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "RefCountObservable第1个订阅：" + aLong + "->time:" + format.format(new Date()));
            }
        });

        // 延迟3秒再订阅
        Disposable refCountdisposable2 = refCountObservable.delaySubscription(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "RefCountObservable第2个订阅：" + aLong + "->time:" + format.format(new Date()));
            }
        });

        connectableObservable.connect();

        addDispose(disposable, disposable2, refCountdisposable, refCountdisposable2);
    }

    /**
     * 发射的数据是共享的 ,默认在 Computation 调度器上
     * <p>
     * 第1个订阅：0->time:11:49:36
     * 第1个订阅：1->time:11:49:37
     * 第1个订阅：2->time:11:49:38
     * 第2个订阅：2->time:11:49:38
     * 第1个订阅：3->time:11:49:39
     * 第2个订阅：3->time:11:49:39
     * 第1个订阅：4->time:11:49:40
     * 第2个订阅：4->time:11:49:40
     * 第1个订阅：5->time:11:49:41
     * 第2个订阅：5->time:11:49:41
     */
    private void testPublish() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CANADA);
        Observable<Long> oldObservable = Observable.interval(1, TimeUnit.SECONDS).take(6);

        // 转换成 ConnectableObservable
        ConnectableObservable<Long> connectableObservable = oldObservable.publish();

        // 可以在 subscribe 之前先调用 connect ，等到 subscribe 之后就会马上发射数据了
        connectableObservable.connect();
        Disposable disposable = connectableObservable.subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "第1个订阅：" + aLong + "->time:" + format.format(new Date()) + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });

        // 延迟3秒再订阅
        connectableObservable.delaySubscription(3, TimeUnit.SECONDS).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "解除订阅了" + " == 解除订阅的线程为：" + Thread.currentThread().getName());
            }
        }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                addDispose(d);
            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "第2个订阅：" + aLong + "->time:" + format.format(new Date()) + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        // 调用 connect 才会发射数据，subscribe 是不会发射数据的，可以更加灵活的控制发射时机，ConnectableObservable 是 Hot Observable
//        connectableObservable.connect();

        addDispose(disposable);
    }

    @OnClick({R.id.tv_request})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_request:
                clearDispose();
                break;
            default:
                break;
        }
    }
}

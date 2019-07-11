package zeng.fanda.com.rxjava2demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DefaultSubscriber;
import io.reactivex.subscribers.SafeSubscriber;

/**
 * 创建 Observable 演示
 */
public class CreateObservableActivity extends BaseActivity {

    private Scheduler.Worker worker;

    private String data = "测试数据";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        testSchedulers();
//        testCreateObservable();
//        testDeferObservable();
//        testEmptyObservable();
//        testNeverObservable();
//        testErrorObservable();

//        testFromObservable();
//        testJustObservable();
//        testIntervalObservable();
//        testTimerObservable();
//        testRangeObservable();
        testRepeatObservable();

        findViewById(R.id.tv_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateObservableActivity.this, TransforObservableActivity.class));
            }
        });

    }

    /**
     * 重复地发射数据，如果不指定次数，默认无限
     */
    private void testRepeatObservable() {
        Disposable disposable = Observable.range(1, 5).repeat(2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 发射一个范围内的有序整数序列，你可以指定范围的起始和长度。
     */
    private void testRangeObservable() {
//        Disposable disposable = Observable.range(5, 10).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
//            }
//        });

        Disposable disposableLong = Observable.rangeLong(5, 10).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "收到消息==" + aLong + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 它在指定延迟之后发射一个零值，默认在 computation 调度器上执行，操作UI需要线程切换
     */
    private void testTimerObservable() {
        Disposable disposable = Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "收到消息==" + aLong + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 创建一个按固定时间间隔发射整数序列的Observable，默认在	computation	调度器上执行，操作UI需要线程切换
     */
    private void testIntervalObservable() {
//        Disposable disposable = Observable.interval(1, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
//                subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        Log.d(TAG, "收到消息==" + aLong + " == 消息线程为：" + Thread.currentThread().getName());
//                    }
//                });

        Disposable disposableRange = Observable.intervalRange(60, 60, 0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "收到消息==" + aLong + " == 消息线程为：" + Thread.currentThread().getName());
                    }
                });
    }

    /**
     * 将单个数据转换为发射那个数据的Observable
     */
    private void testJustObservable() {
        String[] array = {"1", "2", "3", "4", "5"};
        Disposable disposable = Observable.just(array).subscribe(new Consumer<String[]>() {
            @Override
            public void accept(String[] strings) throws Exception {
                for (String string : strings) {
                    Log.d(TAG, "收到消息==" + string + " == 消息线程为：" + Thread.currentThread().getName());
                }
            }
        });

    }

    /**
     * 将其它种类的对象和数据类型转换为Observable，对于Iterable和数组，产生的 Observable 会发射Iterable或数组的每一项数据
     */
    private void testFromObservable() {
        String[] array = {"1", "2", "3", "4", "5"};
        Observable.fromArray(array).subscribe(mObserver);
    }

    /**
     * 创建一个不发射数据以一个错误终止的Observable，需要一 个	Throwable	参数
     */
    private void testErrorObservable() {
        Observable<String> error = Observable.error(new Throwable("testErrorObservable"));
        error.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(mObserver);
    }

    /**
     * 创建一个不发射数据也不终止的Observable
     */
    private void testNeverObservable() {
        Observable<String> never = Observable.never();
        never.subscribe(mObserver);
    }

    /**
     * 创建一个不发射任何数据但是正常终止的Observable
     */
    private void testEmptyObservable() {
        final Observable<String> empty = Observable.empty();
        Schedulers.newThread().createWorker().schedule(new Runnable() {
            @Override
            public void run() {
                empty.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(mObserver);
            }
        });
    }

    /**
     * 直到有观察者订阅时才创建Observable，并且为每个观察者创建一个新的Observable
     */
    private void testDeferObservable() {
        Observable<String> observable = Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just(data);
            }
        });
        data = "新的测试数据";
        observable.subscribe(mObserver);
    }

    /**
     * 通过调用观察者的方法从头创建一个Observable
     */
    public void testCreateObservable() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext(data);
                Log.d(TAG, "Observable 在发射消息");
                emitter.onComplete();
            }
        });

        observable.subscribe(mObserver);
    }

    /**
     * 测试一下调度器的功能
     */
    public void testSchedulers() {
        worker = Schedulers.newThread().createWorker();

        worker.schedule(new Runnable() {
            @Override
            public void run() {
//                for (int i = 0; i < 5; i++) {
//                    Log.d("CreateObservableActivity", "递归信息");
//                    worker.schedule(this);
//                }
//                worker.dispose();

                int counts = 0;
                while (!worker.isDisposed()) {
                    Log.d(TAG, "递归信息");
                    counts++;
                    if (counts == 5) {
                        worker.dispose();
                    }
                }
            }
        });
    }

}

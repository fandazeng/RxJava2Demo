package zeng.fanda.com.rxjava2demo;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

/**
 * 创建 Observable 演示
 */
public class CreateObservableActivity extends BaseActivity {

    private Scheduler.Worker worker;

    private String data = "测试数据";

    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {

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

//        testSingleObservable();
//        testCompletableObservable();
//        testMaybeObservable();

    }

    /**
     * single 和 completable 的结合体，只能发射零或一个数据，后续发的数据不会做任何处理，有 onComplete 和 onError 事件
     * <p>
     * 收到消息==1 == 消息线程为：main
     * <p>
     * 后续的 2 和 onComplete 事件不会收到
     */
    private void testMaybeObservable() {
        Disposable disposable = Maybe.create(new MaybeOnSubscribe<Integer>() {

            @Override
            public void subscribe(MaybeEmitter<Integer> emitter) throws Exception {
                emitter.onSuccess(1);
                emitter.onSuccess(2);
                emitter.onComplete();
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "Maybe Complete==" + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });

        // 如果没有发射任何数据时，会回调 onComplete ，有发射数据或 onError ，则不会再回调 onComplete
        Maybe.empty().subscribe(new MaybeObserver<Object>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "收到消息==" + o + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "完成" + " == 完成线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 不会发射任何数据，只有 onComplete 和 onError 事件
     */
    private void testCompletableObservable() {
        Disposable disposable = Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                TimeUnit.SECONDS.sleep(1);
                emitter.onComplete();

            }
        }).andThen(Observable.range(1, 10)).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 只能发射零或一个数据，后续发的数据不会做任何处理
     * <p>
     * 收到消息==2 == 消息线程为：main
     */
    private void testSingleObservable() {
        Disposable disposable = Single.create(new SingleOnSubscribe<Integer>() {

            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                emitter.onSuccess(2);
                emitter.onSuccess(3);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 重复地发射数据，如果不指定次数，默认无限，如果传0，则不发射任何数据，直接回调完成，如果传入负数，会报错
     * 如果传入的值大于0，则会再重复地发射 n-1 次，即如果传1，则跟正常发射的内容一样，再重复发射0次
     *
     * 结果如下：
     *
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testRepeatObservable() {
        Observable.range(1, 2).repeat(1).subscribe(mObserver);

//        Disposable repeatWhen = Observable.range(1, 5).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
//            @Override
//            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
//                // 当这里发射数据时，才会重新发射原始数据
//                return Observable.timer(5,TimeUnit.SECONDS);
//            }
//        }).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
//            }
//        });

//        final long start = System.currentTimeMillis();
//        Disposable repeatUntil = Observable.range(1, 5).repeatUntil(new BooleanSupplier() {
//            @Override
//            public boolean getAsBoolean() throws Exception {
//                return System.currentTimeMillis() - start > 5000;
//            }
//        }).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
//            }
//        });
//        try {
//            TimeUnit.SECONDS.sleep(6);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * 发射一个范围内的有序整数序列，你可以指定范围的起始和长度
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==5 == 消息线程为：main
     * 收到消息==6 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testRangeObservable() {
        Observable.range(5, 2).subscribe(mObserver);
        Observable.rangeLong(5, 2).subscribe(mObserver);
    }

    /**
     * 它在指定延迟之后发射一个零值，默认在 computation 调度器上执行，操作UI需要线程切换
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==0 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testTimerObservable() {
        Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(mObserver);
    }

    /**
     * 创建一个按固定时间间隔发射整数序列的Observable，默认在 computation	调度器上执行，操作UI需要线程切换
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==0 == 消息线程为：main
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * ......
     */
    private void testIntervalObservable() {
//        Observable.interval(1, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
//                subscribe(mObserver);
        // 按给定的初始值和数量，再按固定时间间隔发射整数序列
        Observable.intervalRange(10, 60, 0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
                subscribe(mObserver);
    }

    /**
     * 将单个数据转换为发射那个数据的Observable
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 收到消息==3 == 消息线程为：main
     */
    private void testJustObservable() {
        String[] array = {"1", "2", "3"};
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
     * 将其它种类的对象和数据类型转换为Observable，对于 Iterable 和数组，产生的 Observable 会发射 Iterable 或数组的每一项数据
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 收到消息==3 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testFromObservable() {
        String[] array = {"1", "2", "3"};
        Observable.fromArray(array).subscribe(mObserver);
    }

    /**
     * 创建一个不发射数据直接以一个错误终止的Observable，需要一 个	Throwable	参数
     * <p>
     * 结果如下：
     * <p>
     * 错误:testErrorObservable == 错误线程为：main
     */
    private void testErrorObservable() {
        Observable.error(new Throwable("testErrorObservable")).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(mObserver);
    }

    /**
     * 创建一个不发射数据也不终止的Observable
     */
    private void testNeverObservable() {
        Observable.never().subscribe(mObserver);
    }

    /**
     * 创建一个不发射任何数据直接通知完成的Observable
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
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==新的测试数据 == 消息线程为：main
     * 完成 == 完成线程为：main
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
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==测试数据 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    public void testCreateObservable() {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(data);
                    emitter.onComplete();
                    // 这里发射的数据不再接收了
                    emitter.onNext("新测试数据");
                    emitter.onNext("新测试数据");
                }
            }
        }).subscribe(mObserver);
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

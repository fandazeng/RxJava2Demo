package zeng.fanda.com.rxjava2demo;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;

/**
 * 辅助操作符演示
 *
 * @author 曾凡达
 * @date 2019/7/11
 */
public class HelpObservableActivity extends BaseActivity {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
//        testDelay();

//        testDo();
        testObserveOn();
//        testTimeInterval();
//        testTimestamp();

//        testTimeout();

//        testTo();
    }

    /**
     * 此系列操作符的作用是将Observable转换为另一个对象或数据结构
     */
    private void testTo() {

//        testToList();

//        testToMap();

//        testMultiMap();

        testToSortedList();

    }

    /**
     * 类似于 toList ，但是它可以对数据进行自然排序，默认是自然升序，如果发射的数据项没有实现	Comparable	接口，会抛出一个异常。
     * <p>
     * 收到消息==[0, 1, 2, 4, 7, 9, 10] == 消息线程为：main
     */
    private void testToSortedList() {
//        Observable.just(1, 10, 4, 7, 2, 9, 0).toSortedList().subscribe(new SingleObserver<List<Integer>>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onSuccess(List<Integer> integers) {
//                Log.d(TAG, "收到消息==" + integers + " == 消息线程为：" + Thread.currentThread().getName());
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//        });

        Observable.just(1, 10, 4, 7, 2, 9, 0).toSortedList(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                // 降序
                if (o1 == o2) {
                    return 0;
                } else if (o1 > o2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }).subscribe(new SingleObserver<List<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<Integer> integers) {
                Log.d(TAG, "收到消息==" + integers + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /**
     * 它生成的这个Map同时还是一个ArrayList
     */
    private void testMultiMap() {
        Observable.range(0, 3).toMultimap(new Function<Integer, String>() {

            @Override
            public String apply(Integer integer) throws Exception {
                return "key" + integer;
            }
        }).subscribe(new SingleObserver<Map<String, Collection<Integer>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Map<String, Collection<Integer>> stringCollectionMap) {
                Log.d(TAG, "收到消息==" + stringCollectionMap + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /**
     * 该操作符收集原始Observable发射的所有数据项到一个Map（默认是HashMap)
     */
    private void testToMap() {
        Observable.range(0, 3).toMap(new Function<Integer, String>() {

            @Override
            public String apply(Integer integer) throws Exception {
                return "key" + integer;
            }
        }).subscribe(new SingleObserver<Map<String, Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Map<String, Integer> stringIntegerMap) {
                Log.d(TAG, "收到消息==" + stringIntegerMap + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }
        });

        Observable.range(0, 3).toMap(new Function<Integer, String>() {

            @Override
            public String apply(Integer integer) throws Exception {
                // 生成 KEY
                return "key" + integer;
            }
        }, new Function<Integer, Integer>() {

            @Override
            public Integer apply(Integer integer) throws Exception {
                // 对值做变换
                return integer + 100;
            }
        }).subscribe(new SingleObserver<Map<String, Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Map<String, Integer> stringIntegerMap) {
                Log.d(TAG, "收到消息==" + stringIntegerMap + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }
        });


    }


    /**
     * 通过toList将单个数据最终以List的形式输出
     */
    private void testToList() {
        Observable.range(0, 10).toList().subscribe(new SingleObserver<List<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<Integer> integers) {
                Log.d(TAG, "收到消息==" + integers + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /**
     * 给Observable发射的数据项附加一个时间戳 ，默认在computation调度器上执行
     */
    private void testTimestamp() {
        // 默认单位为毫秒 TimeUnit.MILLISECONDS
        Observable.range(0, 5000).delay(1, TimeUnit.SECONDS).timestamp(TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Timed<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Timed<Integer> integerTimed) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                        Log.d(TAG, "onNext: value:" + integerTimed.value() + " ===== " + "time" + sdf.format(new Date(integerTimed.time())));

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 如果原始Observable过了指定的一段时间没有发射任何数据，Timeout操作符会以一个onError通知终止这个Observable。
     * 默认在	computation	调度器上执行
     * <p>
     * 订阅线程为：main
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 收到消息==3 == 消息线程为：main
     * 错误:The source did not signal an event for 250 milliseconds and has been terminated. == 错误线程为：RxComputationThreadPool-1
     * <p>
     * onNext 都是在 Main 线程上，报错的时候，在 computation 线程上
     */
    private void testTimeout() {
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                emitter.onNext(1);
//                Thread.sleep(100);
//                emitter.onNext(2);
//                Thread.sleep(200);
//                emitter.onNext(3);
//                Thread.sleep(300);
//                emitter.onNext(4);
//                Thread.sleep(400);
//            }
//        }).timeout(250, TimeUnit.MILLISECONDS).subscribe(mObserver);

        // 在超时的时候切换到一个我们指定的备用的Observable，而不是发错误通知
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(100);
                emitter.onNext(2);
                Thread.sleep(200);
                emitter.onNext(3);
                Thread.sleep(300);
                emitter.onNext(4);
                Thread.sleep(400);
            }
        }).timeout(250, TimeUnit.MILLISECONDS, Observable.just(100, 200)).subscribe(mObserver);

    }

    /**
     * doOnEach 相当于doOnNext，doOnError，doOnCompleted综合体
     */
    private void testDoOnEach() {
        Observable.just(1, 2, 3).concatWith(Observable.<Integer>error(new Throwable("测试错误")))
                .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                    public void accept(Notification<Integer> integerNotification) throws Exception {
                        // onNext回调之前，会先回调这里的
                        Log.d(TAG, "call 线程：" + Thread.currentThread().getName());
                    }
                }).subscribe(mObserver);

        Observable.just(1, 2, 3).doOnEach(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer o) {
                // onNext回调之前，会先回调这里的
                Log.d(TAG, "DO 收到消息==" + o + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                // onError回调之前，会先回调这里的
                Log.d(TAG, "DO 错误:" + e.getMessage() + " == 错误线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onComplete() {
                // onComplete回调之前，会先回调这里的
                Log.d(TAG, "DO 完成" + " == 完成线程为：" + Thread.currentThread().getName());
            }
        }).subscribe(mObserver);
    }

    /**
     * onNext回调之前会先回调
     */
    private void testDoOnNext() {
        // doAfterNext  onNext回调之后再回调

        Observable.just(1, 2, 3).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer > 2) {
                    throw new RuntimeException("测试错误");
                }
            }
        }).subscribe(mObserver);
    }

    /**
     * 对于do系列操作符理解比较容易，他相当于给Observable执行周期的关键节点添加回调。当Observable执行到这个阶段的时候，这些回调就会被触发。
     * 在Rxjava do系列操作符有多个，如doOnNext，doOnSubscribe，doOnUnsubscribe，doOnCompleted，doOnError，doOnTerminate和doOnEach。
     * 当Observable每发送一个数据时，doOnNext会被首先调用，然后再onNext。若发射中途出现异常doOnError会被调用，然后onError。若数据正常
     * 发送完毕doOnCompleted会被触发，然后执行onCompleted。
     */
    private void testDo() {
//        testDoOnNext();
//        testDoOnEach();
//        testDoOnError();
//        testDoOnTerminate();
//        testDoFinally();
//        testDoOnComplete();
//        testDoOnSubscribe();

    }

    /**
     * TimeInterval	操作符拦截原始Observable发射的数据项，替换为发射表示相邻发射物时间间 隔的对象
     * 默认在	immediate	调度器上执行，
     */
    private void testTimeInterval() {
        // 默认单位为毫秒 TimeUnit.MILLISECONDS
        Observable.range(0, 20).delay(1, TimeUnit.SECONDS).timeInterval(TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Timed<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Timed<Integer> integerTimed) {
                        Log.d(TAG, "onNext: value:" + integerTimed.value() + " ===== " + "time" + integerTimed.time());

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * observeOn 该操作符指定Observable在一个特定的调度器上发送通知给观察者，可以指定多次，每次指定会在observeOn下一句代码处生效
     * subscribeOn 它是用于指定Observable本身在特定的调度器上执行，只能指定一次，如果指定多次则以第一次为准
     */
    private void testObserveOn() {
//        Observable.just(1, 2, 3).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).map(new Function<Integer, String>() {
//
//            @Override
//            public String apply(Integer integer) throws Exception {
//                Log.d(TAG, "map1==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
//                return String.valueOf(integer);
//            }
//        }).observeOn(Schedulers.io()).map(new Function<String, Integer>() {
//
//            @Override
//            public Integer apply(String s) throws Exception {
//                Log.d(TAG, "map2==" + s + " == 消息线程为：" + Thread.currentThread().getName());
//                return Integer.valueOf(s);
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);


        Observable.just("aaa", "bbb").observeOn(Schedulers.newThread()).map(new Function<String, String>() {

            @Override
            public String apply(String s) throws Exception {
                Log.d(TAG, "apply==" + s + " == 消息线程为：" + Thread.currentThread().getName());
                return s.toUpperCase();
            }
        }).subscribeOn(Schedulers.single()).observeOn(Schedulers.io())
                .subscribe(mObserver);
    }


    /**
     * 当观察者订阅它生成的Observable就会被调用,在 onSubscribe 回调之前会先调用
     */
    private void testDoOnSubscribe() {
        Observable.just(1).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                Log.d(TAG, "call 线程：" + Thread.currentThread().getName());
            }
        }).subscribe(mObserver);
    }


    /**
     * onComplete回调之前会先回调
     */
    private void testDoOnComplete() {
        Observable.just(1).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "call 线程：" + Thread.currentThread().getName());

            }
        }).subscribe(mObserver);
    }

    /**
     * 当它产生的Observable终止之 *后* 会被调用，无论是正常还 是异常终止
     */
    private void testDoFinally() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
//                emitter.onError(new Throwable("测试错误"));

            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "call 线程：" + Thread.currentThread().getName());
            }
        }).subscribe(mObserver);
    }

    /**
     * Observable终止之前会被调用，无论是正 常还是异常终止
     */
    private void testDoOnTerminate() {

        // doAfterTerminate Observable终止之后再被调用，无论是正 常还是异常终止

        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
//                emitter.onComplete();
                emitter.onError(new Throwable("测试错误"));

            }
        }).doOnTerminate(new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "call 线程：" + Thread.currentThread().getName());
            }
        }).subscribe(mObserver);
    }

    /**
     * onError回调之前会先回调
     */
    private void testDoOnError() {
        Observable.error(new Throwable("测试错误")).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "call 线程：" + Thread.currentThread().getName());
            }
        }).subscribe(mObserver);
    }

    /**
     * delay	默认在	computation	调度器上执行
     */
    private void testDelay() {
        //延迟一段指定的时间再发射来自Observable的发射物
//        Observable.range(0, 5).delay(2, TimeUnit.SECONDS).subscribe(mObserver);

        // delay	不会平移	onError	通知，它会立即将这个通知传递给订阅者，同时丢弃任何待 发射的	onNext	通知
//        Observable.error(new Throwable("测试错误")).delay(10, TimeUnit.SECONDS).subscribe(mObserver);

        // 会平移一个	onCompleted	通知
//        Observable.empty().delay(10, TimeUnit.SECONDS).subscribe(mObserver);

        // 它和dealy的区别是dealy是延迟数据的发送，而此操作符是延迟数据的注册，指定延迟时间的重载方法是执行在computation调度器的。
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                Log.d(TAG, "call 线程：" + Thread.currentThread().getName());
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onNext(5);

            }
        }).delaySubscription(10, TimeUnit.SECONDS).subscribe(mObserver);
    }
}

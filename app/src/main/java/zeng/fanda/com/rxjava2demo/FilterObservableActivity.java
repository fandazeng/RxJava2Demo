package zeng.fanda.com.rxjava2demo;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.CompletableObserver;
import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableTransformer;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 过滤操作符演示
 *
 * @author 曾凡达
 * @date 2019/7/9
 */
public class FilterObservableActivity extends BaseActivity {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {

//        testDebounce();
//        testDistinct();
//        testElementAt();
//        testFilter();
//        testOfType();
//        testFirst();
//        testLast();
//        testSingle();
//        testIgnoreElements();

//        testSample();
//        testthrottleFirst();
//        testSkip();
        testTake();
    }

    /**
     * take 只发射前面的N项数据 take(1) 等价于 first
     * takeLast 只发射后面的N项数据 takeLast(1) 等价于 last
     */
    private void testTake() {
        // 只发射前面4个数据
         Observable.range(1, 5).take(2).subscribe(mObserver);
        // 只发射后面4个数据
        Observable.range(1, 10).takeLast(4).subscribe(mObserver);

        // 只获取3秒内发射的数据
        Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
                .take(3, TimeUnit.SECONDS)
                .subscribe(mObserver);
    }

    private void testSkip() {
        //  忽略Observable发射的前N项数据，只保留之后的数据。
        Observable.interval(1, TimeUnit.SECONDS).skip(2).subscribe(mObserver);

        // 丢弃原始Observable开始的那段时间发射的数据，时长和时间单位通过参数指定
        Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS).skip(5, TimeUnit.SECONDS)
                .subscribe(mObserver);

        // 忽略原始Observable发射的后N项数据，只保留之前的数据。
        Observable.range(0, 10).skipLast(5).subscribe(mObserver);

        // 丢弃在原始Observable的最后一段时间内发射的数据
        Observable.range(0, 100).flatMap(new Function<Integer, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Integer integer) throws Exception {
                Thread.sleep(200);
                return Observable.just(integer);
            }
        }).skipLast(5, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);
    }

    /**
     * 定期发射Observable发射的数据项的第一项 ，默认在 computation 调度器上执行
     * <p>
     * 结果如下；
     * <p>
     * 收到消息==0 == 消息线程为：main
     * 收到消息==5 == 消息线程为：main
     * 收到消息==9 == 消息线程为：main
     * 收到消息==13 == 消息线程为：main
     * 收到消息==17 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testthrottleFirst() {
        Observable.range(0, 20).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                Thread.sleep(500);
                return Observable.just(integer);
            }
        }).throttleFirst(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);
    }

    /**
     * 定期发射 Observable 最近的数据，跟 throttleLast 操作符等价，默认在	computation	调度器上执行
     * <p>
     * 结果如下；
     * <p>
     * 收到消息==2 == 消息线程为：main，这个数据为什么会打印出来？
     * 收到消息==6 == 消息线程为：main
     * 收到消息==10 == 消息线程为：main
     * 收到消息==14 == 消息线程为：main
     * 收到消息==18 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testSample() {
        Observable.range(0, 20).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                Thread.sleep(500);
                return Observable.just(integer);
            }
        }).sample(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);
    }

    /**
     * 只发射最后一个数据
     */
    private void testLast() {
        Disposable firstElement = Observable.just(1, 2, 3).lastElement().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });

        //  在没有发射任何数据时发射一个你在参数中指定的默认值
        Disposable firstDefault = Observable.empty().last(100).subscribe(new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "收到消息==" + o + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 丢弃所有的正常数据，只发射错误或完成通知，是对数据源的处理，返回 CompletableObserver 对象，而 empty（）是创建空的 Observable
     * <p>
     * 结果如下；
     * <p>
     * 错误:测试异常 == 错误线程为：main
     */
    private void testIgnoreElements() {
        Observable.range(0, 10).concatWith(Observable.error(new Throwable("测试异常")))
                .ignoreElements().subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "完成" + " == 完成线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "错误:" + e.getMessage() + " == 错误线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 发送数据是一项的话输出此项的值，若是多个数据则抛出异常执行onError()方法，返回 SingleObserver
     * 如果没有发射任何值，可以用重载方法传的默认值
     * <p>
     * 结果如下；
     * <p>
     * 错误:Sequence contains more than one element! == 错误线程为：main
     */
    private void testSingle() {
        Observable.range(0, 20).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer > 10;
            }
        }).single(100).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "错误:" + e.getMessage() + " == 错误线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 只发射第一个数据
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==1 == 消息线程为：main
     */
    private void testFirst() {
        // 只发射第一个数据，返回一个 MaybeObservable，如果有数据发射，不会再回调 onComplete ，这是 MaybeObservable 的特征
        Disposable firstElement = Observable.just(1, 2, 3).firstElement().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });

        //  只发射第一个数据，在没有发射任何数据时发射一个你在参数中指定的默认值，返回一个 SingleObservable
        Disposable firstDefault = Observable.<Integer>empty().first(100).subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 是 filter	 操作符的一个特殊形式。它过滤一个Observable只发射指定类型的数据
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==one == 消息线程为：main
     * 收到消息==two == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testOfType() {
        Observable.just(0, "one", 6, 4, "two", 8).ofType(String.class).subscribe(mObserver);
    }

    /**
     * 指定一个函数来过滤数据项，只有通过条件的数据才会被发射
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 收到消息==3 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testFilter() {
        Observable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer < 4;
            }
        }).subscribe(mObserver);
    }

    /**
     * 只发射第N项数据，传入的 index 是个索引，从 0 开始，，如果你传递给	elementAt	的值为5，那么它会发射第 六项的数据，返回 MaybeObserver
     * <p>
     * 结果如下：
     * <p>
     * 完成 == 完成线程为：main
     */
    private void testElementAt() {
        //  如果 Index 越界了，会回调 onComplete ,但是如果你传递一 个负数索引值，它仍然会抛出一个	IndexOutOfBoundsException	异常。
        // elementAt

        // 如果 Index 越界了，会回调 onError ,但是如果你传递一 个负数索引值，它仍然会抛出一个	IndexOutOfBoundsException	异常。
        // 	elementAtOrError(int)

        //  如果索引值大于数据 项数，它会发射一个默认值（通过额外的参数指定），而不是抛出异常。
        //  但是如果你传递一 个负数索引值，它仍然会抛出一个	IndexOutOfBoundsException	异常。
        // elementAt(int,T)

        Observable.range(0, 9).elementAt(11).subscribe(new MaybeObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "错误:" + e.getMessage() + " == 错误线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "完成" + " == 完成线程为：" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 过滤掉重复的数据，只允许还没有发射过的数据项通过
     * <p>
     * 结果如下；
     * <p>
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 收到消息==3 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testDistinct() {
        Observable.just(1, 2, 1, 1, 2, 3).distinct().subscribe(mObserver);

        // 它只判定两个相邻的数据是否是不同的，因此只会从序列中过滤掉连续重复的数据
        Observable.just(1, 2, 1, 1, 2, 3).distinctUntilChanged().subscribe(mObserver);

        // 这个函数根据原始Observable发射的数据项产生一个 Key，然后，比较这些Key而不是数据本身，来判定两个数据是否是不同的
        Observable.just(1, 2, 1, 1, 2, 3).distinct(new Function<Number, Boolean>() {
            @Override
            public Boolean apply(Number number) throws Exception {
                return number.intValue() % 2 == 0;
            }
        }).subscribe(mObserver);

        // 和 distinct(Func1)	一样，根据一个函数产生的Key判定两个相邻的数据项是不是不同的
        Observable.just(1, 2, 1, 1, 2, 3).distinctUntilChanged(new Function<Number, Boolean>() {
            @Override
            public Boolean apply(Number number) throws Exception {
                return number.intValue() % 2 == 0;
            }
        }).subscribe(mObserver);

    }

    /**
     * 过滤一段指定的时间内的数据，发射后续的数据(后续没有数据，发射最后一项数据并回调 onCompleted)，默认在 computation 调度器上执行
     * throttleWithTimeout 和 debounce 效果一样
     * <p>
     * 结果如下：
     * <p>
     * 收到消息==10 == 消息线程为：main
     * 收到消息==11 == 消息线程为：main
     * 收到消息==12 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testDebounce() {
        Observable.range(1, 12).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                Thread.sleep(100 * integer);
                // 从9开始才发射一个数据，9*100 刚好少于1秒的第一个数
                // 注意：先 sleep 后发射数据和先发射数据后 sleep 的区别
                return Observable.just(integer + "");
            }
        }).debounce(1, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);


//        Observable.range(1, 15).flatMap(new Function<Integer, ObservableSource<String>>() {
//            @Override
//            public ObservableSource<String> apply(Integer integer) throws Exception {
//                Thread.sleep(200 * integer);
//                // 从9开始才发射一个数据，9*200 刚好少玗2秒的第一个数
//                return Observable.just(integer + "");
//            }
//        }).throttleWithTimeout(2, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);

//        Observable.range(1, 15).debounce(new Function<Integer, ObservableSource<String>>() {
//            @Override
//            public ObservableSource<String> apply(Integer integer) throws Exception {
//                Thread.sleep(100 * integer);
//                return Observable.just(integer + "新的数据");
//            }
//        }).subscribeOn(Schedulers.io()).subscribe(mObserver);
    }


}

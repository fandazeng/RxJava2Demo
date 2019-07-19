package zeng.fanda.com.rxjava2demo;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 布尔运算操作演示
 *
 * @author 曾凡达
 * @date 2019/7/15
 */
public class BooleanObservableActivity extends BaseActivity {


    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
//        testAll();
//        testAmb();
//        testContains();
//        testDefaultIfEmpty();
//        testSequenceEqual();
//        testSkipUtil();
//        testSkipWhile();
//        testTakeUtil();
        testTakeWhile();
    }

    /**
     * 发射Observable发射的数据，直到一个指定的条件不成立
     * <p>
     * 收到消息==0 == 消息线程为：main
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 收到消息==3 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testTakeWhile() {
        Observable.range(0, 10).takeWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer < 4;
            }
        }).subscribe(mObserver);
    }

    /**
     * 如果第二个Observable发射了一项数据或者发射了一个终止通知，	TakeUntil	返回的Observable会 停止发射原始Observable并终止
     * <p>
     * 收到消息==0 == 消息线程为：main
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 收到消息==3 == 消息线程为：main
     * 收到消息==4 == 消息线程为：main
     * 完成 == 完成线程为：main
     * <p>
     * 注意：最后一次不满足条件的数据，还是会发射出去的
     */
    private void testTakeUtil() {
//        Observable.range(0, 10).takeUntil(new Predicate<Integer>() {
//            @Override
//            public boolean test(Integer integer) throws Exception {
//                return integer > 3;
//            }
//        }).subscribe(mObserver);

        Observable.intervalRange(1, 5, 0, 1, TimeUnit.SECONDS)
                .takeUntil(Observable.timer(3, TimeUnit.SECONDS))
                .subscribe(mObserver);
    }

    /**
     * 丢弃Observable发射的数据，直到一个指定的条件不成立
     * <p>
     * 收到消息==4 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testSkipWhile() {
        Observable.range(0, 5).skipWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer < 4;
            }
        }).subscribe(mObserver);
    }

    /**
     * 丢弃原始Observable发射的数据，直到第二个Observable发射了一项数据
     * <p>
     * 收到消息==4 == 消息线程为：RxComputationThreadPool-2
     * 收到消息==5 == 消息线程为：RxComputationThreadPool-2
     * 完成 == 完成线程为：RxComputationThreadPool-2
     */
    private void testSkipUtil() {
        Observable.intervalRange(1, 5, 0, 1, TimeUnit.SECONDS)
                .skipUntil(Observable.timer(3, TimeUnit.SECONDS))
                .subscribe(mObserver);
    }

    /**
     * 判定两个Observables是否发射相同的数据序列
     * <p>
     * 传递两个Observable给	SequenceEqual	操作符，它会比较两个Observable的发射物，
     * 如果两 个序列是相同的（相同的数据，相同的顺序，相同的终止状态），它就发射true，否则发射 false。
     */
    private void testSequenceEqual() {
        Observable<Integer> observableA = Observable.just(1, 2, 3);
        Observable<Integer> observableB = Observable.just(1, 2, 3);
        Disposable disposable = Observable.sequenceEqual(observableA, observableB).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d(TAG, "收到消息==" + aBoolean + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });

        // 三个参数，传递一个用于比较两个数据项是否相同的函数
        Disposable disposable2 = Observable.sequenceEqual(observableA, observableB, new BiPredicate<Integer, Integer>() {
            @Override
            public boolean test(Integer integer, Integer integer2) throws Exception {
                return integer == integer2;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d(TAG, "收到消息==" + aBoolean + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });


    }

    /**
     * 发射来自原始Observable的值，如果原始Observable没有发射任何值，就发射一个默认值
     * <p>
     * 收到消息==100 == 消息线程为：main
     */
    private void testDefaultIfEmpty() {
        // 其实内部就是调的 switchIfEmpty 操作符并且用了just 来发射单个数据
        Observable.just(1, 2, 3).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer > 3;
            }
        }).defaultIfEmpty(100).subscribe(mObserver);

        // switchIfEmpty 如果原始Observable没有发射数据，它发射一个备用Observable的发射物
        Observable.just(1, 2, 3).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer > 3;
            }
        }).switchIfEmpty(Observable.just(100)).subscribe(mObserver);
    }

    /**
     * 判定一个Observable是否发射一个特定的值
     */
    private void testContains() {
        // IsEmpty 用于判定原始Observable是否没有发射任何数据。
        Observable.just(1, 2, 3).contains(2).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.d(TAG, "收到消息==" + aBoolean + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /**
     * 给定两个或多个Observables，它只发射首先发射数据或通知的那个Observable的所有数据
     * <p>
     * 当你传递多个Observable给	Amb	时，它只发射其中一个Observable的数据和通知：
     * 首先发送 通知给	Amb	的那个，不管发射的是一项数据还是一个	onError	或	onCompleted	通知。
     * Amb	将 忽略和丢弃其它所有Observables的发射物。
     */
    private void testAmb() {
        Observable<Integer> observableA = Observable.just(1, 2, 3).delay(500, TimeUnit.MILLISECONDS);
        Observable<Integer> observableB = Observable.just(4, 5, 6).delay(100, TimeUnit.MILLISECONDS);

        Observable.ambArray(observableA, observableB).subscribe(mObserver);
        // 这里等价于上面的语句
        observableA.ambWith(observableB).subscribe(mObserver);

    }

    /**
     * 判定是否Observable发射的所有数据都满足某个条件
     * All	返回一个只发射一个单个布尔值的Observable，如果原始Observable正常终止并且每一项数据都满足条件，就返回true；
     * 如果原始Observable的任何一项数据不满足条 件就返回False
     * <p>
     * test==0 == 消息线程为：main
     * 收到消息==false == 消息线程为：main
     */
    private void testAll() {
        Observable.range(0, 5).all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                Log.d(TAG, "test==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
                return integer > 3;
            }
        }).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.d(TAG, "收到消息==" + aBoolean + " == 消息线程为：" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }
}

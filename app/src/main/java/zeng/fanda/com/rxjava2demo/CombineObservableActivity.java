package zeng.fanda.com.rxjava2demo;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;

/**
 * 组合操作符演示
 *
 * @author 曾凡达
 * @date 2019/7/11
 */
public class CombineObservableActivity extends BaseActivity {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
//        testCombineLatest();

//        testJoin();
//        testMerge();

//        testZip();
        testStartWith();
//        testSwitchOnNext();
    }

    /**
     * Switch	订阅一个发射多个Observables的Observable。它每次观察那些Observables中的一 个，	Switch	返回的这个Observable取消订阅前
     * 一个发射数据的Observable，开始发射最近的 Observable发射的数据。注意：当原始Observable发射了一个新的Observable时（不是这个 新的
     * Observable发射了一条数据时），它将取消订阅之前的那个Observable。这意味着，在 后来那个Observable产生之后到它开始发射数据之前的这段
     * 时间里，前一个Observable发射 的数据将被丢弃
     * <p>
     * 有点像时钟周期一样，每当前一个 Observable 发射数据后，另一个 Observable 的数据从头开始发射
     *
     * <p>
     * call1: 0
     * call2: 0
     * 收到消息==0 == 消息线程为：RxComputationThreadPool-2
     * call2: 1
     * 收到消息==10 == 消息线程为：RxComputationThreadPool-2
     * call2: 2
     * 收到消息==20 == 消息线程为：RxComputationThreadPool-2
     * call1: 1
     * call2: 0
     * 收到消息==0 == 消息线程为：RxComputationThreadPool-3
     * call2: 1
     * 收到消息==10 == 消息线程为：RxComputationThreadPool-3
     * call2: 2
     * 收到消息==20 == 消息线程为：RxComputationThreadPool-3
     * call2: 3
     * 收到消息==30 == 消息线程为：RxComputationThreadPool-3
     * call2: 4
     * 收到消息==40 == 消息线程为：RxComputationThreadPool-3
     * <p>
     * 前述结果是因为，当第一次结果为 20 时，前一个发射了新的数据，所以后一个 Observable 被丢弃了，又重新从0开始
     * 因为前一个 Observable 调用了 take(2) ，所以不再发射新的数据，后面调用了take(4) ,所以结果为 40 的时候就停止了
     */
    private void testSwitchOnNext() {
        Observable<Observable<Long>> observable = Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> apply(Long aLong) {
                        //每隔200毫秒产生一组数据（0,10,20,30,40)
                        Log.d(TAG, "call1: " + aLong);
                        return Observable.interval(0, 200, TimeUnit.MILLISECONDS).map(new Function<Long, Long>() {
                            @Override
                            public Long apply(Long aLong) {
                                Log.d(TAG, "call2: " + aLong);
                                return aLong * 10;
                            }
                        }).take(5);
                    }
                }).take(2);
        Observable.switchOnNext(observable).subscribe(mObserver);

    }

    /**
     * startWith 在发射数据之前先发射一个指定的数据序列 (头添加)，最后发射的数据永远排在最前面
     * concatWith 在发射数据后面发射一个指定的数据序列 (尾添加)
     *
     * 结果如下；
     *
     * 收到消息==15 == 消息线程为：main
     * 收到消息==500 == 消息线程为：main
     * 收到消息==100 == 消息线程为：main
     * 收到消息==200 == 消息线程为：main
     * 收到消息==0 == 消息线程为：main
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testStartWith() {
        Observable.range(0, 3).startWithArray(100, 200).startWith(500)
                .startWith(Observable.range(15, 1)).subscribe(mObserver);
    }

    /**
     * 使用一个函数按顺序结合多个 Observable 发射的数据项，然后发射这个函数返回的结果。
     * 它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据，假如两个Observable数据分布为4项，5项，则最终合并是4项
     * <p>
     * 结果如下；
     * <p>
     * 收到消息==observableA: 1 ==== observableB: 7 == 消息线程为：main
     * 收到消息==observableA: 2 ==== observableB: 8 == 消息线程为：main
     */
    private void testZip() {
        Observable<Integer> observableA = Observable.range(1, 2);   // 1 2
        Observable<Integer> observableB = Observable.range(7, 3);   // 7 8 9
        Observable.zip(observableA, observableB, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return "observableA: " + integer + " ==== " + "observableB: " + integer2;
            }
        }).subscribe(mObserver);
    }

    /**
     * 将多个Observables的输出合并，就好像它们是一个单个的Observable一样，合并的Observable发射的数据可能会交错（顺序发生变化），
     * 有一个类似的操作符 Concat 不会让数据交错，能够保持数据的顺序性。
     * 在此过程中任何一个原始Observable的onError通知都会被立即传递给观察者，而且会终止合并后的Observable。
     * <p>
     * 结果如下；
     * <p>
     * 收到消息==1 == 消息线程为：main
     * 收到消息==2 == 消息线程为：main
     * 收到消息==5 == 消息线程为：main
     * 收到消息==6 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testMerge() {
//        Observable<Integer> observableA = Observable.range(1, 2);
//        Observable<Integer> observableB = Observable.range(5, 2);
//        Observable.merge(observableA, observableB).subscribe(mObserver);

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("100");
                emitter.onError(new Throwable("error"));
            }
        });

        Observable<Integer> observable2 = Observable.range(0, 3);

//         mergeDelayError 它会保留	onError	通知直到合并后的Observable所有的数据发射完成，在那时它才会把 onError 传递给观察者
        Observable.mergeDelayError(observable, observable2).subscribe(mObserver);

    }

    /**
     * 该操作符只要在另一个Observable发射的数据定义的时间窗口内，这个Observable发射了一条数据，就结合两个Observable发射的数据
     * 即一个 Observable 的每个数据跟基座的 Observable 的所有数据都结合一次，是一对多的关系。先发射数据的 Observable 为基座。
     * <p>
     * 结果如下；
     * <p>
     * observableB==7 == 消息线程为：main
     * observableB==8 == 消息线程为：main
     * observableA==1 == 消息线程为：RxComputationThreadPool-1
     * observableA ==1observableB ==7== 消息线程为：RxComputationThreadPool-1
     * 收到消息==8 == 消息线程为：RxComputationThreadPool-1-1
     * observableA ==1observableB ==8== 消息线程为：RxComputationThreadPool-1
     * 收到消息==9 == 消息线程为：RxComputationThreadPool-1-1
     * observableA==2 == 消息线程为：RxComputationThreadPool-1
     * observableA ==2observableB ==7== 消息线程为：RxComputationThreadPool-1-1
     * 收到消息==9 == 消息线程为：RxComputationThreadPool-1
     * observableA ==2observableB ==8== 消息线程为：RxComputationThreadPool-1
     * 收到消息==10 == 消息线程为：RxComputationThreadPool-1-1
     * 完成 == 完成线程为：RxComputationThreadPool-1
     */
    private void testJoin() {
        Observable<Integer> observableA = Observable.range(1, 2).delay(1, TimeUnit.SECONDS);
        Observable<Integer> observableB = Observable.range(7, 2);
        observableA.join(observableB, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                Log.d(TAG, "observableA==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
                return Observable.just(integer).delay(1, TimeUnit.SECONDS);
            }
        }, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                Log.d(TAG, "observableB==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
                return Observable.just(integer).delay(1, TimeUnit.SECONDS);
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                Log.d(TAG, "observableA ==" + integer + "observableB ==" + integer2 + "== 消息线程为：" + Thread.currentThread().getName());
                return integer + integer2;
            }
        }).subscribe(mObserver);
    }

    /**
     * 当多个 Observable 中的任何一个发射了数据时，使用一个函数结合每个Observable发射的最近数据项，并且基于这个函数的结果发射数据
     * <p>
     * 结果如下；
     * <p>
     * 收到消息==observableA:1=====observableB:14=====observableC:104===== == 消息线程为：RxComputationThreadPool-1
     * 收到消息==observableA:2=====observableB:14=====observableC:104===== == 消息线程为：RxComputationThreadPool-1
     * 收到消息==observableA:3=====observableB:14=====observableC:104===== == 消息线程为：RxComputationThreadPool-1
     * 收到消息==observableA:4=====observableB:14=====observableC:104===== == 消息线程为：RxComputationThreadPool-1
     * 完成 == 完成线程为：RxComputationThreadPool-1
     */
    private void testCombineLatest() {
        Observable<Integer> observableA = Observable.range(1, 4).delay(1, TimeUnit.SECONDS);
        Observable<Integer> observableB = Observable.range(10, 5);
        Observable<Integer> observableC = Observable.range(100, 5);

        Observable.combineLatest(observableA, observableB, observableC, new Function3<Integer, Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2, Integer integer3) throws Exception {
                return "observableA:" + integer + "=====" + "observableB:" + integer2 + "=====" + "observableC:" + integer3 + "=====";
            }
        }).subscribe(mObserver);
    }

}

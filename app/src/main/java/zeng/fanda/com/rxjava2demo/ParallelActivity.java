package zeng.fanda.com.rxjava2demo;

import android.util.Log;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.schedulers.Schedulers;
import zeng.fanda.com.rxjava2demo.transformers.TransformerUtil;

/**
 * 并行相关演示
 * <p>
 * 并行：多个处理器或多核的处理器同进处理多个不同的任务，是同时发生的多个并发事件，具有并发的含义
 * <p>
 * 并发：一个处理器同进处理多个不同的任务
 *
 * @author 曾凡达
 * @date 2019/7/19
 */
public class ParallelActivity extends BaseActivity {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
//        testParalleByFlatMap();
        testParalleByFlatMapWithExecutor();
//        testParalleByFlowable();
    }

    /**
     * 通过 ParalleFlowable 来实现并行效果，优先使用 ParalleFlowable 来实现并行，对于无法使用 ParalleFlowable 的操作符，则可以
     * 使用 FlatMap 的方式来实现并行
     */
    private void testParalleByFlowable() {
        ParallelFlowable<Integer> parallelFlowable = Flowable.range(1, 100).parallel();
        // 需要调用 sequential 操作符返回到顺序流
        Disposable disposable = parallelFlowable.runOn(Schedulers.io()).sequential().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
            }
        });

    }

    /**
     * 通过 FlatMap 操作符来实现并行效果，发射的数据会交错，用自定义的调度器
     */
    private void testParalleByFlatMapWithExecutor() {
        int threadNum = Runtime.getRuntime().availableProcessors() + 1;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        Observable.range(1, 100).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                return Observable.just(integer).subscribeOn(Schedulers.from(executorService));
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                // 最后需要执行 shutdown 来关闭
                executorService.shutdown();
            }
        }).subscribe(mObserver);
    }

    /**
     * 通过 FlatMap 操作符来实现并行效果，发射的数据会交错
     */
    private void testParalleByFlatMap() {
        Observable.range(1, 100).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                return Observable.just(integer).subscribeOn(Schedulers.computation());
            }
        }).subscribe(mObserver);
    }


}

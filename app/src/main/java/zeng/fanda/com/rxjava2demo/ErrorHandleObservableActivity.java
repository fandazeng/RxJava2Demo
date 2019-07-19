package zeng.fanda.com.rxjava2demo;

import android.util.Log;

import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * 错误处理演示
 *
 * @author 曾凡达
 * @date 2019/7/11
 */
public class ErrorHandleObservableActivity extends BaseActivity {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
//        testOnErrorReturn();

//        testOnErrorResumeNext();

//        testOnExceptionResumeNext();

        testRetry();
    }

    /**
     * Retry	操作符不会将原始Observable的	onError	通知传递给观察者，它会订阅这个 Observable，再给它一次机会无错误地完成它的数据序列。
     * Retry	总是传递	onNext	通知给观 察者，由于重新订阅，可能会造成数据项重
     * retry	操作符默认在	trampoline	调度器上执行。
     */
    private void testRetry() {
        // 无论收到多少次onError	通知，无参数版本的	retry	都会继续订阅并发射原始Observable，会一直重试，不会停止除非不报错
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                emitter.onNext(100);
//                Log.d(TAG, "重试线程为：" + Thread.currentThread().getName());
//                emitter.onError(new Throwable("测试错误"));
//            }
//        }).retry().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);

        // 接受单个	count	参数的	retry	会最多重新订阅指定的次数，如果次数超了，
        // 它不会尝试再次 订阅，它会把最新的一个	onError	通知传递给它的观察者。

        /*
        订阅线程为：main
        收到消息==100 == 消息线程为：main
        重试线程为：main
        收到消息==100 == 消息线程为：main
        重试线程为：main
        收到消息==100 == 消息线程为：main
        重试线程为：main
        收到消息==100 == 消息线程为：main
        重试线程为：main
        错误:测试错误 == 错误线程为：main
         */
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                emitter.onNext(100);
//                Log.d(TAG, "重试线程为：" + Thread.currentThread().getName());
//                emitter.onError(new Throwable("测试错误"));
//            }
//        }).retry(3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);


        // 	retryWhen	观察它的结果再决定是不是要重新订阅原 始的Observable。如果这个Observable发射了一项数据，它就重新订阅，
        // 	如果这个 Observable发射的是	onError	通知，它就将这个通知传递给观察者然后终止。
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d(TAG, "重试线程为：" + Thread.currentThread().getName());
                emitter.onError(new Throwable("测试错误"));
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Throwable> throwableObservable) throws Exception {
                return Observable.range(1, 3).concatWith(new CompletableSource() {
                    @Override
                    public void subscribe(CompletableObserver co) {
                        co.onError(new Throwable("发身一个Error通知"));
                    }
                });
            }
        }).subscribe(mObserver);

    }

    /**
     * 跟 onErrorResumeNext  类似 ，但是，如果	onError	收到 的	Throwable	不是一个	Exception	，
     * 它会将错误传递给观察者的	onError	方法，不会使用备用 的Observable
     */
    private void testOnExceptionResumeNext() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(100);
                // 这里用的是 Exception ，所以可以用备用 的 Observable
                emitter.onError(new Exception("测试错误"));
                emitter.onComplete();
            }
        }).onExceptionResumeNext(Observable.just(0)).subscribe(mObserver);
    }

    /**
     * 让Observable在遇到错误时开始发射第二个Observable的数据序列。
     */
    private void testOnErrorResumeNext() {
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                emitter.onNext(100);
//                emitter.onError(new Throwable("测试错误"));
//                emitter.onComplete();
//            }
//        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
//            @Override
//            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
//                return Observable.just(0);
//            }
//        }).subscribe(mObserver);

        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(100);
                emitter.onError(new Throwable("测试错误"));
                emitter.onComplete();
            }
        }).onErrorResumeNext(Observable.just(0)).subscribe(mObserver);

    }

    /**
     * 让Observable遇到错误时发射一个特殊的项并且正常终止。
     * <p>
     * onErrorReturn	方法返回一个镜像原有Observable行为的新Observable，后者会忽略前者 的	onError	调用，
     * 不会将错误传递给观察者，作为替代，它会发发射一个特殊的项并调用观 察者的	onCompleted	方法
     * <p>
     * 收到消息==100 == 消息线程为：main
     * 收到消息==0 == 消息线程为：main
     * 完成 == 完成线程为：main
     */
    private void testOnErrorReturn() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(100);
                emitter.onError(new Throwable("测试错误"));
                emitter.onComplete();
            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                return 0;
            }
        }).subscribe(mObserver);

        // 这里的结果等价于上面的方法
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(100);
                emitter.onError(new Throwable("测试错误"));
                emitter.onComplete();
            }
        }).onErrorReturnItem(0).subscribe(mObserver);
    }


}

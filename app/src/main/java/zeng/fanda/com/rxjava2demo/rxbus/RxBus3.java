package zeng.fanda.com.rxjava2demo.rxbus;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 *  在第二版的基础上，增加了对异常的处理，这里用到了一个第三方库， RxRelay
 *
 *  https://github.com/JakeWharton/RxRelay
 *
 *  RxRelay 中的各个 Relay ，即是 Observable 类型，也是 Consumer 的类型 ，它们是一个没有 onComplete 和 onError 的 subject
 *
 *  注意：没有背压处理了，这个版本，其实基本上用不到背压
 *
 *  不足： 没有粘性事件处理
 *
 * @author 曾凡达
 * @date 2019/7/29
 */
public class RxBus3 {

    private final Relay<Object> mBus;

    private RxBus3() {
        mBus = PublishRelay.create().toSerialized();
    }

    public static RxBus3 get() {
        return Holder.BUS;
    }

    public void post(Object obj) {
        mBus.accept(obj);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    public boolean hasSubscribers() {
        return mBus.hasObservers();
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext, Consumer onError,
                                   Action onComplete, Consumer onSubscribe) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError, onComplete, onSubscribe);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext, Consumer onError,
                                   Action onComplete) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext, Consumer onError) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext) {
        return toObservable(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError,
                                   Action onComplete, Consumer onSubscribe) {
        return toObservable(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError, onComplete, onSubscribe);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError,
                                   Action onComplete) {
        return toObservable(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError) {
        return toObservable(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError);
    }

    public void unregister(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private static class Holder {
        private static final RxBus3 BUS = new RxBus3();
    }
}

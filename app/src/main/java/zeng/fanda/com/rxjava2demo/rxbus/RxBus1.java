package zeng.fanda.com.rxjava2demo.rxbus;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 *  第一版 Rxbus 实现 ，完成了最基本的功能，也考虑了多线程的处理，但是没有考虑背压的情况
 *
 * @author 曾凡达
 * @date 2019/7/29
 */
public class RxBus1 {

    private final Subject<Object> mBus;

    private RxBus1() {
        mBus = PublishSubject.create().toSerialized();
    }

    public static RxBus1 get() {
        return Holder.BUS;
    }

    public void post(Object obj) {
        mBus.onNext(obj);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    private static class Holder {
        private static final RxBus1 BUS = new RxBus1();
    }
}

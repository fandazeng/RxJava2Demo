package zeng.fanda.com.rxjava2demo.rxbus;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 *  第二版 Rxbus 实现 ，完成了最基本的功能，也考虑了多线程的处理，也考虑背压的情况
 *  但是，没有对异常进行处理，一但遇到了异步，那么订阅者就会无法再收到事件
 *
 * @author 曾凡达
 * @date 2019/7/29
 */
public class RxBus2 {

    private final FlowableProcessor<Object> mBus;

    private RxBus2() {
        mBus = PublishProcessor.create().toSerialized();
    }

    public static RxBus2 get() {
        return Holder.BUS;
    }

    public void post(Object obj) {
        mBus.onNext(obj);
    }

    public <T> Flowable<T> toFlowable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Flowable<Object> toFlowable() {
        return mBus;
    }

    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    private static class Holder {
        private static final RxBus2 BUS = new RxBus2();
    }
}

package zeng.fanda.com.rxjava2demo.transformers;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 曾凡达
 * @date 2019/7/19
 */
public class TransformerUtil {

    private final static ObservableTransformer mObservableTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private final static FlowableTransformer mFlowableTransformer = new FlowableTransformer() {
        @Override
        public Publisher apply(Flowable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    public static <T> ObservableTransformer<T, T> io_main() {
        return mObservableTransformer;
    }

    public static <T> FlowableTransformer<T, T> io_main_flowable() {
        return mFlowableTransformer;
    }
}

package zeng.fanda.com.rxjava2demo;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import zeng.fanda.com.rxjava2demo.transformers.TransformerUtil;

/**
 * 转换器相关演示
 *
 * @author 曾凡达
 * @date 2019/7/19
 */
public class ComposeActivity extends BaseActivity {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
        testCompose();
        testCompose();
        testCompose();
    }


    /**
     * 面向的对象是源 Observable ，进行统一的转换操作，可以给不同的 Observable 复用，达到代码重用的效果
     */
    private void testCompose() {
        Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
                .compose(TransformerUtil.<Long>io_main()).subscribe(mObserver);
    }


}

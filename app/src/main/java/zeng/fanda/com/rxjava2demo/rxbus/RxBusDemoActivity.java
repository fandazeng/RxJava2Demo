package zeng.fanda.com.rxjava2demo.rxbus;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import zeng.fanda.com.rxjava2demo.BaseActivity;
import zeng.fanda.com.rxjava2demo.R;
import zeng.fanda.com.rxjava2demo.rxbus.event.TestEvent;
import zeng.fanda.com.rxjava2demo.transformers.TransformerUtil;

/**
 * @author 曾凡达
 * @date 2019/7/29
 */
public class RxBusDemoActivity extends BaseActivity {

    @BindView(R.id.tv_content)
    TextView mContent;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_rxbus;
    }

    @Override
    protected void initData() {
        Disposable disposable = RxBus1.get().toObservable(TestEvent.class).compose(TransformerUtil.io_main()).subscribe(new Consumer<TestEvent>() {
            @Override
            public void accept(TestEvent testEvent) throws Exception {
                mContent.setText(testEvent.content);
            }
        });

        Disposable disposable2 = RxBus2.get().toFlowable(TestEvent.class).compose(TransformerUtil.io_main_flowable()).subscribe(new Consumer<TestEvent>() {
            @Override
            public void accept(TestEvent testEvent) throws Exception {
                mContent.setText(testEvent.content);
            }
        });

        Disposable disposable3 = RxBus3.get().register(TestEvent.class, new Consumer<TestEvent>() {
            @Override
            public void accept(TestEvent testEvent) throws Exception {
                // 空指针，不会 Crash
                String nullPoint = null;
                System.out.println(nullPoint.substring(0));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) throws Exception {
                // 会回调到这里，能够减少 APP 的 crash
                mContent.setText(t.getMessage());
            }
        });

        RxBus4.get().postSticky(new TestEvent("这是粘性事件"));

        addDispose(disposable, disposable2, disposable3);
    }

    @OnClick({R.id.btn_post, R.id.btn_post2, R.id.btn_post3, R.id.btn_post4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_post:
                RxBus1.get().post(new TestEvent("发送消息了"));
                break;
            case R.id.btn_post2:
                RxBus2.get().post(new TestEvent("发送消息了2"));
                break;
            case R.id.btn_post3:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RxBus3.get().post(new TestEvent(Thread.currentThread().getName() + "发送消息了3"));
                    }
                }).start();

                break;
            case R.id.btn_post4:
                Disposable disposable4 = RxBus4.get().registerSticky(TestEvent.class, new Consumer<TestEvent>() {
                    @Override
                    public void accept(TestEvent testEvent) throws Exception {
                        mContent.setText(testEvent.content);
                    }
                });
                addDispose(disposable4);
                break;
            default:
                break;
        }
    }
}

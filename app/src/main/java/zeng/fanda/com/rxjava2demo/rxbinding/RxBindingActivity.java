package zeng.fanda.com.rxjava2demo.rxbinding;

import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import zeng.fanda.com.rxjava2demo.BaseActivity;
import zeng.fanda.com.rxjava2demo.R;

/**
 * @author 曾凡达
 * @date 2019/7/19
 */
public class RxBindingActivity extends BaseActivity {

    private long MAX_TIME = 30;

    @BindView(R.id.tv_click)
    Button mButton;

    @BindView(R.id.tv_login)
    Button mLogin;

    @BindView(R.id.et_edit)
    EditText mEditText;

    @BindView(R.id.et_phone)
    EditText mPhone;

    @BindView(R.id.et_password)
    EditText mPassword;

    @BindView(R.id.tv_get_code)
    TextView mGetCode;

    @BindView(R.id.tv_share)
    Button mShare;

    private CheckResult mCheckResult;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_binding;
    }

    @Override
    protected void initData() {
//        testView();
//        initCheckData();
//        testLogin();
//        testGetCode();
        testShare();
    }

    /**
     * 使用 share 操作符，进行多次监听
     */
    private void testShare() {
        Observable<Object> shareObservable = RxView.clicks(mShare).share();
        Disposable disposable = shareObservable.subscribe(new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                showMessage("第一次监听");
            }
        });
        Disposable disposable2 = shareObservable.subscribe(new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                showMessage("第二次监听");
            }
        });
        addDispose(disposable, disposable2);
    }

    /**
     * 获取验证码
     */
    private void testGetCode() {
        Disposable disposable = RxView.clicks(mGetCode).throttleFirst(MAX_TIME, TimeUnit.SECONDS)
                .flatMap((Function<Object, ObservableSource<Long>>) o -> {
                    // 进行短信验证码的网络请求，可以在获取成功后再倒计时
                    mGetCode.setEnabled(false);
                    mGetCode.setText(MAX_TIME + "秒");
                    mGetCode.setTextColor(Color.parseColor("#333333"));
                    return Observable.intervalRange(1, 30, 0, 1, TimeUnit.SECONDS);
                }).map(aLong -> MAX_TIME - aLong).doOnError(throwable -> showMessage(throwable.getMessage()))
                .observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                    if (aLong == 0) {
                        mGetCode.setEnabled(true);
                        mGetCode.setText("获取验证码");
                        mGetCode.setTextColor(Color.parseColor("#ff6602"));
                    } else {
                        mGetCode.setText(aLong + "秒");
                    }
                });

        addDispose(disposable);
    }

    /**
     * 登录
     */
    private void testLogin() {
        Disposable disposable = RxView.clicks(mLogin).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    Log.d(TAG, " == 消息线程为：" + Thread.currentThread().getName());
                    if (mCheckResult == null) return;
                    if (mCheckResult.flag) {
                        showMessage("登录成功");
                    } else {
                        showMessage(mCheckResult.message);
                    }
                });
        addDispose(disposable);
    }

    /**
     * 检查手机号和密码
     */
    private void initCheckData() {
        Disposable disposable = Observable.combineLatest(RxTextView.textChanges(mPhone), RxTextView.textChanges(mPassword), new BiFunction<CharSequence, CharSequence, CheckResult>() {

            CheckResult checkResult;

            @Override
            public CheckResult apply(CharSequence phone, CharSequence password) throws Exception {
                Log.d(TAG, " == 消息线程为：" + Thread.currentThread().getName());
                if (phone.length() > 0 && password.length() > 0) {
                    mLogin.setBackgroundColor(Color.parseColor("#ff6602"));
                } else {
                    mLogin.setBackgroundColor(Color.parseColor("#333333"));
                }

                checkResult = new CheckResult();
                if (phone.length() == 0) {
                    checkResult.flag = false;
                    checkResult.message = "手机号不能为空";
                } else if (password.length() == 0) {
                    checkResult.flag = false;
                    checkResult.message = "密码不能为空";
                } else if (phone.length() != 11) {
                    checkResult.flag = false;
                    checkResult.message = "手机号需要11位";
                }

                return checkResult;
            }
        }).subscribe(checkResult -> {
            Log.d(TAG, " == 消息线程为：" + Thread.currentThread().getName());
            mCheckResult = checkResult;
        });

        addDispose(disposable);
    }

    /**
     * 测试一些 View 事件
     */
    private void testView() {
        Disposable click = RxView.clicks(mButton).throttleFirst(1, TimeUnit.SECONDS).subscribe(o -> showMessage(mButton.getText().toString()));
        Disposable longClick = RxView.longClicks(mButton).subscribe(o -> showMessage(mButton.getText().toString()));
        Disposable textChange = RxTextView.textChanges(mEditText).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                mButton.setText(charSequence);
            }
        });


    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

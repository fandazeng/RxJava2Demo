package zeng.fanda.com.rxjava2demo;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import zeng.fanda.com.rxjava2demo.adapter.MainAdapter;
import zeng.fanda.com.rxjava2demo.rxbinding.RxBindingActivity;

/**
 * @author 曾凡达
 * @date 2019/7/17
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    MainAdapter mMainAdapter;

    List<String> mDatas;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        initDatas();
        initRecyclerView();
        initClicks();
    }

    private void initDatas() {
        mDatas = new ArrayList<>();
        mDatas.add("创建 操作符");
        mDatas.add("辅助 操作符");
        mDatas.add("过滤 操作符");
        mDatas.add("错误 操作符");
        mDatas.add("变换 操作符");
        mDatas.add("组合 操作符");
        mDatas.add("布尔 操作符");
        mDatas.add("连接 操作符");
        mDatas.add("背压相关操作");
        mDatas.add("转换器相关操作");
        mDatas.add("并行 相关操作");
        mDatas.add("RxBinding 使用场景");
        mDatas.add("Subject 使用");
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mMainAdapter = new MainAdapter();
        mRecyclerView.setAdapter(mMainAdapter);
        mMainAdapter.setNewData(mDatas);
    }

    private void initClicks() {
        mMainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.setClass(MainActivity.this, CreateObservableActivity.class);
                        break;
                    case 1:
                        intent.setClass(MainActivity.this, HelpObservableActivity.class);
                        break;
                    case 2:
                        intent.setClass(MainActivity.this, FilterObservableActivity.class);
                        break;
                    case 3:
                        intent.setClass(MainActivity.this, ErrorHandleObservableActivity.class);
                        break;
                    case 4:
                        intent.setClass(MainActivity.this, TransforObservableActivity.class);
                        break;
                    case 5:
                        intent.setClass(MainActivity.this, CombineObservableActivity.class);
                        break;
                    case 6:
                        intent.setClass(MainActivity.this, BooleanObservableActivity.class);
                        break;
                    case 7:
                        intent.setClass(MainActivity.this, ConnectObservableActivity.class);
                        break;
                    case 8:
                        intent.setClass(MainActivity.this, BackPressureActivity.class);
                        break;
                    case 10:
                        intent.setClass(MainActivity.this, ParallelActivity.class);
                        break;
                    case 11:
                        intent.setClass(MainActivity.this, RxBindingActivity.class);
                        break;
                        case 12:
                        intent.setClass(MainActivity.this, SubjectActivity.class);
                        break;
                }
                intent.putExtra(INTENT_KEY, mDatas.get(position));
                startActivity(intent);
            }
        });
    }
}

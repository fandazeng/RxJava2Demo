package zeng.fanda.com.rxjava2demo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import zeng.fanda.com.rxjava2demo.R;

/**
 * @author 曾凡达
 * @date 2019/7/17
 */
public class MainAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MainAdapter() {
        super(R.layout.activity_main_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_content, item);
    }

}

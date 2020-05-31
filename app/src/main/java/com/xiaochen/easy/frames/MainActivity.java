package com.xiaochen.easy.frames;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.xiaochen.butterknife.annotation.BindView;
import com.xiaochen.butterknife.core.EasyButterKnife;
import com.xiaochen.common.base.BaseActivity;
import com.xiaochen.common.base.RouterPathConstant;
import com.xiaochen.easy.core.EasyRouter;
import com.xiaochen.module.easy.okhttp.OkHttpMainActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_1)
    Button btn1;
    @BindView(R.id.btn_2)
    Button btn2;
    @BindView(R.id.btn_3)
    Button btn3;
    @BindView(R.id.btn_4)
    Button btn4;
    @BindView(R.id.btn_5)
    Button btn5;

    @Override
    protected int getLayoutResId() {
        return R.layout.app_activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        EasyButterKnife.bind(this);
    }


    @Override
    public void initListener() {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                EasyRouter.getInstance()
                        .build(RouterPathConstant.AROUTER_ACTIVITY)
                        .navigation(this);
                break;
            case R.id.btn_2:
                Intent intent = new Intent(this, OkHttpMainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_3:
                break;
            case R.id.btn_4:
                break;
            case R.id.btn_5:
                break;
            default:
        }
    }
}

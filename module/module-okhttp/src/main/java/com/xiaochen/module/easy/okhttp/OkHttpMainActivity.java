package com.xiaochen.module.easy.okhttp;

import android.view.View;

import com.xiaochen.common.base.BaseActivity;
import com.xiaochen.easy.okhttp.Callback;
import com.xiaochen.easy.okhttp.OkHttpClient;
import com.xiaochen.easy.okhttp.RealCall;
import com.xiaochen.easy.okhttp.Request;
import com.xiaochen.easy.okhttp.Response;

import java.io.IOException;
import java.net.MalformedURLException;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
import timber.log.Timber;

public class OkHttpMainActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.okhttp_activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        View button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            test();
        });
    }

    private void test() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        String url = "https://www.wanandroid.com/article/list/1/json";
        Request request = null;
        try {
            request = new Request.Builder()
                    .url(url)
                    .get()  //默认为GET请求，可以不写
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RealCall call = builder.build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(RealCall call, IOException e) {
                Timber.tag("onFailure").e(e);
            }

            @Override
            public void onResponse(RealCall call, Response response) throws IOException {
                String body = response.getBody();
                Timber.tag("onResponse").e("%s", body);
            }
        });
    }
}

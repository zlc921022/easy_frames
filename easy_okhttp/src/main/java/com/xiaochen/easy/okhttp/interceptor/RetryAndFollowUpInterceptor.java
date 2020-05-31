package com.xiaochen.easy.okhttp.interceptor;

import android.util.Log;

import com.xiaochen.easy.okhttp.RealCall;
import com.xiaochen.easy.okhttp.RealInterceptorChain;
import com.xiaochen.easy.okhttp.Response;

import java.io.IOException;

public class RetryAndFollowUpInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.e("interceptor", "重试拦截器");
        RealInterceptorChain real = (RealInterceptorChain) chain;
        RealCall call = real.getCall();
        for (int i = 0; i < call.getClient().getRetry(); i++) {
            try {
                return chain.proceed();
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        return null;
    }
}

package com.xiaochen.easy.okhttp.interceptor;

import android.util.Log;

import com.xiaochen.easy.okhttp.connection.HttpCodec;
import com.xiaochen.easy.okhttp.RealInterceptorChain;
import com.xiaochen.easy.okhttp.Request;
import com.xiaochen.easy.okhttp.RequestBody;
import com.xiaochen.easy.okhttp.Response;

import java.io.IOException;
import java.util.Map;

public class BridgeInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.e("interceprot", "Http头拦截器....");
        RealInterceptorChain real = (RealInterceptorChain) chain;
        Request request = real.getCall().getRequest();
        Map<String, String> heads = request.getHeads();
        heads.put(HttpCodec.HEAD_HOST, request.getUrl().getHost());
        heads.put(HttpCodec.HEAD_CONNECTION, HttpCodec.HEAD_VALUE_KEEP_ALIVE);
        RequestBody body = request.getRequestBody();
        if (null != body) {
            String contentType = body.contentType();
            if (contentType != null) {
                heads.put(HttpCodec.HEAD_CONTENT_TYPE, contentType);
            }
            long contentLength = body.contentLength();
            if (contentLength != -1) {
                heads.put(HttpCodec.HEAD_CONTENT_LENGTH, Long.toString(contentLength));
            }
        }
        return chain.proceed();
    }
}

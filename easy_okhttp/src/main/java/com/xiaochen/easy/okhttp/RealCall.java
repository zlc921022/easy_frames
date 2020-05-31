package com.xiaochen.easy.okhttp;

import com.xiaochen.easy.okhttp.interceptor.BridgeInterceptor;
import com.xiaochen.easy.okhttp.interceptor.CallServerInterceptor;
import com.xiaochen.easy.okhttp.interceptor.ConnectInterceptor;
import com.xiaochen.easy.okhttp.interceptor.Interceptor;
import com.xiaochen.easy.okhttp.interceptor.RetryAndFollowUpInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RealCall {
    OkHttpClient client;
    Request request;
    private boolean executed;

    public RealCall(OkHttpClient client, Request request) {
        this.client = client;
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public OkHttpClient getClient() {
        return client;
    }

    /**
     * 同步执行
     *
     * @return
     */
    public Response execute() throws IOException {
        synchronized (this) {
            if (executed) {
                throw new IllegalStateException("Already executed");
            }
            executed = true;
        }
        try {
            client.getDispatcher().execute(this);
            return getResponseWithInterceptorChain();
        }finally {
            client.getDispatcher().finished(this);
        }
    }

    /**
     * 异步执行
     *
     * @param callback
     */
    public void enqueue(Callback callback) {
        synchronized (this) {
            if (executed) {
                throw new IllegalStateException("Already executed");
            }
            executed = true;
        }
        client.getDispatcher().enqueue(new AsyncCall(callback));
    }

    private Response getResponseWithInterceptorChain() throws IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors);
        //负责失败重试以及重定向
        interceptors.add(new RetryAndFollowUpInterceptor());
        //请求时，对必要的Header进行一些添加，接收响应时，移除必要的Header
        interceptors.add(new BridgeInterceptor());
        //负责读取缓存直接返回、更新缓存
//        interceptors.add(new CacheInterceptor());
        // 负责和服务器建立连接
        interceptors.add(new ConnectInterceptor());
        // 负责向服务器发送请求数据、从服务器读取响应数据
        interceptors.add(new CallServerInterceptor());
        RealInterceptorChain chain = new RealInterceptorChain(interceptors, 0,this,null);
        return chain.proceed();
    }

    /**
     * 异步执行runnable
     */
    final class AsyncCall implements Runnable {
        Callback responseCallBack;

        AsyncCall(Callback callback) {
            this.responseCallBack = callback;
        }

        @Override
        public void run() {
            execute();
        }

        private void execute() {
            try {
                Response response = getResponseWithInterceptorChain();
                if (responseCallBack != null) {
                    responseCallBack.onResponse(RealCall.this, response);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (responseCallBack != null) {
                    responseCallBack.onFailure(RealCall.this, e);
                }
            } finally {
                client.getDispatcher().finished(this);
            }
        }

        public String host() {
            return request.url.host;
        }
    }

}

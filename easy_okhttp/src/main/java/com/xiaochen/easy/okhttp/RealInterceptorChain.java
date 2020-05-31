package com.xiaochen.easy.okhttp;

import com.xiaochen.easy.okhttp.interceptor.Interceptor;

import java.io.IOException;
import java.util.List;

public class RealInterceptorChain implements Interceptor.Chain {

    private List<Interceptor> interceptors;
    private int index;
    private RealCall call;
    private HttpConnection connection;
    final HttpCodec httpCodec = new HttpCodec();

    RealInterceptorChain(List<Interceptor> interceptors, int index, RealCall call, HttpConnection connection) {
        this.interceptors = interceptors;
        this.call = call;
        this.index = index;
        this.connection = connection;
    }

    public HttpConnection getConnection() {
        return connection;
    }

    public HttpCodec getHttpCodec() {
        return httpCodec;
    }

    public RealCall getCall() {
        return call;
    }

    @Override
    public Response proceed() throws IOException {
       return proceed(connection);
    }

    public Response proceed(HttpConnection connection) throws IOException {
        // 获取当前链的下一个节点
        RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, call, connection);
        // 获取当前拦截器 调用intercept传入下一个链节点
        Interceptor interceptor = interceptors.get(index);
        return interceptor.intercept(next);
    }
}

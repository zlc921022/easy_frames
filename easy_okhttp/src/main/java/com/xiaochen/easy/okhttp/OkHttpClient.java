package com.xiaochen.easy.okhttp;

import com.xiaochen.easy.okhttp.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class OkHttpClient {

    /**
     * 队列 任务分发
     */
    Dispatcher dispatcher;
    /**
     * 连接池
     */
    ConnectionPool connectionPool;
    /**
     * 连接时间
     */
    int connectTimeout;
    /**
     * 拦截器
     */
    List<Interceptor> interceptors;

    /**
     * 重试次数
     */
    int retry;

    public OkHttpClient(Builder builder) {
        this.dispatcher = builder.dispatcher;
        this.connectionPool = builder.connectionPool;
        this.connectTimeout = builder.connectTimeout;
        this.interceptors = builder.interceptors;
        this.retry = builder.retry;
    }

    public int getRetry() {
        return retry;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public RealCall newCall(Request request) {
        return new RealCall(this,request);
    }

    public static final class Builder {
        /**
         * 队列 任务分发
         */
        Dispatcher dispatcher;
        /**
         * 连接池
         */
        ConnectionPool connectionPool;
        /**
         * 连接时间
         */
        int connectTimeout;
        /**
         * 拦截器
         */
        List<Interceptor> interceptors = new ArrayList<>();

        //默认重试3次
        int retry = 3;

        public Builder() {
            dispatcher = new Dispatcher();
            connectionPool = new ConnectionPool();
            connectTimeout = 10_000;
        }

        public void setRetry(int retry) {
            this.retry = retry;
        }

        public Builder setDispatcher(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        public Builder setConnectionPool(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public OkHttpClient build() {
            return new OkHttpClient(this);
        }
    }
}

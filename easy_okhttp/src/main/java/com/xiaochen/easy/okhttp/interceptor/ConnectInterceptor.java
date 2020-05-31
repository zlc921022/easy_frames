package com.xiaochen.easy.okhttp.interceptor;

import android.util.Log;

import com.xiaochen.easy.okhttp.HttpConnection;
import com.xiaochen.easy.okhttp.HttpUrl;
import com.xiaochen.easy.okhttp.OkHttpClient;
import com.xiaochen.easy.okhttp.RealInterceptorChain;
import com.xiaochen.easy.okhttp.Request;
import com.xiaochen.easy.okhttp.Response;

import java.io.IOException;

/**
 * 连接拦截器
 */
public class ConnectInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.e("intercept","连接拦截器");
        RealInterceptorChain chain1 = (RealInterceptorChain) chain;
        Request request = chain1.getCall().getRequest();
        OkHttpClient client = chain1.getCall().getClient();
        HttpUrl url = request.getUrl();
        String host = url.getHost();
        int port = url.getPort();
        HttpConnection connection = client.getConnectionPool().get(host, port);
        if(connection == null){
            connection = new HttpConnection();
        }else{
            Log.e("call", "使用连接池......");
        }
        connection.setRequest(request);
        Response response = chain1.proceed(connection);
        if(response.isKeepAlive()){
            client.getConnectionPool().put(connection);
        }
        return response;
    }
}

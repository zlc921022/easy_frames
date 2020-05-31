package com.xiaochen.easy.okhttp.interceptor;

import android.text.TextUtils;
import android.util.Log;

import com.xiaochen.easy.okhttp.HttpCodec;
import com.xiaochen.easy.okhttp.HttpConnection;
import com.xiaochen.easy.okhttp.RealInterceptorChain;
import com.xiaochen.easy.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * 和服务器交互的拦截器
 */
public class CallServerInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.e("interceprot", "通信拦截器....");
        RealInterceptorChain chain1 = (RealInterceptorChain) chain;
        HttpCodec httpCodec = chain1.getHttpCodec();
        HttpConnection connection = chain1.getConnection();
        InputStream is = connection.call(httpCodec);
        //HTTP/1.1 200 OK 空格隔开的响应状态
        String readLine = httpCodec.readLine(is);
        Map<String, String> heads = httpCodec.readHeads(is);
        //是否保持连接
        boolean isKeepAlive = false;
        if (heads.containsKey(HttpCodec.HEAD_CONNECTION)) {
            isKeepAlive = TextUtils.equals(heads.get(HttpCodec.HEAD_CONNECTION),
                    HttpCodec.HEAD_VALUE_KEEP_ALIVE);
        }
        int contentLength = -1;
        if (heads.containsKey(HttpCodec.HEAD_CONTENT_LENGTH)) {
            contentLength = Integer.parseInt(Objects.requireNonNull(heads.get(HttpCodec.HEAD_CONTENT_LENGTH)));
        }
        //分块编码数据
        boolean isChunked = false;
        if (heads.containsKey(HttpCodec.HEAD_TRANSFER_ENCODING)) {
            isChunked = Objects.requireNonNull(heads.get(HttpCodec.HEAD_TRANSFER_ENCODING))
                    .equalsIgnoreCase(HttpCodec.HEAD_VALUE_CHUNKED);
        }

        String body = null;
        if (contentLength > 0) {
            byte[] bytes = httpCodec.readBytes(is, contentLength);
            body = new String(bytes);
        } else if (isChunked) {
            body = httpCodec.readChunked(is);
        }

        String[] split = readLine.split(" ");
        connection.updateLastUseTime();
        ;
        Response.Builder builder = new Response.Builder();
        builder.setCode(Integer.parseInt(split[1]))
                .setContentLength(contentLength)
                .setHeads(heads)
                .setKeepAlive(isKeepAlive)
                .setBody(body);
        return builder.build();
    }
}

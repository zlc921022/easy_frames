package com.xiaochen.easy.okhttp.connection;

import android.text.TextUtils;

import com.xiaochen.easy.okhttp.HttpUrl;
import com.xiaochen.easy.okhttp.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

public class HttpConnection {

    static final String HTTPS = "https";
    Socket socket;
    InputStream is;
    OutputStream os;
    Request request;
    long lastUseTime;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public InputStream call(HttpCodec httpCodec) throws IOException {
        try {
            createSocket();
            httpCodec.wirteRequest(os, request);
            return is;
        } catch (Exception e) {
            closeQuietly();
            throw new IOException(e);
        }
    }

    private void createSocket() throws IOException {
        if (null == socket || socket.isClosed()) {
            HttpUrl url = request.getUrl();
            if (url.getProtocol().equalsIgnoreCase(HTTPS)) {
                socket = SSLSocketFactory.getDefault().createSocket();
            } else {
                socket = new Socket();
            }
            socket.connect(new InetSocketAddress(url.getHost(), url.getPort()));
            os = socket.getOutputStream();
            is = socket.getInputStream();
        }
    }

    public void closeQuietly() {
        if (null != socket) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateLastUseTime() {
        //更新最后使用时间
        lastUseTime = System.currentTimeMillis();
    }

    public boolean isSameAddress(String host, int port) {
        if (null == socket) {
            return false;
        }
        return TextUtils.equals(
                socket.getInetAddress().getHostName(),
                host
        ) && socket.getPort() == port;
    }

}

package com.xiaochen.easy.okhttp;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpUrl {
    String protocol;  //协议http  https
    String host;      //192.6.2.3
    String file;    // 文件地址
    int port;     //端口

    public HttpUrl(String url) throws MalformedURLException {
        URL url1 = new URL(url);
        protocol = url1.getProtocol();
        host = url1.getHost();
        file = url1.getFile();
        port = url1.getPort() == -1 ? url1.getDefaultPort() : url1.getPort();
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

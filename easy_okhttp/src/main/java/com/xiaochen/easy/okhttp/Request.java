package com.xiaochen.easy.okhttp;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class Request {

    HttpUrl url;
    String method;
    RequestBody requestBody;
    Map<String, String> heads;

    public Request(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.requestBody = builder.requestBody;
        this.heads = builder.heads;
    }

    public HttpUrl getUrl() {
        return url;
    }

    public void setUrl(HttpUrl url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, String> getHeads() {
        return heads;
    }

    public void setHeads(Map<String, String> heads) {
        this.heads = heads;
    }

    public static final class Builder {

        HttpUrl url;
        String method;
        RequestBody requestBody;
        Map<String, String> heads;

        public Builder() {
            method = "GET";
            requestBody = new RequestBody();
            heads = new HashMap<>();
        }

        public Builder url(String address) throws MalformedURLException {
            url = new HttpUrl(address);
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setHeads(Map<String, String> heads) {
            this.heads = heads;
            return this;
        }

        public Builder get() {
            method = "GET";
            return this;
        }

        public Builder post(RequestBody body) {
            method = "POST";
            this.requestBody = body;
            return this;
        }


        public Builder setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public Builder setUrl(HttpUrl url) {
            this.url = url;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}

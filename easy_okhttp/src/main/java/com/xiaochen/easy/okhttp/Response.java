package com.xiaochen.easy.okhttp;

import java.util.HashMap;
import java.util.Map;

public class Response {

    int code;
    Map<String, String> heads;
    String body;
    boolean isKeepAlive;
    int contentLength = -1;

    public Response(Builder builder) {
        this.code = builder.code;
        this.contentLength = builder.contentLength;
        this.heads = builder.heads;
        this.body = builder.body;
        this.isKeepAlive = builder.isKeepAlive;
    }

    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    public String getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }

    public Map<String, String> getHeads() {
        return heads;
    }

    public static final class Builder {
        int code;
        int contentLength;
        Map<String, String> heads;
        String body;
        boolean isKeepAlive;

        public Builder() {
            heads = new HashMap<>();
        }

        public Builder setContentLength(int contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Builder setCode(int code) {
            this.code = code;
            return this;
        }

        public Builder setHeads(Map<String, String> heads) {
            this.heads = heads;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setKeepAlive(boolean keepAlive) {
            isKeepAlive = keepAlive;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}

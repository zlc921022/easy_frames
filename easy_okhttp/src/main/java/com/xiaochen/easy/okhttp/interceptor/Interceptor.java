package com.xiaochen.easy.okhttp.interceptor;

import com.xiaochen.easy.okhttp.Response;

import java.io.IOException;

public interface Interceptor {
    Response intercept(Chain chain) throws IOException;
    interface Chain{
        Response proceed() throws IOException;
    }
}

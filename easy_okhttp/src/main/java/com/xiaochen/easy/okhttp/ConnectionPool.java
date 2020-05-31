package com.xiaochen.easy.okhttp;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * keep-alive 就是浏览器和服务端之间保持长连接，这个连接是可以复用的。在HTTP1.1中是默认开启的。
 * <p>
 * 连接的复用为什么会提高性能呢？
 * <p>
 * (一次响应的过程) 通常我们在发起http请求的时候首先要完成tcp的三次握手，然后传输数据，最后再释放连接
 * <p>
 * 如果在高并发的请求连接情况下或者同个客户端多次频繁的请求操作，无限制的创建会导致性能低下。
 * 如果使用keep-alive，在timeout空闲时间内，连接不会关闭，相同重复的request将复用原先的connection，
 * 减少握手的次数，大幅提高效率。（并非keep-alive的timeout设置时间越长，就越能提升性能。
 * 长久不关闭会造成过多的僵尸连接和泄露连接出现）
 */
public class ConnectionPool {

    /**
     * 每个连接的最大存活时间
     */
    private final long keepAliveDuration;

    private final Deque<HttpConnection> connections = new ArrayDeque<>();

    private boolean cleanupRunning;

    public ConnectionPool() {
        this(1, TimeUnit.MINUTES);
    }

    public ConnectionPool(long keepAliveDuration, TimeUnit timeUnit) {
        this.keepAliveDuration = timeUnit.toMillis(keepAliveDuration);
    }

    /**
     * 垃圾回收线程
     * 线程池，用来检测闲置socket并对其进行清理
     */
    private static ThreadFactory factory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "okHttpClient ConnectionPool");
            thread.setDaemon(true);
            return thread;
        }
    };

    private static final Executor executor = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
            new SynchronousQueue<>(), factory
    );

    private final Runnable cleanupRunnable = new Runnable() {

        @Override
        public void run() {
            while (true){
                long waitTimes = cleanup(System.currentTimeMillis());
                if(waitTimes == -1){
                    return;
                }
                if(waitTimes > 0){
                    synchronized (ConnectionPool.this){
                        try {
                            ConnectionPool.this.wait(waitTimes);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    public HttpConnection get(String host,int port){
        Iterator<HttpConnection> it = connections.iterator();
        while (it.hasNext()){
            HttpConnection connection = it.next();
            if(connection.isSameAddress(host,port)){
                it.remove();
                return connection;
            }
        }
        return null;
    }

    public void put(HttpConnection connection){
        //执行检测清理
        if(!cleanupRunning){
            cleanupRunning = true;
            executor.execute(cleanupRunnable);
        }
        connections.add(connection);
    }


    /**
     * 检查需要移除的连接返回下次检查时间
     */
    long cleanup(long now){
        long longestIdleDuration = -1;
        synchronized (this){
            Iterator<HttpConnection> it = connections.iterator();
            while (it.hasNext()){
                HttpConnection connection = it.next();
                long idleDuration = connection.lastUseTime;
                if(idleDuration > keepAliveDuration){
                    connection.closeQuietly();
                    it.remove();
                    Log.e("Pool", "移出连接池");
                    continue;
                }
                if(longestIdleDuration < idleDuration){
                    longestIdleDuration = idleDuration;
                }
            }
            //下次检查时间
            if(longestIdleDuration >= 0){
                return keepAliveDuration - longestIdleDuration;
            }else{
                cleanupRunning = false;
                return longestIdleDuration;
            }
        }
    }
}

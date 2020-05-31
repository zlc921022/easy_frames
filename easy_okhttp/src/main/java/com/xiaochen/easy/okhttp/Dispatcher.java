package com.xiaochen.easy.okhttp;

import android.text.TextUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Dispatcher {

    //最多同时请求
    private int maxRequests;
    //同一个host同时最多请求
    private int maxRequestsPerHost;
    /**
     * 线程池
     */
    private ExecutorService executorService;
    /**
     * 正在执行的异步队列
     */
    private Deque<RealCall.AsyncCall> runningAsyncCalls = new ArrayDeque<>();
    /**
     * 等待执行的异步队列
     */
    private Deque<RealCall.AsyncCall> readyAsyncCalls = new ArrayDeque<>();
    /**
     * 正在运行的同步队列
     */
    private Deque<RealCall> runningSyncCalls = new ArrayDeque<>();

    Dispatcher() {
        this(64, 5);
    }

    Dispatcher(int maxRequests, int maxRequestsPerHost) {
        this.maxRequests = maxRequests;
        this.maxRequestsPerHost = maxRequestsPerHost;
    }


    /**
     * 同步执行
     * @param call
     */
    synchronized void execute(RealCall call) {
        runningSyncCalls.add(call);
    }

    /**
     * 异步执行
     * @param call
     */
    void enqueue(RealCall.AsyncCall call) {
        if (runningAsyncCalls.size() < maxRequests &&
                getSameHostCount(call) < maxRequestsPerHost) {
            runningAsyncCalls.add(call);
            executorService().execute(call);
        } else {
            readyAsyncCalls.add(call);
        }
    }

    public void finished(RealCall call) {
        synchronized (this) {
            runningSyncCalls.remove(call);
//            execute(runningSyncCalls.peek());
        }
    }

    public void finished(RealCall.AsyncCall call) {
        synchronized (this) {
            runningAsyncCalls.remove(call);
            //判断是否执行等待队列中的请求
            promoteCalls();
        }
    }

    /**
     * 判断是否执行等待队列中的请求
     */
    private void promoteCalls() {
        if (runningAsyncCalls.size() > maxRequests) {
            return;
        }
        if (readyAsyncCalls.isEmpty()) {
            return;
        }
        Iterator<RealCall.AsyncCall> it = readyAsyncCalls.iterator();
        while (it.hasNext()) {
            RealCall.AsyncCall call = it.next();
            if (getSameHostCount(call) < maxRequestsPerHost) {
                it.remove();
                runningAsyncCalls.add(call);
                executorService().execute(call);
            }
            if (runningAsyncCalls.size() > maxRequests) {
                return;
            }
        }
    }

    public int getSameHostCount(RealCall.AsyncCall asyncCall) {
        int count = 0;
        for (RealCall.AsyncCall call : runningAsyncCalls) {
            if (TextUtils.equals(call.host(), asyncCall.host())) {
                count++;
            }
        }
        return count;
    }

    public ExecutorService executorService() {
        if (executorService == null) {
            ThreadFactory factory = new ThreadFactory() {

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "okHttp dispatcher");
                }
            };
            /**
             *    1、corePoolSize：线程池中核心线程数的最大值
             *    2、maximumPoolSize：线程池中能拥有最多线程数
             *    3、keepAliveTime：表示空闲线程的存活时间  60秒
             *    4、表示keepAliveTime的单位。
             *    5、workQueue：它决定了缓存任务的排队策略。
             *      SynchronousQueue<Runnable>：此队列中不缓存任何一个任务。向线程池提交任务时，
             *      如果没有空闲线程来运行任务，则入列操作会阻塞。当有线程来获取任务时，
             *      出列操作会唤醒执行入列操作的线程。
             *    6、指定创建线程的工厂
             */
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60,
                    TimeUnit.SECONDS, new SynchronousQueue<>(), factory);
        }
        return executorService;
    }
}

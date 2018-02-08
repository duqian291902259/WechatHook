package site.duqian.wchook.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by duqian on 2017/5/11.
 */

public class ThreadManager {

    private static ThreadPoolProxy mBackgroundUploadPool = null;
    private static Object mBackgroundUploadLock = new Object();

    private static Map<String, ThreadPoolProxy> mMap = new HashMap<String, ThreadPoolProxy>();


    /** 获取后台线程池 */
    public static ThreadPoolProxy getBackgroundPool() {
        synchronized (mBackgroundUploadLock) {
            if (mBackgroundUploadPool == null) {
                mBackgroundUploadPool = new ThreadPoolProxy(5, 5, 5L);
            }
            return mBackgroundUploadPool;
        }
    }


    public static class ThreadPoolProxy {
        private ThreadPoolExecutor mPool;
        private int mCorePoolSize;
        private int mMaximumPoolSize;
        private long mKeepAliveTime;

        private ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            mCorePoolSize = corePoolSize;
            mMaximumPoolSize = maximumPoolSize;
            mKeepAliveTime = keepAliveTime;
        }

         /** 执行任务，当线程池处于关闭，将会重新创建新的线程池 */
        public synchronized void execute(Runnable run) {
            if (run == null) {
                return;
            }
            if (mPool == null || mPool.isShutdown()) {
                //ThreadFactory是每次创建新的线程工厂
                mPool = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            }
            mPool.execute(run);
        }


        /** 立刻关闭线程池，并且正在执行的任务也将会被中断 */
        public void stop() {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                mPool.shutdown();
            }
        }

        /** 平缓关闭单任务线程池，但是会确保所有已经加入的任务都将会被执行完毕才关闭 */
        public synchronized void shutdown() {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                mPool.shutdownNow();
            }
        }
    }
}

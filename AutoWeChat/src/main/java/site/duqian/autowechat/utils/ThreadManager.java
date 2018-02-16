/*
 * File Name: ThreadManager.java 
 * History:
 * Created by mwqi on 2014-4-4
 */
package site.duqian.autowechat.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * 一个简易的线程池管理类
 */
public class ThreadManager {
	public static final String DEFAULT_SINGLE_POOL_NAME = "DEFAULT_SINGLE_POOL_NAME";

	private static ThreadPoolProxy mLongPool = null;
	private static Object mLongLock = new Object();

	private static ThreadPoolProxy mShortPool = null;
	private static Object mShortLock = new Object();

    private static ThreadPoolProxy mBackgroundUploadPool = null;
	private static Object mBackgroundUploadLock = new Object();

	private static Map<String, ThreadPoolProxy> mMap = new HashMap<String, ThreadPoolProxy>();
	private static Object mSingleLock = new Object();


    /** 获取后台上传线程 */
	public static ThreadPoolProxy getBackgroundPool() {
		synchronized (mBackgroundUploadLock) {
			if (mBackgroundUploadPool == null) {
                mBackgroundUploadPool = new ThreadPoolProxy(5, 5, 5L);
			}
			return mBackgroundUploadPool;
		}
	}

	/** 获取一个用于执行长耗时任务的线程池，避免和短耗时任务处在同一个队列而阻塞了重要的短耗时任务，通常用来联网操作 */
	public static ThreadPoolProxy getLongPool() {
		synchronized (mLongLock) {
			if (mLongPool == null) {
				mLongPool = new ThreadPoolProxy(2, 2, 10L);
			}
			return mLongPool;
		}
	}

	/** 获取一个用于执行短耗时任务的线程池，避免因为和耗时长的任务处在同一个队列而长时间得不到执行，通常用来执行本地的IO/SQL */
	public static ThreadPoolProxy getShortPool() {
		synchronized (mShortLock) {
			if (mShortPool == null) {
				mShortPool = new ThreadPoolProxy(20, 20, 5L);
			}
			return mShortPool;
		}
	}

	/** 获取一个单线程池，所有任务将会被按照加入的顺序执行，免除了同步开销的问题 */
	public static ThreadPoolProxy getSinglePool() {
		return getSinglePool(DEFAULT_SINGLE_POOL_NAME);
	}

	/** 获取一个单线程池，所有任务将会被按照加入的顺序执行，免除了同步开销的问题 */
	public static ThreadPoolProxy getSinglePool(String name) {
		synchronized (mSingleLock) {
			ThreadPoolProxy singlePool = mMap.get(name);
			if (singlePool == null) {
				singlePool = new ThreadPoolProxy(1, 1, 5L);
				mMap.put(name, singlePool);
			}
			return singlePool;
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
				//参数说明
				//当线程池中的线程小于mCorePoolSize，直接创建新的线程加入线程池执行任务
				//当线程池中的线程数目等于mCorePoolSize，将会把任务放入任务队列BlockingQueue中
				//当BlockingQueue中的任务放满了，将会创建新的线程去执行，
				//但是当总线程数大于mMaximumPoolSize时，将会抛出异常，交给RejectedExecutionHandler处理
				//mKeepAliveTime是线程执行完任务后，且队列中没有可以执行的任务，存活的时间，后面的参数是时间单位
				//ThreadFactory是每次创建新的线程工厂
				mPool = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new AbortPolicy());
			}
			mPool.execute(run);
		}

		/** 取消线程池中某个还未执行的任务 */
		public synchronized void cancel(Runnable run) {
			if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
				mPool.getQueue().remove(run);
			}
		}

		/** 取消线程池中某个还未执行的任务 */
		public synchronized boolean contains(Runnable run) {
			if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
				return mPool.getQueue().contains(run);
			} else {
				return false;
			}
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

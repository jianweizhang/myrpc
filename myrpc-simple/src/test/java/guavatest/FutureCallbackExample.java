package guavatest;

import com.google.common.util.concurrent.*;
import java.util.concurrent.*;

/**
 * 使用guava实现异步回调 {@link java.util.concurrent.Future}
 * {@link com.google.common.util.concurrent.ListenableFuture}
 * {@link com.google.common.util.concurrent.FutureCallback}
 *
 * @author landon
 */
public class FutureCallbackExample {

	public static void main(String[] args) throws Exception {
		nativeFuture();
		Thread.sleep(3000L);

		guavaFuture();
		Thread.sleep(3000L);

		guavaFuture2();
	}


	public static void nativeFuture() throws Exception {
		// 原生的Future模式,实现异步
		ExecutorService nativeExecutor = Executors.newSingleThreadExecutor();
		Future<String> nativeFuture = nativeExecutor
				.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						// 使用sleep模拟调用耗时
						TimeUnit.SECONDS.sleep(1);
						return  "[" + Thread.currentThread().getName() +"]: 并发包Future返回结果" ;
					}
				});
		// Future只实现了异步，而没有实现回调.所以此时主线程get结果时阻塞.或者可以轮训以便获取异步调用是否完成
		System.out.println("[" + Thread.currentThread().getName() +"]====>"+ nativeFuture.get());

	}
	public static void guavaFuture() throws Exception {
		System.out.println("-------------------------------- 神秘的分割线 -----------------------------------");
		// 好的实现应该是提供回调,即异步调用完成后,可以直接回调.本例采用guava提供的异步回调接口,方便很多.
		ListeningExecutorService guavaExecutor = MoreExecutors
				.listeningDecorator(Executors.newSingleThreadExecutor());
		final ListenableFuture<String> listenableFuture = guavaExecutor
				.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						TimeUnit.SECONDS.sleep(1);
						return  "[" + Thread.currentThread().getName() +"]: guava的Future返回结果";
					}
				});

		// 注册监听器,即异步调用完成时会在指定的线程池中执行注册的监听器
		listenableFuture.addListener(new Runnable() {
			@Override
			public void run() {
				try {
					String logTxt = "[" + Thread.currentThread().getName() +"]: guava对返回结果进行异步CallBack(Runnable):"
							+ listenableFuture.get();
					System.out.println(logTxt);
				} catch (Exception e) {
				}
			}
		}, Executors.newSingleThreadExecutor());

		// 主线程可以继续执行,异步完成后会执行注册的监听器任务.
		System.out.println( "[" + Thread.currentThread().getName() +"]: guavaFuture1执行结束");
	}

	public static void guavaFuture2() throws Exception {
		System.out.println("-------------------------------- 神秘的分割线 -----------------------------------");
		// 除了ListenableFuture,guava还提供了FutureCallback接口,相对来说更加方便一些.
		ListeningExecutorService guavaExecutor2 = MoreExecutors
				.listeningDecorator(Executors.newSingleThreadExecutor());
		final ListenableFuture<String> listenableFuture2 = guavaExecutor2
				.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						TimeUnit.SECONDS.sleep(1);
						String logText = "[" + Thread.currentThread().getName() +"]: guava的Future返回结果";
						System.out.println(logText);
						return logText;
					}
				});

		// 注意这里没用指定执行回调的线程池,从输出可以看出，默认是和执行异步操作的线程是同一个.
		Futures.addCallback(listenableFuture2, new FutureCallback<String>() {
					@Override
					public void onSuccess(String result) {
						String logTxt = "[" + Thread.currentThread().getName() +"]=======>对回调结果【"+result+"】进行FutureCallback,经测试，发现是和回调结果处理线程为同一个线程";
						System.out.println(logTxt);
					}
					@Override
					public void onFailure(Throwable t) {
					}
				}
		);
		// 主线程可以继续执行,异步完成后会执行注册的监听器任务.
		System.out.println( "[" + Thread.currentThread().getName() +"]: guavaFuture2执行结束");
	}
}
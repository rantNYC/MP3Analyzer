package com.projects.mp3.controller.engine.utilities;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EngineThread {

	private static Logger log = LoggerFactory.getLogger(EngineThread.class);
	
	private EngineThread() { }
	
	public static ExecutorService createExecutor(int maxPool, String name) {
		return Executors.newFixedThreadPool(maxPool, new EngineThreadFactory(name));
	}
	
	public static EngineThreadFactory createThreadFactory(String name) {
		return new EngineThreadFactory(name);
	}
	
	
	public static void runThreads(final Collection<Runnable> tasks, boolean isSerially, int maxThreads) {
		EngineThreadFactory factory = createThreadFactory("CustomThreads");
		if(isSerially) {
			for(Runnable task : tasks) {
				Thread t = factory.newThread(task);
				t.run();
			}
		}else {
			ExecutorService service = Executors.newFixedThreadPool(maxThreads, factory);
			CountDownLatch latch = new CountDownLatch(tasks.size());
			for(Runnable task : tasks) {
				service.submit(new SynchronizedRunner(task, latch));
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				//Ignore
			}
			terminateExecutor(service, 5, TimeUnit.SECONDS);
		}
	}
	
	public static void terminateExecutor(ExecutorService service, long timeout, TimeUnit unit) {
		service.shutdown();
		try {
		    if (!service.awaitTermination(timeout, unit)) {
		    	service.shutdownNow();
		    } 
		} catch (InterruptedException e) {
			log.warn("Executor was interrupted during termination");
			service.shutdownNow();
		}
	}
	
	private static class SynchronizedRunner implements Runnable{
		private Runnable task;
		private CountDownLatch latch;
		
		public SynchronizedRunner(Runnable task, CountDownLatch latch) {
			super();
			this.task = task;
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				task.run();
			} finally {
				latch.countDown();
			}
		}
	}

}

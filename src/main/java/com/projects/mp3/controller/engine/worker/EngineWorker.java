package com.projects.mp3.controller.engine.worker;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.model.AudioInfo;
import com.projects.mp3.model.ContainerType;

public abstract class EngineWorker implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(EngineWorker.class);
	
	protected IThreadListener listener;
	protected final String name;
	protected volatile boolean isInterrupted = false;
	protected CountDownLatch latch;
	
	//TODO: Move to subclass for containers only?
	public final ContainerType type;
	
	public EngineWorker(String name, ContainerType type) {
		this.name = name;
		this.type = type;
	}
	
	public IThreadListener getListener() {
		return listener;
	}

	public void setListener(IThreadListener listener) {
		this.listener = listener;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	
	private final void notifyFinishThread() {
		listener.onThreadFinished(this);
	}
	
	//TODO: Maybe use addDataToContainer instead of this
	protected final boolean notifyNewDataThread(AudioInfo info) {
		return listener.onNewData(info);
		
	}
	
	protected final boolean notifyDataUnique(AudioInfo info) {
		return listener.verifyDataUnique(info);
	}

	protected final void notifyNewDataError(AudioInfo info) {
		listener.onNewDataError(info);
	}
	
	protected final boolean notifyaddDataToContainer(ContainerType type, AudioInfo info) {
		return listener.addDataToContainer(type, info);
	}
	
	protected final void notifyEnableLogic() {
		listener.enableLogic();
	}
	
	protected final void notifyDisableLogic() {
		listener.disableLogic();
	}

	public String getName() {
		return name;
	}

	public void run() {
		
		try {
			log.info("Executing thread: " + name);
			execute();
		}
		finally {
			notifyFinishThread();
			if(latch != null) latch.countDown();
		}
	}

	public void shutdown() {
		isInterrupted = true;
	}

	public abstract void execute();
	
	public void interrupt() {
		isInterrupted = true;
	}
	
	public boolean isInterrupted() {
		return isInterrupted;
	}
}

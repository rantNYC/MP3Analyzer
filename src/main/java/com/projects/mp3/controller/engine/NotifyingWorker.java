package com.projects.mp3.controller.engine;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Info;

public abstract class NotifyingWorker implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(NotifyingWorker.class);
	
	private final Set<IThreadListener> listeners = new CopyOnWriteArraySet<IThreadListener>();
	private final String name;
	volatile boolean isInterrupted = false;
	
	//TODO: Move to subclass for containers only?
	public final ContainerType type;
	
	public final void addListener(final IThreadListener listener) {
		listeners.add(listener);
	}
	
	public final void removeListener(final IThreadListener listener) {
		listeners.remove(listener);
	}
	
	private final void notifyFinishThread() {
		for (IThreadListener listener : listeners) {
			listener.onThreadFinished(this);
		}
	}

	protected final boolean notifyNewDataThread(MP3Info info) {
		for (IThreadListener listener : listeners) {
			if(!listener.onNewData(info)) return false;
		}
		
		return true;
	}
	
	protected final boolean notifyDataUnique(MP3Info info) {
		for (IThreadListener listener : listeners) {
			if(!listener.verifyDataUnique(info)) return false;
		}
		
		return true;
	}

	protected final void notifyNewDataError(MP3Info info) {
		for (IThreadListener listener : listeners) {
			listener.onNewDataError(info);
		}
	}
	
	protected final void notifySingleProcessFinish() {
		for(IThreadListener listener : listeners) {
			listener.singleProcessFinish();
		}
	}
	
	public NotifyingWorker(String name, ContainerType type) {
		this.name = name;
		this.type = type;
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

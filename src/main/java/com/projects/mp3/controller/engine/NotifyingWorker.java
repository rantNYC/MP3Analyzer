package com.projects.mp3.controller.engine;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.model.AudioInfo;
import com.projects.mp3.model.ContainerType;

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
	
	//TODO: Maybe use addDataToContainer instead of this
	protected final boolean notifyNewDataThread(AudioInfo info) {
		for (IThreadListener listener : listeners) {
			if(!listener.onNewData(info)) return false;
		}
		
		return true;
	}
	
	protected final boolean notifyDataUnique(AudioInfo info) {
		for (IThreadListener listener : listeners) {
			if(!listener.verifyDataUnique(info)) return false;
		}
		
		return true;
	}

	protected final void notifyNewDataError(AudioInfo info) {
		for (IThreadListener listener : listeners) {
			listener.onNewDataError(info);
		}
	}
	
	protected final boolean NotifyaddDataToContainer(ContainerType type, AudioInfo info) {
		for(IThreadListener listener : listeners) {
			if(!listener.addDataToContainer(type, info)) return false;
		}
		
		return true;
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

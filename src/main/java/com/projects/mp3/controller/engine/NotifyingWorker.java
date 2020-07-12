package com.projects.mp3.controller.engine;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import com.projects.mp3.model.MP3Info;

public abstract class NotifyingWorker implements Runnable {

	private final Set<ThreadListener> listeners = new CopyOnWriteArraySet<ThreadListener>();
	private final String name;
	volatile boolean isInterrupted = false;
	
	public final void addListener(final ThreadListener listener) {
		listeners.add(listener);
	}
	public final void removeListener(final ThreadListener listener) {
		listeners.remove(listener);
	}
	private final void notifyFinishThread() {
		for (ThreadListener listener : listeners) {
			listener.onThreadFinished(this);
		}
	}
	
	protected final void notifyNewDataThread(MP3Info info) {
		for (ThreadListener listener : listeners) {
			listener.onNewData(info);
		}
	}

	public NotifyingWorker(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void run() {
		try {
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
}

package com.projects.mp3.controller.engine;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.projects.mp3.model.MP3Info;

public abstract class NotifyingWorker implements Runnable {

	private final Set<IThreadListener> listeners = new CopyOnWriteArraySet<IThreadListener>();
	private final String name;
	volatile boolean isInterrupted = false;
	
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
	
	protected final void notifyNewDataThread(MP3Info info) {
		for (IThreadListener listener : listeners) {
			listener.onNewData(info);
		}
	}
	
	protected final boolean verifyDataUnique(MP3Info info) {
		for (IThreadListener listener : listeners) {
			if(!listener.verifyDataUnique(info)) return false;
		}
		
		return true;
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

package com.projects.mp3.controller.engine;

import com.projects.mp3.model.MP3Info;

public abstract class ListenerWoker implements ThreadListener, Runnable {

	NotifyingWorker worker;
	
	public ListenerWoker(NotifyingWorker worker) {
		this.worker = worker;
	}

	public void run() {
		worker.run();
	}

	public String getWorkerName() {
		return worker.getName();
	}
	
	public void stopWoker() {
		worker.shutdown();
	}
	
	public abstract String onNewData(MP3Info info);

	public abstract void onThreadFinished(NotifyingWorker notifyingThread);

}

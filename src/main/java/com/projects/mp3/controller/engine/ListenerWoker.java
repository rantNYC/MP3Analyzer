package com.projects.mp3.controller.engine;

import com.projects.mp3.model.SynchronizedDataContainer;

public abstract class ListenerWoker implements IThreadListener, Runnable {

	NotifyingWorker worker;
	SynchronizedDataContainer container;
	
	public ListenerWoker(NotifyingWorker worker, SynchronizedDataContainer container) {
		this.worker = worker;
		this.container = container;
//		this.container.addEmptyConcurrentSet(worker.getName());
	}

	public void run() {
		worker.run();
	}

	public String getWorkerName() {
		return worker.getName();
	}
}

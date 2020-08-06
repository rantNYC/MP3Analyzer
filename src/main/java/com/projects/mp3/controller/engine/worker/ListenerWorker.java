package com.projects.mp3.controller.engine.worker;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.SynchronizedDataContainer;

@SuppressWarnings("unused")
public abstract class ListenerWorker implements IThreadListener, Runnable {

	protected EngineWorker worker;
	
	public ListenerWorker(EngineWorker worker) {
		this.worker = worker;
//		this.container.addEmptyConcurrentSet(worker.getName());
	}

	public void run() {
		worker.run();
	}

	public String getWorkerName() {
		return worker.getName();
	}
	
	public ContainerType getWorkerContainer() {
		return worker.type;
	}
}

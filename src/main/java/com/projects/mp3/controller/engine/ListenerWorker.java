package com.projects.mp3.controller.engine;

import java.util.List;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.AudioInfo;
import com.projects.mp3.model.SynchronizedDataContainer;

public abstract class ListenerWorker implements IThreadListener, Runnable {

	NotifyingWorker worker;
	SynchronizedDataContainer container;
	
	public ListenerWorker(NotifyingWorker worker, SynchronizedDataContainer container) {
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
	
	public List<AudioInfo> getDifferencerRight(ContainerType leftType, ContainerType rightType) {
		return container.getDifferencerRight(leftType, rightType);
	}
	
	public ContainerType getWorkerContainer() {
		return worker.type;
	}

	public abstract void executeLogic();
}

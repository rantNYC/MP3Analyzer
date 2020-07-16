package com.projects.mp3.controller.engine;

import java.util.Set;

import com.projects.mp3.model.MP3Info;

public abstract class ListenerWoker implements IThreadListener, Runnable {

	NotifyingWorker worker;
	protected Set<MP3Info> appData;
	
	public ListenerWoker(NotifyingWorker worker, Set<MP3Info> appData) {
		this.worker = worker;
		this.appData = appData;
	}

	public void run() {
		worker.run();
	}

	public String getWorkerName() {
		return worker.getName();
	}
}

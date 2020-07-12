package com.projects.mp3.controller.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class GUIThreadFactory implements ThreadFactory {

	private int counter;
	private String name;
	private String workerName = "Unknown";
	private List<String> stats;

	public GUIThreadFactory(String name) {
		this.counter = 1;
		this.name = name;
		this.stats = new ArrayList<String>();
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, String.format("%s_%s_%d", name, workerName, counter));
		
        t.setDaemon(true);
		++counter;
		stats.add(String.format("Created thread %d with name %s on %s \n", t.getId(), t.getName(), new Date()));
		return t;
	}

}

package com.projects.mp3.controller.engine.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import com.projects.mp3.controller.engine.worker.EngineWorker;

public class EngineThreadFactory implements ThreadFactory {

	private int counter;
	private String name;
	private List<String> stats;

	public EngineThreadFactory(String name) {
		this.counter = 1;
		this.name = name;
		this.stats = new ArrayList<String>();
	}

	
	public Thread newThread(Runnable r) {
		EngineWorker worker = null;
		if(r instanceof EngineWorker) {
			worker = (EngineWorker) r;
		}
		String threadName = String.format("%s_%d", name, counter);
		if(worker != null) {
			threadName = String.format("%s_%s_%d", name, worker.getName(), counter);
		}
		
		Thread t = new Thread(r, threadName);
		
        t.setDaemon(true);
		++counter;
		stats.add(String.format("Created thread %d with name %s on %s", t.getId(), t.getName(), new Date()));
		return t;
	}

}

package com.projects.mp3.controller.engine;

import java.io.File;
import java.util.*	;
import java.util.concurrent.BlockingQueue;

import com.projects.mp3.model.MP3Info;

public class EngineWorker implements Runnable{

	private final String workerName;
	private final List<File> mp3Files;
	private BlockingQueue<MP3Info> queue;
//	private final Engine engine;
	
	public EngineWorker(String name, final List<File> mp3Files, BlockingQueue<MP3Info> queue) {
		if(!EngineUtilities.isNullorEmpty(name))
			workerName = name;
		else
			workerName = "Unknown";
//		if(engine == null) throw new IllegalArgumentException("Path cannot be null ");
//		this.engine = engine;
		this.mp3Files = mp3Files;
		this.queue = queue;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void run() {
		EngineUtilities.printConsole("Executing thread:" + workerName);
		for(File file : mp3Files) {
			//TODO: Handle millions of rows
			MP3Decoder decoder = new MP3Decoder(file.getAbsolutePath());
			MP3Info info = null;
			try {
				info = decoder.decodeInformation();
			} catch (Exception e) {
				// TODO change this
				e.printStackTrace();
			}
			//TODO:log it contains object
			if(info != null && !queue.contains(info)) {
				queue.add(info);
			}
		}
	}
	

}

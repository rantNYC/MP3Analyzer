package com.projects.mp3.controller.engine;

import java.io.File;
import java.util.*	;
import java.util.concurrent.BlockingQueue;

import com.projects.mp3.model.MP3Info;

public class EngineWorker extends NotifyingWorker{

	private final String workerName;
	private final List<File> mp3Files;
//	private final Engine engine;
	
	public EngineWorker(String name, final List<File> mp3Files) {
		super(name);
		if(!EngineUtilities.isNullorEmpty(name))
			workerName = name;
		else
			workerName = "Unknown";
		this.mp3Files = mp3Files;
	}

	@Override
	public void execute() {
		EngineUtilities.printConsole("Executing thread:" + workerName);
		for(File file : mp3Files) {
			if(isInterrupted || Thread.currentThread().isInterrupted()) {
				return;
			}
			//TODO: Handle millions of rows
			MP3Decoder decoder = new MP3Decoder(file.getAbsolutePath());
			MP3Info info = null;
			try {
				info = decoder.decodeInformation();
				notifyNewDataThread(info);
			} catch (Exception e) {
				// TODO change this
				e.printStackTrace();
			}
			//TODO:log it contains object
//			if(info != null && !queue.contains(info)) {
//				queue.add(info);
//			}
		}
	}
	

}

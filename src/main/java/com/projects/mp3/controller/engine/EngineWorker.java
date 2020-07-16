package com.projects.mp3.controller.engine;

import java.io.File;
import java.util.*	;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.model.MP3Info;

public class EngineWorker extends NotifyingWorker{

	private static final Logger log = LoggerFactory.getLogger(EngineWorker.class);
	
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
		log.info("Executing thread: " + workerName);
		for(File file : mp3Files) {
			if(Thread.currentThread().isInterrupted()) {
				log.info(String.format("%s was interrupted", Thread.currentThread().getName()));
				return;
			}
			//TODO: Handle millions of rows
			MP3Decoder decoder = new MP3Decoder(file.getAbsolutePath());
			MP3Info info = null;
			try {
				info = decoder.decodeInformation();
				notifyNewDataThread(info);
			} catch (Exception e) {
				log.error("Error decoding: " + file, e);
			}
		}
	}
	

}

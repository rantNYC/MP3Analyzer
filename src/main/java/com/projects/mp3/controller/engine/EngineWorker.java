package com.projects.mp3.controller.engine;

import java.io.File;
import java.util.*	;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Info;

public class EngineWorker extends NotifyingWorker{

	private static final Logger log = LoggerFactory.getLogger(EngineWorker.class);
	
	private final List<File> mp3Files;
//	private final Engine engine;
	
	public EngineWorker(String name, final List<File> mp3Files) {
		super(name, ContainerType.FolderContainer);
		this.mp3Files = mp3Files;
	}

	@Override
	public void execute() {
		for(File file : mp3Files) {
			if(Thread.currentThread().isInterrupted()) {
				log.info(String.format("%s was interrupted", Thread.currentThread().getName()));
				this.interrupt();
				return;
			}
			//TODO: Handle millions of rows
			MP3Decoder decoder = new MP3Decoder(file.getAbsolutePath());
			MP3Info info = null;
			try {
				info = decoder.decodeInformation();
				if(notifyNewDataThread(info)) {
					log.info(String.format("Data %s was sucessfully added", info.toString()));
				}else {					
					log.warn(String.format("Data %s already exists", info.toString()));
				}
			} catch (Exception e) {
				log.error("Error decoding: " + file, e);
			}
		}
	}
	

}

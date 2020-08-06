package com.projects.mp3.controller.engine.worker;

import java.io.File;
import java.util.*	;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.projects.mp3.model.ContainerType;
import com.projects.mp3.controller.engine.decoder.Decoder;
import com.projects.mp3.controller.engine.decoder.IDecoder;
import com.projects.mp3.model.AudioInfo;

public class DecoderWorker extends EngineWorker{

	private static final Logger log = LoggerFactory.getLogger(DecoderWorker.class);
	
	private final List<File> mp3Files;
//	private final Engine engine;
	
	public DecoderWorker(String name, final List<File> mp3Files) {
		super(name, ContainerType.FolderContainer);
		this.mp3Files = mp3Files;
	}

	public List<File> getMp3Files() {
		return ImmutableList.copyOf(mp3Files);
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
			IDecoder decoder = new Decoder();
			AudioInfo info = null;
			try {
				if(decoder.isAudioFile(file)) {
					info = decoder.decodeInformation(file);
					if(notifyNewDataThread(info)) {
						log.info(String.format("Data %s was sucessfully added", info.toString()));
					}else {					
						log.warn(String.format("Data %s already exists", info.toString()));
					}
				} else {
					log.warn(String.format("File %s is not an audio type", file));
				}
			} catch (Exception e) {
				log.error("Error decoding: " + file, e);
			}
		}
	}
}

package com.projects.mp3.controller.storage;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.engine.NotifyingWorker;
import com.projects.mp3.controller.engine.decoder.Decoder;
import com.projects.mp3.controller.engine.decoder.IDecoder;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.AudioInfo;

public class DatabaseWorker extends NotifyingWorker {

	private static final Logger log = LoggerFactory.getLogger(DatabaseWorker.class);

	public final ContainerType type = ContainerType.DBContainer;

	private MySQLDriver driver;
	private DBAction action;
	private List<File> mp3Files;
	private boolean parseFileNameifInfoNull = false;
	
	public DatabaseWorker(String name, MySQLDriver driver, DBAction action, List<File> mp3Files) {
		super(name, ContainerType.DBContainer);
		if(driver == null) throw new IllegalArgumentException("DB driver cannot be null");
		this.driver = driver;
		this.action = action;
		this.mp3Files = mp3Files;
	}

	public void setEnableFileNameParsing(boolean enableFileNameParsing) {
		this.parseFileNameifInfoNull = enableFileNameParsing;
	}
	
	@Override
	public void execute() {
		switch(action) {
		case Fetch:
		case Refresh:
			try {
				executeFetching();
			} catch (SQLException e) {
				log.error("Error updating the Database", e);
			}
			return;
		case Upload:
			executeUpload();
			return;
		default:
			log.debug("Nothing to execute in default");
			return;
		}
	}

	private void executeUpload() {
		if(mp3Files == null) {
			log.warn("Cannot upload without searching first");
			return;
		}

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
					if(NotifyaddDataToContainer(ContainerType.FolderContainer, info)) {
						log.info(String.format("Data %s was sucessfully added to %s", 
												info.toString(),ContainerType.FolderContainer));
					}else {					
						log.warn(String.format("Data %s already exists in %s", 
												info.toString(),ContainerType.FolderContainer));
					}
					if(notifyDataUnique(info)) {
						if(info.getSongName() == null && info.getArtistName() == null &&
								parseFileNameifInfoNull) {
							info = decoder.parseFileName(info);
						}
						driver.insertMP3ToDB(info);
						notifyNewDataThread(info);
					}
					else {
						log.warn(String.format("Data %s already in %s", info, type));
					}
				} else {
					log.warn(String.format("File %s is not an audio type", file));
				}
			} catch (Exception e) {
				notifyNewDataError(info);
				log.error("Error processing: " + file, e);
			} finally {
				notifySingleProcessFinish();
			}
		}
	}

	private void executeFetching() throws SQLException {
		List<AudioInfo> dbData = driver.getAllDataInDB();
		for(AudioInfo info : dbData) {
			if(Thread.currentThread().isInterrupted()) {
				log.info(String.format("%s was interrupted", Thread.currentThread().getName()));
				return;
			}
			if(notifyNewDataThread(info)) {
				log.info(String.format("Data %s was sucessfully fetched", info.toString()));
			}else {					
				log.warn(String.format("Data %s already exists", info.toString()));
			}
		}
	}

}

package com.projects.mp3.controller.storage;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.engine.MP3Decoder;
import com.projects.mp3.controller.engine.NotifyingWorker;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Info;

public class DatabaseWorker extends NotifyingWorker {

	private static final Logger log = LoggerFactory.getLogger(DatabaseWorker.class);

	public final ContainerType type = ContainerType.DBContainer;

	private MySQLDriver driver;
	private DBAction action;
	private List<File> mp3Files; 

	public DatabaseWorker(String name, MySQLDriver driver, DBAction action, List<File> mp3Files) {
		super(name, ContainerType.DBContainer);
		if(driver == null) throw new IllegalArgumentException("DB driver cannot be null");
		this.driver = driver;
		this.action = action;
		this.mp3Files = mp3Files;
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
			MP3Decoder decoder = new MP3Decoder(file.getAbsolutePath());
			MP3Info info = null;
			try {
				info = decoder.decodeInformation();
				driver.insertMP3ToDB(info);
				if(notifyNewDataThread(info)) {
					log.info(String.format("Data %s was sucessfully added", info.toString()));
				}else {					
					log.warn(String.format("Data %s already exists", info.toString()));
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
		List<MP3Info> dbData = driver.getAllDataInDB();
		for(MP3Info info : dbData) {
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

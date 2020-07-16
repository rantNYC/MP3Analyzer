package com.projects.mp3.controller.storage;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.engine.NotifyingWorker;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.MP3Info;

public class DatabaseWorker extends NotifyingWorker {

	private static final Logger log = LoggerFactory.getLogger(DatabaseWorker.class);
	
	MySQLDriver driver;
	List<MP3Info> data;
	DBAction action;
	
	public DatabaseWorker(String name, MySQLDriver driver,final List<MP3Info> data, DBAction action) {
		super(name);
		if(driver == null) throw new IllegalArgumentException("DB driver cannot be null");
		this.driver = driver;
		this.data = data;
		this.action = action;
	}

	@Override
	public void execute() {
		switch(action) {
			case Fetch:
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
		if(data == null || data.size() == 0) {
			log.info("No MP3 Information to upload");
			return;
		}
		
		for(MP3Info info : data) {
			//TODO: Fix this with data container
//			if(!verifyDataUnique(info)) {
//				log.warn(String.format("%s exists in DB already", info.toString()));
//				continue;
//			}
			if(Thread.currentThread().isInterrupted()) {
				log.info(String.format("%s was interrupted", Thread.currentThread().getName()));
				return;
			}
			try {
				driver.insertMP3ToDB(info);
				notifyNewDataThread(info);
			} catch (Exception e) {
				log.error("Cannot insert data to DB " + info.toString(), e);
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
			notifyNewDataThread(info);
		}
	}

}

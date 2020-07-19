package com.projects.mp3.controller.storage;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.engine.EngineUtilities;
import com.projects.mp3.controller.engine.NotifyingWorker;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Info;

public class DatabaseWorker extends NotifyingWorker {

	private static final Logger log = LoggerFactory.getLogger(DatabaseWorker.class);
	
	public final ContainerType type = ContainerType.DBContainer;
	MySQLDriver driver;
	List<MP3Info> data;
	DBAction action;
	
	public DatabaseWorker(String name, MySQLDriver driver,final List<MP3Info> data, DBAction action) {
		super(name, ContainerType.DBContainer);
		if(driver == null) throw new IllegalArgumentException("DB driver cannot be null");
		this.driver = driver;
		this.data = data;
		this.action = action;
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
		if(data == null || data.size() == 0) {
			String content = action == DBAction.Fetch ? "No MP3Data to upload" : "DB already up-to-date"; 
			log.info(content);
			return;
		}
		
		for(MP3Info info : data) {
			if(!notifyDataUnique(info)) {
				log.warn(String.format("%s exists in DB already", info.toString()));
				continue;
			}
			if(Thread.currentThread().isInterrupted()) {
				log.info(String.format("%s was interrupted", Thread.currentThread().getName()));
				return;
			}
			try {
				if(EngineUtilities.isNullorEmpty(info.getSongName()) ||
					EngineUtilities.isNullorEmpty(info.getArtistName())) {
					log.warn(String.format("Cannot insert %s because it contains null for primary keys", info.toString()));
					continue;
				}
				driver.insertMP3ToDB(info);
				if(notifyNewDataThread(info)) {
					log.info(String.format("Data %s was sucessfully added", info.toString()));
				}else {					
					log.warn(String.format("Data %s already exists", info.toString()));
				}
			} catch (Exception e) {
//				notifyNewDataError(info);
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
			if(notifyNewDataThread(info)) {
				log.info(String.format("Data %s was sucessfully added", info.toString()));
			}else {					
				log.warn(String.format("Data %s already exists", info.toString()));
			}
		}
	}

}

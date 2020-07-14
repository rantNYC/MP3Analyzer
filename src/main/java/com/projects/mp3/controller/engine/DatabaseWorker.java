package com.projects.mp3.controller.engine;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.MP3Info;

public class DatabaseWorker extends NotifyingWorker {

	private static final Logger log = LoggerFactory.getLogger(DatabaseWorker.class);
	
	MySQLDriver driver;
	List<MP3Info> data;
	
	public DatabaseWorker(String name, MySQLDriver driver, List<MP3Info> data) {
		super(name);
		if(driver == null) throw new IllegalArgumentException("DB driver cannot be null");
		this.driver = driver;
		if(data == null) throw new IllegalArgumentException("Data cannot be null");
		this.data = data;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		if(data.size() == 0) {
			log.info("No MP3 Information to upload");
			return;
		}
		
		for(MP3Info info : data) {
			if(Thread.currentThread().isInterrupted()) return;
			try {
				driver.insertMP3ToDB(info);
				notifyNewDataThread(info);
			} catch (Exception e) {
				log.error("Cannot insert data to DB " + info.toString(), e);
			}
		}
	}

}

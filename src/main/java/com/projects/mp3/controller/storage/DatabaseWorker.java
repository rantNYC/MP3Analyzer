package com.projects.mp3.controller.storage;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.engine.EngineUtilities;
import com.projects.mp3.controller.engine.ListenerWorker;
import com.projects.mp3.controller.engine.NotifyingWorker;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Info;

public class DatabaseWorker extends NotifyingWorker {

	private static final Logger log = LoggerFactory.getLogger(DatabaseWorker.class);

	public final ContainerType type = ContainerType.DBContainer;

	private MySQLDriver driver;
	private DBAction action;
	private ListenerWorker subRoutine; 

	public DatabaseWorker(String name, MySQLDriver driver, DBAction action, ListenerWorker subRoutine) {
		super(name, ContainerType.DBContainer);
		if(driver == null) throw new IllegalArgumentException("DB driver cannot be null");
		this.driver = driver;
		this.action = action;
		this.subRoutine = subRoutine;
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
		if(subRoutine == null) {
			log.warn("Cannot upload without searching first");
			return;
		}

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(subRoutine);
		try {
			service.awaitTermination(EngineUtilities.TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			log.info(String.format("%s was interrupted", Thread.currentThread().getName()));
			service.shutdownNow();
			this.interrupt();
			return;
		}
		
		List<MP3Info> data = subRoutine.getDifferencerRight(type, subRoutine.getWorkerContainer());
		for(MP3Info info : data) {
			if(!notifyDataUnique(info)) {
				log.warn(String.format("%s exists in DB already", info.toString()));
				continue;
			}
			if(Thread.currentThread().isInterrupted()) {
				log.info(String.format("%s was interrupted", Thread.currentThread().getName()));
				this.interrupt();
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

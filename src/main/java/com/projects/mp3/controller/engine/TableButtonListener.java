package com.projects.mp3.controller.engine;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.model.MP3Info;

import javafx.scene.control.*;

public class TableButtonListener extends ListenerWoker {

	private static final Logger log = LoggerFactory.getLogger(TableButtonListener.class);
	
	TableView<MP3Info> viewer;
	//TODO: Maybe move to Controller? Do we need onNewData then?
	public static Set<MP3Info> unique = Collections.synchronizedSet(new HashSet<MP3Info>());
	Button searchButton;
	
	public TableButtonListener(TableView<MP3Info> viewer, Button searchButton, NotifyingWorker worker) {
		super(worker);
		if(viewer == null) throw new IllegalArgumentException("TableView cannot be null");
		if(searchButton == null) throw new IllegalArgumentException("Button cannot be null");
		this.viewer = viewer;
		this.searchButton = searchButton;
	}
	
	@Override
	public void run() {
		searchButton.setDisable(true);
		super.run();
	}
	
	public void onNewData(MP3Info info) {
		if(unique.add(info)) {
			viewer.getItems().add(info);
			log.info(String.format("Succesfully added %s", info.toString()));
		} else {
			log.warn(String.format("%s already added", info.toString()));
		}
	}

	public void onThreadFinished(NotifyingWorker notifyingThread) {
		searchButton.setDisable(false);
	}

}

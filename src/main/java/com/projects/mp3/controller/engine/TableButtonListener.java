package com.projects.mp3.controller.engine;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.model.MP3Info;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.*;

public class TableButtonListener extends ListenerWoker {

	private static final Logger log = LoggerFactory.getLogger(TableButtonListener.class);
	
	TableView<MP3Info> viewer;
	ObservableSet<MP3Info> viewerData = FXCollections.synchronizedObservableSet(FXCollections.observableSet());
	Button button;
	
	public TableButtonListener(TableView<MP3Info> viewer, Button button, NotifyingWorker worker, Set<MP3Info> appSet) {
		super(worker, appSet);
		if(viewer == null) throw new IllegalArgumentException("TableView cannot be null");
		if(button == null) throw new IllegalArgumentException("Button cannot be null");
		this.viewer = viewer;
		if(viewer.getItems() != null) {
			for(MP3Info info : viewer.getItems()) {
				viewerData.add(info);
			}
		}
		this.button = button;
	}
	
	@Override
	public void run() {
		button.setDisable(true);
		super.run();
	}
	
	@Override
	public void onNewData(MP3Info info) {
		appData.add(info);
		if(viewerData.add(info)) {
			viewer.getItems().add(info);
			log.info(String.format("Succesfully added %s", info.toString()));
		} else {
			log.warn(String.format("%s already added", info.toString()));
		}
	}

	@Override
	public void onThreadFinished(NotifyingWorker notifyingThread) {
		button.setDisable(false);
	}

	@Override
	public boolean verifyDataUnique(MP3Info info) {
		return appData.add(info);
	}

}

package com.projects.mp3.controller.engine;

import java.util.*;
import com.projects.mp3.model.MP3Info;

import javafx.scene.control.*;

public class SearchMP3Listener extends ListenerWoker {

	TableView<MP3Info> viewer;
	Set<MP3Info> unique = Collections.synchronizedSet(new HashSet<MP3Info>());
	Button searchButton;
	
	public SearchMP3Listener(TableView<MP3Info> viewer, Button searchButton, NotifyingWorker worker) {
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
	
	public String onNewData(MP3Info info) {
		if(unique.add(info)) {
			viewer.getItems().add(info);
			return String.format("Succesfully added %b", info);
		} else {
			return String.format("%b already added", info);
		}
	}

	public void onThreadFinished(NotifyingWorker notifyingThread) {
		searchButton.setDisable(false);
	}

}

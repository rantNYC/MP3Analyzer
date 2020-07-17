package com.projects.mp3.controller.engine;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Info;
import com.projects.mp3.model.SynchronizedDataContainer;

import javafx.scene.control.*;

public class TableButtonListener extends ListenerWoker {
	
	TableView<MP3Info> viewer;
	Button button;
	
	public TableButtonListener(TableView<MP3Info> viewer, Button button, NotifyingWorker worker, 
								SynchronizedDataContainer container) {
		super(worker, container);
		if(viewer == null) throw new IllegalArgumentException("TableView cannot be null");
		if(button == null) throw new IllegalArgumentException("Button cannot be null");
		this.viewer = viewer;
		this.button = button;
	}
	
	@Override
	public void run() {
		button.setDisable(true);
		super.run();
	}
	
	@Override
	public boolean onNewData(MP3Info info) {
		if(container.addDataToContainer(worker.getName(), info)) {
			viewer.getItems().add(info);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onThreadFinished(NotifyingWorker notifyingThread) {
		button.setDisable(false);
	}

	@Override
	public boolean verifyDataUnique(MP3Info info) {
		if(container.containsDataInContainer(worker.getName(), info) ||
				container.containsDataInContainer(ContainerType.OrphanContainer.toString(), info)) {
			return false;
		}
		
		return true;
	}

	@Override
	public void onNewDataError(MP3Info info) {
		container.addDataToContainer(ContainerType.OrphanContainer.toString(), info);
	}

}

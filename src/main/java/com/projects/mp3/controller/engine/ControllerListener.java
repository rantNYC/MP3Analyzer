package com.projects.mp3.controller.engine;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Info;
import com.projects.mp3.model.SynchronizedDataContainer;

import javafx.application.Platform;
import javafx.scene.control.*;

public class ControllerListener extends ListenerWorker {

	private boolean shouldEnableButton = true;
	
	private TableView<MP3Info> viewer;
	private Button btn;
	private ProgressBar bar;
	private Label barLabel;
	private Label sucessLabel;
	private Label failLabel;
	private int successCounter;
	private int failCounter;;
	private double counter;
	private double total;
	
	public ControllerListener(TableView<MP3Info> viewer, Button btn, 
								NotifyingWorker worker, SynchronizedDataContainer container) {
		super(worker, container);
		if(btn == null) throw new IllegalArgumentException("Button cannot be null");
		if(viewer == null) throw new IllegalArgumentException("Table View cannot be null");
		this.viewer = viewer;
		this.btn = btn;
		counter = 0;
		total = 0;
	}

	public void setProgressBar(ProgressBar bar) {
		if(bar == null) throw new IllegalArgumentException("Progress bar cannot be null");
		this.bar = bar;
	}
	
	public void setBarLabel(Label barLabel) {
		if(barLabel == null) throw new IllegalArgumentException("Bar label cannot be null");
		this.barLabel = barLabel;
	}
	
	public void setSuccessLabel(Label sucessLabel) {
		if(sucessLabel == null) throw new IllegalArgumentException("Success label cannot be null");
		this.sucessLabel = sucessLabel;
		successCounter = EngineUtilities.tryParseInt(sucessLabel.getText(), 0);
	}
	
	public void setFailLabel(Label failLabel) {
		if(failLabel == null) throw new IllegalArgumentException("Failure label cannot be null");
		this.failLabel = failLabel;
		failCounter = EngineUtilities.tryParseInt(failLabel.getText(), 0);
	}
	
	public void refreshViewer() {
		viewer.getItems().clear();
	}
	
	public void setTotal(int total) {
		if(total > 0) this.total = total;
	}
	
	public void disableButton() {
		btn.setDisable(true);
	}
	
	public void enableButton() {
		btn.setDisable(false);
	}
	
	@Override
	public void run() {
		disableButton();
		super.run();
	}
	
	@Override
	public boolean onNewData(MP3Info info) {
		if(sucessLabel != null) {
			Platform.runLater(() -> {
				sucessLabel.setText(++successCounter+"");
			});
		}
		
		if(container.addDataToContainer(worker.type, info)) {
			viewer.getItems().add(info);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onThreadFinished(NotifyingWorker notifyingThread) {
		if(shouldEnableButton) enableButton();
	}
	
	@Override
	public boolean verifyDataUnique(MP3Info info) {
		if(container.containsDataInContainer(worker.type, info) ||
				container.containsDataInContainer(ContainerType.OrphanContainer, info)) {
			return false;
		}

		return true;
	}

	@Override
	public void onNewDataError(MP3Info info) {
		container.addDataToContainer(ContainerType.OrphanContainer, info);
		if(failLabel != null) {
			Platform.runLater(() -> {
				failLabel.setText(++failCounter+"");
			});
		}
	}
	
	@Override
	public void executeLogic() {
		//Don't disable button if this is called
		shouldEnableButton = false;
	}

	@Override
	public void singleProcessFinish() {
		if(bar != null && barLabel != null && total > 0) {
			double timer = ++counter/total ;
			Platform.runLater(() -> {
				barLabel.setText(String.format("%.2f%%", timer*100));
				bar.setProgress(timer);
			});
		}
	}
}

package com.projects.mp3.controller.engine.worker;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.controller.MainGUIController;
import com.projects.mp3.model.AudioInfo;
import com.projects.mp3.model.SynchronizedDataContainer;

public class ControllerListener extends ContainerListener {

	private boolean shouldEnableButton = true;
	
	private MainGUIController controller;
	private int successCounter;
	private int failCounter;;
	private double counter;
	private double total;
	
	public ControllerListener(MainGUIController controller,
								EngineWorker worker, SynchronizedDataContainer container) {
		super(worker, container);
		if(controller == null) throw new IllegalArgumentException("Controller cannot be null");
		this.controller = controller;
		counter = 0;
		total = 0;
	}
	
	public void refreshDBViewer() {
		controller.refreshDBGUI();
	}
	
	public void setTotal(int total) {
		if(total > 0) this.total = total;
	}
	
	
	@Override
	public void run() {
		controller.disableStartButton();
		super.run();
	}
	
	@Override
	public boolean onNewData(AudioInfo info) {
		if(super.onNewData(info)) {
			controller.setDBTableInfo(info);
			controller.runThreadSafe(() -> controller.setSuccessLabel(++successCounter));
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onThreadFinished(EngineWorker notifyingThread) {
		if(shouldEnableButton) controller.enableStartButton();
		double timer = ++counter/total;
		controller.runThreadSafe(() -> controller.setProgressBar(timer));
		controller.runThreadSafe(() -> controller.setNumDBFiles(container.getSizeContainer(ContainerType.DBContainer)));
		controller.runThreadSafe(() -> controller.setNumRootFiles(container.getSizeContainer(ContainerType.FolderContainer)));
	}

	@Override
	public void onNewDataError(AudioInfo info) {
		super.onNewDataError(info);
		controller.runThreadSafe(() -> controller.setFailedLabel(++failCounter));
	}
	
	@Override
	public void disableLogic() {
		//Don't disable button if this is called
		shouldEnableButton = false;
	}
	
	@Override
	public void enableLogic() {
		//Don't disable button if this is called
		shouldEnableButton = true;
	}

	@Override
	public boolean addDataToContainer(ContainerType type, AudioInfo info) {
		if(super.addDataToContainer(type, info)) {
			controller.setFolderTableInfo(info);
			return true;
		}
		
		return false;
	}
}

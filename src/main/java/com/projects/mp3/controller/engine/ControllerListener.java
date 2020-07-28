package com.projects.mp3.controller.engine;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.controller.MainGUIController;
import com.projects.mp3.model.AudioInfo;
import com.projects.mp3.model.SynchronizedDataContainer;

public class ControllerListener extends ListenerWorker {

	private boolean shouldEnableButton = true;
	
	private MainGUIController controller;
	private int successCounter;
	private int failCounter;;
	private double counter;
	private double total;
	
	public ControllerListener(MainGUIController controller,
								NotifyingWorker worker, SynchronizedDataContainer container) {
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
		controller.runThreadSafe(() -> controller.setSuccessLabel(++successCounter));
		
		if(container.addDataToContainer(worker.type, info)) {
			controller.setDBTableInfo(info);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onThreadFinished(NotifyingWorker notifyingThread) {
		if(shouldEnableButton) controller.enableStartButton();
	}
	
	@Override
	public boolean verifyDataUnique(AudioInfo info) {
		if(container.containsDataInContainer(worker.type, info) ||
				container.containsDataInContainer(ContainerType.OrphanContainer, info)) {
			return false;
		}

		return true;
	}

	@Override
	public void onNewDataError(AudioInfo info) {
		container.addDataToContainer(ContainerType.OrphanContainer, info);
		controller.runThreadSafe(() -> controller.setFailedLabel(++failCounter));
	}
	
	@Override
	public void executeLogic() {
		//Don't disable button if this is called
		shouldEnableButton = false;
	}

	@Override
	public void singleProcessFinish() {
		double timer = ++counter/total;
		controller.runThreadSafe(() -> controller.setProgressBar(timer));
	}

	@Override
	public boolean addDataToContainer(ContainerType type, AudioInfo info) {
		if(container.addDataToContainer(type, info)) {
			controller.setFolderTableInfo(info);
			return true;
		}else {
			return false;
		}
	}
}

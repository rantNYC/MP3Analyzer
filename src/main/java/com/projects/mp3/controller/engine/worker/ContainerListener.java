package com.projects.mp3.controller.engine.worker;

import java.util.List;

import com.projects.mp3.model.AudioInfo;
import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.SynchronizedDataContainer;

public class ContainerListener extends ListenerWorker {

	protected SynchronizedDataContainer container;
	
	public ContainerListener(EngineWorker worker, SynchronizedDataContainer container) {
		super(worker);
		this.container = container;
	}

	public List<AudioInfo> getDifferencerRight(ContainerType leftType, ContainerType rightType) {
		return container.getDifferencerRight(leftType, rightType);
	}
	
	@Override
	public void onThreadFinished(EngineWorker notifyingThread) { }

	@Override
	public boolean addDataToContainer(ContainerType type, AudioInfo info) {
		if(container.addDataToContainer(type, info)) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public boolean onNewData(AudioInfo info) {
		if(container.addDataToContainer(worker.type, info)) {
			return true;
		} else {
			return false;
		}
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
	}

	@Override
	public SynchronizedDataContainer getContainer() {
		return container;
	}

	@Override
	public void enableLogic() {	}

	@Override
	public void disableLogic() { }

}

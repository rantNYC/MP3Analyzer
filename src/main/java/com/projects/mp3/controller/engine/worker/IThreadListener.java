package com.projects.mp3.controller.engine.worker;

import com.projects.mp3.model.AudioInfo;
import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.SynchronizedDataContainer;

public interface IThreadListener {

	void onThreadFinished(final EngineWorker notifyingThread);
	boolean addDataToContainer(final ContainerType container, final AudioInfo info);
	boolean onNewData(final AudioInfo info);
	boolean verifyDataUnique(final AudioInfo info);
	void onNewDataError(final AudioInfo info);
	SynchronizedDataContainer getContainer();
	void enableLogic();
	void disableLogic();
}

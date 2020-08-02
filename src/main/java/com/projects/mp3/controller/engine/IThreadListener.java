package com.projects.mp3.controller.engine;

import com.projects.mp3.model.AudioInfo;
import com.projects.mp3.model.ContainerType;

public interface IThreadListener {

	void onThreadFinished(final NotifyingWorker notifyingThread);
	boolean addDataToContainer(final ContainerType container, final AudioInfo info);
	boolean onNewData(final AudioInfo info);
	boolean verifyDataUnique(final AudioInfo info);
	void onNewDataError(final AudioInfo info);
}

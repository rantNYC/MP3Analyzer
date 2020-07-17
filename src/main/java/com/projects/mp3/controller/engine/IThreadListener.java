package com.projects.mp3.controller.engine;

import com.projects.mp3.model.MP3Info;

public interface IThreadListener {

	void onThreadFinished(final NotifyingWorker notifyingThread);
	boolean onNewData(final MP3Info info);
	boolean verifyDataUnique(final MP3Info info);
	void onNewDataError(final MP3Info info);
}

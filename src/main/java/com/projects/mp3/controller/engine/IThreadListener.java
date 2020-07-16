package com.projects.mp3.controller.engine;

import com.projects.mp3.model.MP3Info;

public interface IThreadListener {

	void onNewData(final MP3Info info);
	void onThreadFinished(final NotifyingWorker notifyingThread);
	boolean verifyDataUnique(final MP3Info info);
}

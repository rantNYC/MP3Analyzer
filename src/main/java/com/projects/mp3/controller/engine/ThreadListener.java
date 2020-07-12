package com.projects.mp3.controller.engine;

import com.projects.mp3.model.MP3Info;

public interface ThreadListener {

	void onNewData(final MP3Info info);
	void onThreadFinished(final NotifyingWorker notifyingThread);
}

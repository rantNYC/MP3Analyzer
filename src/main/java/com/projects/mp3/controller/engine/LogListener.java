package com.projects.mp3.controller.engine;

import com.projects.mp3.model.MP3Info;

public class LogListener extends ListenerWoker {

	public LogListener(NotifyingWorker worker) {
		super(worker);
	}

	@Override
	public String onNewData(MP3Info info) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onThreadFinished(NotifyingWorker notifyingThread) {
		// TODO Auto-generated method stub

	}

}

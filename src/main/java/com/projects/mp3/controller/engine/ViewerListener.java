package com.projects.mp3.controller.engine;

import java.util.*;
import java.util.concurrent.BlockingQueue;

import com.projects.mp3.model.MP3Info;

import javafx.scene.control.TableView;

public class ViewerListener implements Runnable {

	TableView<MP3Info> viewer;
	Set<MP3Info> unique = Collections.synchronizedSet(new HashSet<MP3Info>());
	BlockingQueue<MP3Info> queue;
	
	public ViewerListener(TableView<MP3Info> viewer, BlockingQueue<MP3Info> queue) {
		if(viewer == null) throw new IllegalArgumentException("TableView cannot be null");
		this.queue = queue;
		this.viewer = viewer;
	}

	public void run() {
		while(true) {
			try {
				MP3Info info = queue.take();
				if(unique.add(info)) {
					viewer.getItems().add(info);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

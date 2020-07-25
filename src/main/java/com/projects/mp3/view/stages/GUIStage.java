package com.projects.mp3.view.stages;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class GUIStage extends Stage {

	private static final Logger log = LoggerFactory.getLogger(GUIStage.class);
	
	protected final int screenWidth;
	protected final int screenHeight;

	private final URL resource;
	
	public GUIStage(int screenWidth, int screenHeight, URL resource) {
		super();
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.resource = resource;
	}

	public void startStage() {
		try {
			FXMLLoader loader = new FXMLLoader(resource);
			//new FXMLLoader(getClass().getClassLoader().getResource("FXML/MainDBGUI.fxml"));
			Parent root = (Parent)loader.load();
			this.setTitle("Database Login");
			Scene scene = new Scene(root, screenWidth, screenHeight);
			this.setMinHeight(300);
			this.setMinWidth(400);
			this.setScene(scene);
			logicToGUI(loader);
			this.show();
		} catch (Exception e) {
			log.error("Error in GUI", e);
			System.exit(-1);
		}
	}

	public abstract void logicToGUI(FXMLLoader loader) throws Exception ;

}
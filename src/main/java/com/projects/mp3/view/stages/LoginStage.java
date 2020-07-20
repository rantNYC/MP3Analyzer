package com.projects.mp3.view.stages;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.MainGUIController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginStage extends Stage {
	private static Logger log = LoggerFactory.getLogger(LoginStage.class);
	
	protected final int SCREEN_WIDTH   = 453;
	protected final int SCREEN_HEIGHT  = 214;
	
	public LoginStage(){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/MainDBGUI.fxml"));
			Parent root = (Parent)loader.load();
			this.setTitle("Database Login");
			Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
			this.setMinHeight(300);
			this.setMinWidth(400);
			this.setScene(scene);
			this.setResizable(false);
			this.show();
		} catch (IOException e) {
			log.error("Error in GUI", e);
			System.exit(-1);
		}
	}
}

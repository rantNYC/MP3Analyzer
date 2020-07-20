package com.projects.mp3.view.stages;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.MainGUIController;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainGUIStage extends Stage {
	private static Logger log = LoggerFactory.getLogger(MainGUIStage.class);
	
	protected final int SCREEN_WIDTH   = 900;
	protected final int SCREEN_HEIGHT  = 600;
	
	public MainGUIStage(MySQLDriver driver){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/MainGUI.fxml"));
			Parent root = (Parent)loader.load();
			this.setTitle("Database Login");
			Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
			MainGUIController mainGUI = loader.getController();
			mainGUI.setDBInfo(driver);
			this.setMinHeight(300);
			this.setMinWidth(400);
			this.setScene(scene);
			this.show();
		} catch (Exception e) {
			log.error("Error in GUI", e);
			System.exit(-1);
		}
	}
}

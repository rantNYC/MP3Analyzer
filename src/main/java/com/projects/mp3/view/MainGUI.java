package com.projects.mp3.view;

import java.net.URL;
import java.util.Properties;

import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.view.stages.GUIStage;
import com.projects.mp3.view.stages.MainGUIStage;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application{
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
		URL rootPath = getClass().getResource("/app.properties");
		 
		Properties appProps = new Properties();
		appProps.load(rootPath.openStream());
	
		MySQLDriver driver = new MySQLDriver(appProps.getProperty("app.mysql.hostname"),
												appProps.getProperty("app.mysql.username"),
												appProps.getProperty("app.mysql.password"));
		
		GUIStage login = new MainGUIStage(900, 600, driver, getClass().getResource("/FXML/MainGUI.fxml"));
		login.startStage();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}

package com.projects.mp3.view.stages;

import java.net.URL;
import java.sql.SQLException;

import com.projects.mp3.controller.MainGUIController;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;

import javafx.fxml.FXMLLoader;

public class MainGUIStage extends GUIStage {
	
	private MySQLDriver driver;
	
	public MainGUIStage(int width, int height, MySQLDriver driver, URL resource){
		super(width, height, resource);
		this.driver = driver;
	}
	
	@Override
	public void logicToGUI(FXMLLoader loader) throws SQLException {
		MainGUIController mainGUI = loader.getController();
		mainGUI.setDBInfo(driver);
		this.setOnCloseRequest(closeEvent -> {
			MainGUIController controller = 
				    loader.<MainGUIController>getController();
			controller.disposeDBConnection();
		});
	}
}

package com.projects.mp3.view.stages;

import java.net.URL;

import javafx.fxml.FXMLLoader;

public class LoginStage extends GUIStage {
	
	
	public LoginStage(int width, int heigth, URL resource){
		super(width, heigth, resource);
	}

	@Override
	public void logicToGUI(FXMLLoader loader) throws Exception {
		this.setResizable(false);
		return;
	}
}

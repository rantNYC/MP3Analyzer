package com.projects.mp3.view;

import com.projects.mp3.view.stages.GUIStage;
import com.projects.mp3.view.stages.LoginStage;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application{
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GUIStage login = new LoginStage(453, 214, getClass().getResource("/FXML/MainDBGUI.fxml"));
		login.startStage();
	}
}

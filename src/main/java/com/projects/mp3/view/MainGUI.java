package com.projects.mp3.view;

import com.projects.mp3.view.stages.LoginStage;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application{
	
	protected final int SCREEN_WIDTH   = 900;
	protected final int SCREEN_HEIGHT  = 600;
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		LoginStage login = new LoginStage();
	}
}

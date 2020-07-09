package com.projects.mp3.view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application{

	protected final int SCREEN_WIDTH   = 900;
	protected final int SCREEN_HEIGHT  = 600;
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Parent root = (Parent)FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
			primaryStage.setTitle("MP3 Analyzer");
			Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
			primaryStage.setMinHeight(300);
			primaryStage.setMinWidth(400);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

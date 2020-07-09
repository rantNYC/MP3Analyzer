package com.projects.mp3.controller.popup;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public abstract class PopupMessage {

	Alert alert;
	//TODO: Add enum for titles
	public PopupMessage(AlertType type) {
		alert = new Alert(type);
	}
	
	public void displayPopUp(String title, String header, String message) {
		alert.setTitle(title);
		alert.setHeaderText(header);

		Text text = new Text(message);
		text.setWrappingWidth(300);
		HBox box = new HBox(text);
		box.setPadding(new Insets(10,10,10,10));
		alert.getDialogPane().setContent(box);

		alert.showAndWait();
	}

}

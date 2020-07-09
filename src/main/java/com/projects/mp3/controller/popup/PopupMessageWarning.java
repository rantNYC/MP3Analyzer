package com.projects.mp3.controller.popup;

import javafx.scene.control.Alert.AlertType;

public class PopupMessageWarning extends PopupMessage{

	public PopupMessageWarning(AlertType type) {
		super(AlertType.WARNING);
	}
}

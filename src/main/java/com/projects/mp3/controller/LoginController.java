package com.projects.mp3.controller;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.engine.EngineUtilities;
import com.projects.mp3.controller.popup.PopupMessageError;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.view.stages.MainGUIStage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController {
	
	private static Logger log = LoggerFactory.getLogger(LoginController.class);
	
	private MySQLDriver dbDriver;
	//private ExecutorService service = Executors.newFixedThreadPool(10, guiFactory);
	//private SynchronizedDataContainer container = new SynchronizedDataContainer();
	
	@FXML
	TextField dbConnectionString;

	@FXML
	TextField dbUsername;

	@FXML
	PasswordField dbPassword;
	
	@FXML
	Button dbConnectionButton;
	
	@FXML
	Button cancelProgramButton;
	
	@FXML
	Label statusLabel;
	
	@FXML
	Label connectedToLabel;
	
	@FXML
	public void connectToDB() throws SQLException {
		//		dbDriver = new MySQLDriver();
		String connection = dbConnectionString.getText();
		String username = dbUsername.getText();
		String password = dbPassword.getText();

		if(EngineUtilities.isNullorEmpty(connection)) {
			PopupMessageError popUp = new PopupMessageError();
			popUp.displayPopUp("DB Error", "Connection Error", "Database url cannot be empty");
			return;
		}

		if(EngineUtilities.isNullorEmpty(username)) {
			PopupMessageError popUp = new PopupMessageError();
			popUp.displayPopUp("DB Error", "Username Error", "User cannot be empty");
			return;
		}

		if(EngineUtilities.isNullorEmpty(password)) {
			PopupMessageError popUp = new PopupMessageError();
			popUp.displayPopUp("DB Error", "Password Error", "Password cannot be empty");
			return;
		}

		try {
			dbDriver = new MySQLDriver(connection, username, password);
			//connectedToLabel.setText(connection);
			//statusLabel.setText(dbDriver.getStatus().toString());
			dbConnectionString.setDisable(true);
			dbUsername.setDisable(true);
			dbPassword.setDisable(true);
			//fetchDBInformation();
			log.info("Succesfully connected to database");
			MainGUIStage primaryStage = new MainGUIStage(900, 600, dbDriver, 
														 getClass().getResource("/FXML/MainGUI.fxml"));
			primaryStage.startStage();
			Stage thisStage = (Stage) this.dbConnectionButton.getScene().getWindow();
			thisStage.close();
		} catch (Exception ex) {
			if(dbDriver != null) dbDriver.closeConnection();
			dbConnectionString.setDisable(false);
			dbUsername.setDisable(false);
			dbPassword.setDisable(false);
			PopupMessageError popUp = new PopupMessageError();
			log.error("Cannot connect to DB", ex);
			popUp.displayPopUp("DB Error", "Error connecting to DB", 
					ex.getMessage());
		}

	}
	

	@FXML
	public void keyPressedHandler(KeyEvent event) throws SQLException {
		switch(event.getCode()) {
		case ENTER:
			connectToDB();
		default:
			return;
		}
	}
	
	@FXML
	public void programExit() {
		log.info("Program exiting...");
		System.exit(0);
	}

	
//	private void fetchDBInformation() throws SQLException {
//		//TODO: Try catch
//		if (!isDBConnected()) {
//			PopupMessageWarning warn = new PopupMessageWarning();
//			warn.displayPopUp("Warning", "Databased Disconnected", "Please login to the database to start this action");
//		}
//		DatabaseWorker worker = new DatabaseWorker("DBContainer", dbDriver, null, DBAction.Fetch);
//		ListenerWoker viewerListener = new TableButtonListener(this.dbTable, startActionButton, worker, container);
//		worker.addListener(viewerListener);
//		service.execute(viewerListener);
//	}
	
//	private boolean isDBConnected() {
//		if(dbDriver == null || dbDriver.getStatus() != DBStatus.Connected) {
//			PopupMessageWarning popUp = new PopupMessageWarning();
//			popUp.displayPopUp("DB Warning", "DB Connection", 
//					"Please connect to the database before running this action");
//			return false;
//		}
//
//		return true;
//	}

}

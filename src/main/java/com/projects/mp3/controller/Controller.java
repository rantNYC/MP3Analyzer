package com.projects.mp3.controller;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.projects.mp3.controller.engine.*;
import com.projects.mp3.controller.popup.*;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.Actions;
import com.projects.mp3.model.DBStatus;
import com.projects.mp3.model.MP3Annotation;
import com.projects.mp3.model.MP3Info;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;

public class Controller {


	//TODO: Store values in engine?
	//TODO: Avoid duplicates?
	//TODO: Move thread to new class
	//TODO: Signal to stop from user
	//TODO: Play songs from GUI
	//TODO: Implement logging to log tab
	
	private MySQLDriver dbDriver;
	private Engine engine;
	private final GUIThreadFactory guiFactory = new GUIThreadFactory("GUI Threads");
	private ExecutorService service = Executors.newFixedThreadPool(10, guiFactory);
	private AtomicInteger threadNum = new AtomicInteger(0);
	
	private final ObservableList<String> actions =FXCollections.observableArrayList(
			"Upload MP3 to DB", 
			"Songs in DB",
			"MP3 info in DB",
			"Generate Report");
	
	@FXML
	ComboBox<String> actionsBox;
	
	@FXML
	TextField dbConnectionString;
	
	@FXML
	TextField dbUsername;

	@FXML
	PasswordField dbPassword;

	@FXML
	TextField rootFolder;

	@FXML
	Button dbConnectionButton;

	@FXML	
	Button selectFolderButton;
	
	@FXML
	Button searchMP3Button;
	
	@FXML
	Button startActionButton;
	
	@FXML
	Button stopActionButton;
	
	@FXML
	TableView<MP3Info> rootTable;
	
	@FXML
	Label statusLabel;

	@FXML
	Label connectedToLabel;

	@FXML
	public void initialize() {
		//TODO: Move database login to another window before accessing this one
		actionsBox.setItems(actions);
		getMP3InfoColumns();
	}
	
	@FXML
	public void connectToDB() throws SQLException {
		//		dbDriver = new MySQLDriver();
		String connection = dbConnectionString.getText();
		String username = dbUsername.getText();
		String password = dbPassword.getText();

		if(EngineUtilities.isNullorEmpty(connection)) {
			PopupMessageError popUp = new PopupMessageError(null);
			popUp.displayPopUp("DB Error", "Connection Error", "Database url cannot be empty");
			return;
		}

		if(EngineUtilities.isNullorEmpty(username)) {
			PopupMessageError popUp = new PopupMessageError(null);
			popUp.displayPopUp("DB Error", "Username Error", "User cannot be empty");
			return;
		}

		if(EngineUtilities.isNullorEmpty(password)) {
			PopupMessageError popUp = new PopupMessageError(null);
			popUp.displayPopUp("DB Error", "Password Error", "User cannot be empty");
			return;
		}

		try {
			dbDriver = new MySQLDriver(connection, username, password);
			connectedToLabel.setText(connection);
			statusLabel.setText(dbDriver.getStatus().toString());
			dbConnectionString.setDisable(true);
			dbUsername.setDisable(true);
			dbPassword.setDisable(true);
			PopupMessageInfo popUp = new PopupMessageInfo(null);
			popUp.displayPopUp("Database connection", "Connection Sucess", "Succesfully connected to database");
		} catch (Exception ex) {
			PopupMessageError popUp = new PopupMessageError(null);
			popUp.displayPopUp("DB Error", "Credentials Error", 
					"Cannot connect to the DB, please check the connection and credentials");
		}

	}
	
	@FXML
	public void searchMP3Files() throws Exception {
		String rootPath = rootFolder.getText();
		if(EngineUtilities.isNullorEmpty(rootPath)) {
			PopupMessageError popup = new PopupMessageError(null);
			popup.displayPopUp("Root Folder", "Folder Error", "Select a root folder first");
			return;
		}
		
		File path = new File(rootPath);
		if(path.exists() && path.isDirectory()) {
			engine = new Engine(path);
			EngineWorker worker = new EngineWorker("SearchMP3 " + threadNum.incrementAndGet(), engine.getMP3Files());
			ListenerWoker viewerListener = new SearchMP3Listener(rootTable, searchMP3Button, worker);
			worker.addListener(viewerListener);
			guiFactory.setWorkerName(viewerListener.getWorkerName());
			service.execute(viewerListener);
		}else {
			PopupMessageError popup = new PopupMessageError(null);
			popup.displayPopUp("Root Folder", "Folder Error", "Folder does not exists");
			return;
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
	public void startAction() {
		String actionString = actionsBox.getValue();
		if(EngineUtilities.isNullorEmpty(actionString)) {
			PopupMessageWarning popUp = new PopupMessageWarning(null);
			popUp.displayPopUp("Action Warning", "Action Missing", 
						 "Please select an action from the dropdown list");
			return;
		}
		int indexAction = actions.indexOf(actionString);
		
		switch(Actions.getAction(indexAction)) {
			case Upload:
				uploadToDB();
			case GetSongs:
			case GetMP3:
			case GenerateReport:
				throw new UnsupportedOperationException("Action not yet implemented");
			default:
				//Shouldn't be here
				return;
		}
	}	

	@FXML
	public void stopAction() {
		List<Runnable> threads = service.shutdownNow();
	}
	
	@FXML
	public void chooseRootFolder() {
		DirectoryChooser dc = new DirectoryChooser();

		if(!EngineUtilities.isNullorEmpty(rootFolder.getText())) {
			dc.setInitialDirectory(new File(rootFolder.getText()));
		} else {
			dc.setInitialDirectory(new File(System.getProperty("user.home")));
		}

		File selectedDirectory = dc.showDialog(null);

		if(selectedDirectory != null) {
			if(selectedDirectory.exists() && selectedDirectory.isDirectory()) {
				rootFolder.setText(selectedDirectory.getAbsolutePath());
			} else {
				rootFolder.setPromptText("Please specify a valid path");
			}
		}
	}
	
	private void disposeDBConnection() {
		try {
			dbDriver.closeConnection();
			dbConnectionString.setDisable(false);
			dbUsername.setDisable(false);
			dbPassword.setDisable(false);
		} catch (Exception ex) {
			PopupMessageError popUp = new PopupMessageError(null);
			popUp.displayPopUp("DB Error", "Fatal Error",
					"Closing the connection failed " + ex.getStackTrace());
		}
	}
	
	private void getMP3InfoColumns(){
		for(Field field : MP3Info.class.getDeclaredFields()) {
			MP3Annotation value = field.getAnnotation(MP3Annotation.class);
			TableColumn<MP3Info, String> column = new TableColumn<MP3Info, String>(value.value());
			column.setCellValueFactory(new PropertyValueFactory<MP3Info, String>(field.getName()));
			rootTable.getColumns().add(column);
		}
	}	

	@FXML
	private void uploadToDB() {
		if(dbDriver == null || dbDriver.getStatus() != DBStatus.Connected) {
			PopupMessageWarning popUp = new PopupMessageWarning(null);
			popUp.displayPopUp("DB Warning", "DB Connection", 
					 "Please connect to the database befero running an action");
		}
	}
}

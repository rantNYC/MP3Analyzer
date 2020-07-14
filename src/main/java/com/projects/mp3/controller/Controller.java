package com.projects.mp3.controller;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.projects.mp3.controller.engine.*;
import com.projects.mp3.controller.popup.*;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.Actions;
import com.projects.mp3.model.DBStatus;
import com.projects.mp3.model.MP3Annotation;
import com.projects.mp3.model.MP3Info;
import com.projects.mp3.view.TextAreaAppender;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;

public class Controller {

	private static Logger log = LoggerFactory.getLogger(Controller.class);
	//TODO: Play songs from GUI
	
	private MySQLDriver dbDriver;
	private Engine engine;
	private final GUIThreadFactory guiFactory = new GUIThreadFactory("GUI Threads");
	private ExecutorService service = Executors.newFixedThreadPool(10, guiFactory);
	private AtomicInteger threadNum = new AtomicInteger(0);
	
	private ObservableList<String> logger = FXCollections.observableArrayList();
	
	private final ObservableList<String> actions = FXCollections.observableArrayList(
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
	TableView<MP3Info> folderTable;
	
	@FXML
	TableView<MP3Info> dbTable;
	
	@FXML
	ListView<String> logView;
	
	@FXML
	Label statusLabel;

	@FXML
	Label connectedToLabel;

	@FXML
	public void initialize() {
		//TODO: Move database login to another window before accessing this one
		log.info("Initializing GUI...");
		actionsBox.setItems(actions);
		List<TableColumn<MP3Info, String>> tableColumns = getMP3InfoColumns();
		folderTable.getColumns().setAll(tableColumns);
		dbTable.getColumns().setAll(tableColumns);
		logView.setItems(logger);
		TextAreaAppender.setTextArea(logger);
	}
	
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
			fetchDBInformation();
			PopupMessageInfo popUp = new PopupMessageInfo();
			popUp.displayPopUp("Database connection", "Connection Sucess", "Succesfully connected to database");
		} catch (Exception ex) {
			dbConnectionString.setDisable(false);
			dbUsername.setDisable(false);
			dbPassword.setDisable(false);
			PopupMessageError popUp = new PopupMessageError();
			log.error("Cannot connect to DB", ex);
			popUp.displayPopUp("DB Error", "Error connecting to DB", 
								ex.getMessage());
		}

	}
	
	private void fetchDBInformation() throws SQLException {
		//TODO: Try catch
		if (!isDBConnected()) return;
		List<MP3Info> dataInDb = dbDriver.getAllDataInDB();
		TableButtonListener.unique.addAll(dataInDb);
		dbTable.getItems().addAll(dataInDb);
	}

	private boolean isDBConnected() {
		if(dbDriver == null || dbDriver.getStatus() != DBStatus.Connected) {
			PopupMessageWarning popUp = new PopupMessageWarning();
			popUp.displayPopUp("DB Warning", "DB Connection", 
					 "Please connect to the database before running this action");
			return false;
		}
		
		return true;
	}
	
	@FXML
	public void searchMP3Files() throws Exception {
		String rootPath = rootFolder.getText();
		if(EngineUtilities.isNullorEmpty(rootPath)) {
			PopupMessageError popup = new PopupMessageError();
			popup.displayPopUp("Root Folder", "Folder Error", "Select a root folder first");
			return;
		}
		
		File path = new File(rootPath);
		if(path.exists() && path.isDirectory()) {
			engine = new Engine(path);
			EngineWorker worker = new EngineWorker("SearchMP3 " + threadNum.incrementAndGet(), engine.getMP3Files());
			ListenerWoker viewerListener = new TableButtonListener(folderTable, searchMP3Button, worker);
			worker.addListener(viewerListener);
			guiFactory.setWorkerName(viewerListener.getWorkerName());
			service.execute(viewerListener);
		}else {
			PopupMessageError popup = new PopupMessageError();
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
			PopupMessageWarning popUp = new PopupMessageWarning();
			popUp.displayPopUp("Action Warning", "Action Missing", 
						 "Please select an action from the dropdown list");
			return;
		}
		int indexAction = actions.indexOf(actionString);
		
		switch(Actions.getAction(indexAction)) {
			case Upload:
				uploadToDB();
				break;
			case GetSongs:
				//TODO: Add functionality
			case GetMP3:
				//TODO: Add functionality
			case GenerateReport:
				//TODO: Add functionality
				throw new UnsupportedOperationException("Action not yet implemented");
			default:
				//Shouldn't be here
				return;
		}
	}	

	@FXML
	public void stopAction() throws InterruptedException {
		service.shutdownNow();
		service.awaitTermination(5, TimeUnit.SECONDS);
		service = Executors.newFixedThreadPool(10, guiFactory);
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
			PopupMessageError popUp = new PopupMessageError();
			popUp.displayPopUp("DB Error", "Fatal Error",
					"Closing the connection failed " + ex.getStackTrace());
		}
	}
	
	private List<TableColumn<MP3Info, String>> getMP3InfoColumns(){
		List<TableColumn<MP3Info, String>> columns = new ArrayList<TableColumn<MP3Info, String>>();
		for(Field field : MP3Info.class.getDeclaredFields()) {
			MP3Annotation value = field.getAnnotation(MP3Annotation.class);
			TableColumn<MP3Info, String> column = new TableColumn<MP3Info, String>(value.value());
			column.setCellValueFactory(new PropertyValueFactory<MP3Info, String>(field.getName()));
			columns.add(column);
//			rootTable.getColumns().add(column);
		}
		
		return ImmutableList.copyOf(columns);
	}	

	private void uploadToDB() {
		if (!isDBConnected()) return;
		DatabaseWorker worker = new DatabaseWorker("Upload_To_DB", dbDriver, new ArrayList<MP3Info>(TableButtonListener.unique));
		ListenerWoker viewerListener = new TableButtonListener(this.dbTable, startActionButton, worker);
		worker.addListener(viewerListener);
		service.execute(viewerListener);
	}
}

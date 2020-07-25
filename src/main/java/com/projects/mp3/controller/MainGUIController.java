package com.projects.mp3.controller;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.projects.mp3.controller.engine.*;
import com.projects.mp3.controller.popup.*;
import com.projects.mp3.controller.storage.DBAction;
import com.projects.mp3.controller.storage.DBStatus;
import com.projects.mp3.controller.storage.DatabaseWorker;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Annotation;
import com.projects.mp3.model.MP3Info;
import com.projects.mp3.model.SynchronizedDataContainer;
import com.projects.mp3.view.TextAreaAppender;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;

public class MainGUIController {

	private static Logger log = LoggerFactory.getLogger(MainGUIController.class);
	//TODO: Play songs from GUI
	//TODO: Generate report action
	//TODO: Create use login before accessing the main window. 
	//TODO: Store user information, (Encryption, hashing and local caching user id)
	//TODO: DB Side: Modify DB Schema to add User table and MP3Info-User table
	//	 	Create Stored Procedure to save User information
	//Java Side: Create function to take stored procedure and save user information to DumbB
	//Research Side: Encryption algorithm to store user information Java and DB 
	//	       Local encrypted file to save user information (Possible DB Information too)
	//To think about: Create a Login GUI for the user. Get rid of DB Login GUI (Next steps are going to be using cloud base like FireBase DB or AWS)
	//		Think about user/admin classes and behaviours


	private MySQLDriver dbDriver;
	private Engine engine;
	private final GUIThreadFactory guiFactory = new GUIThreadFactory("GUI Threads");
	private ExecutorService service = Executors.newFixedThreadPool(10, guiFactory);
	private SynchronizedDataContainer container = new SynchronizedDataContainer();

	private ObservableList<String> logger = FXCollections.observableArrayList();

	@FXML
	TextField dbConnectionString;

	@FXML
	TextField dbUsername;

	@FXML
	PasswordField dbPassword;

	@FXML
	TextField rootFolder;

	@FXML	
	Button selectFolderButton;

	@FXML
	Button startButton;

	@FXML
	Button stopButton;

	@FXML
	Button refreshButton;
	
	Button reportButton;
	
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

	public void setDBInfo(MySQLDriver _dbDriver) throws SQLException {
		dbDriver = _dbDriver;
		statusLabel.setText(_dbDriver.getStatus().toString());
		connectedToLabel.setText(_dbDriver.getConnectionPath());
		fetchDBInformation();
	}
	
	@FXML
	public void initialize() {
		log.info("Initializing Main GUI and fetching DB...");
		folderTable.getColumns().setAll(getMP3InfoColumns());
		dbTable.getColumns().setAll(getMP3InfoColumns());
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
			dbConnectionString.setDisable(true);
			dbUsername.setDisable(true);
			dbPassword.setDisable(true);
			fetchDBInformation();
			log.info("Succesfully connected to database");
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
	public void keyPressedHandler(KeyEvent event) throws SQLException {
		switch(event.getCode()) {
		case ENTER:
			connectToDB();
		default:
			return;
		}
	}

	@FXML
	public void onStartHandle() throws Exception {
		//TODO: Wrong need to synchronize
		ListenerWorker searchWorker = searchMP3Files();
		ListenerWorker uploadWorker = uploadToDB(searchWorker);
		
		service.submit(uploadWorker);
	}

	@FXML
	public void onReportHandle() {
		//TODO: Add functionality
		//Implementation: Excel file in the output folder (decided by the user?), three sheets
		//One summary, one db report, and one folder report. 
		//Summary will have information like: How many files in DB, how many files in root folder,
		//how many files from root are in DB, how many files from DB are in root, and the diff 
		//between them
		//DB Sheet will have all the records in the db
		//Root folder will have all the records in the root folder
		//All this based on the table view items
	}
	
	@FXML
	public void onRefreshHandle() {
		NotifyingWorker worker = new DatabaseWorker(ContainerType.DBContainer.toString(), dbDriver, DBAction.Refresh, null);
		TableButtonListener viewerListener = new TableButtonListener(this.dbTable, refreshButton, worker, container);
		viewerListener.refreshViewer();
		worker.addListener(viewerListener);
		service.execute(viewerListener);
	}
	
	@FXML
	public void onStopHandle() throws InterruptedException {
		service.shutdownNow();
		service.awaitTermination(5, TimeUnit.SECONDS);
		service = Executors.newFixedThreadPool(10, guiFactory);
		log.info("Program Stopped...");
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

	public void disposeDBConnection() {
		try {
//			if(dbDriver == null) return;
			dbDriver.closeConnection();
		} catch (Exception ex) {
			PopupMessageError popUp = new PopupMessageError();
			popUp.displayPopUp("DB Error", "Fatal Error",
					"Closing the connection failed " + ex.getMessage());
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

	private ListenerWorker uploadToDB(ListenerWorker searchListener) {
		if (!isDBConnected()) {
			return null;
		}
		NotifyingWorker worker = new DatabaseWorker(ContainerType.DBContainer.toString(), dbDriver, DBAction.Upload, searchListener);
		ListenerWorker viewerListener = new TableButtonListener(this.dbTable, startButton, worker, container);
		worker.addListener(viewerListener);
		return viewerListener;
	}

	private void fetchDBInformation() throws SQLException {
		//TODO: Try catch
		if (!isDBConnected()) {
			PopupMessageWarning warn = new PopupMessageWarning();
			warn.displayPopUp("Warning", "Databased Disconnected", "Please login to the database to start this action");
		}
		NotifyingWorker worker = new DatabaseWorker("DBContainer", dbDriver, DBAction.Fetch, null);
		ListenerWorker viewerListener = new TableButtonListener(this.dbTable, startButton, worker, container);
		worker.addListener(viewerListener);
		service.execute(viewerListener);
	}

	private ListenerWorker searchMP3Files() throws Exception {
		String rootPath = rootFolder.getText();
		if(EngineUtilities.isNullorEmpty(rootPath)) {
			PopupMessageError popup = new PopupMessageError();
			popup.displayPopUp("Root Folder", "Folder Error", "Select a root folder first");
			return null;
		}

		File path = new File(rootPath);
		if(path.exists() && path.isDirectory()) {
			engine = new Engine(path);
			NotifyingWorker worker = new EngineWorker(ContainerType.FolderContainer.toString(), engine.getMP3Files());
			ListenerWorker viewerListener = new TableButtonListener(folderTable, startButton, worker, container);
			worker.addListener(viewerListener);
			guiFactory.setWorkerName(viewerListener.getWorkerName());
			return viewerListener;
		}else {
			PopupMessageError popup = new PopupMessageError();
			popup.displayPopUp("Root Folder", "Folder Error", "Folder does not exists");
			return null;
		}
	}
	
}

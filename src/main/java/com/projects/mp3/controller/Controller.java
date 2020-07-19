package com.projects.mp3.controller;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import com.projects.mp3.model.Action;
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

public class Controller {

	private static Logger log = LoggerFactory.getLogger(Controller.class);
	//TODO: Play songs from GUI
	//TODO: Add app dataset with information
	//TODO: Fetch data from DB action
	//TODO: Generate report action
	//TODO: Move information gathering from the Table view
	//Soon, we will implement constrain to the display and data will be lost if we
	//reference the table view. Instead hold that information in other class, and
	//add it to the viewer when refresh, or something. This class is where the data
	//from the actions, search mp3, connect to db will go.
	//TODO: Create use login before accessing the main window. 
	//TODO: Store user information, (Encryption, hashing and local caching user id)

	private MySQLDriver dbDriver;
	private Engine engine;
	private final GUIThreadFactory guiFactory = new GUIThreadFactory("GUI Threads");
	private ExecutorService service = Executors.newFixedThreadPool(10, guiFactory);
	private SynchronizedDataContainer container = new SynchronizedDataContainer();

	private ObservableList<String> logger = FXCollections.observableArrayList();

	private final ObservableList<String> actions = FXCollections.observableArrayList(
			"Upload MP3 to DB",
			"Refresh DB",
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
		//		List<TableColumn<MP3Info, String>> tableColumns = getMP3InfoColumns();
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
			dbDriver = new MySQLDriver(connection, username, password);
			connectedToLabel.setText(connection);
			statusLabel.setText(dbDriver.getStatus().toString());
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

	private void fetchDBInformation() throws SQLException {
		//TODO: Try catch
		if (!isDBConnected()) {
			PopupMessageWarning warn = new PopupMessageWarning();
			warn.displayPopUp("Warning", "Databased Disconnected", "Please login to the database to start this action");
		}
		DatabaseWorker worker = new DatabaseWorker("DBContainer", dbDriver, null, DBAction.Fetch);
		ListenerWoker viewerListener = new TableButtonListener(this.dbTable, startActionButton, worker, container);
		worker.addListener(viewerListener);
		service.execute(viewerListener);
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
			EngineWorker worker = new EngineWorker(ContainerType.FolderContainer.toString(), engine.getMP3Files());
			ListenerWoker viewerListener = new TableButtonListener(folderTable, searchMP3Button, worker, container);
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

		switch(Action.getAction(indexAction)) {
			case Upload:
				uploadToDB();
				break;
			case RefreshDB:
				refreshDB();
				break;
			case GenerateReport:
				//TODO: Add functionality
				//Implementation: Excel file in the output folder (decided by the user?), three sheets
				//One summary, one db report, and one folder report. 
				//Summary will have information like: How many files in DB, how many files in root folder,
				//how many files from root are in DB, how many files from DB are in root, and the diff 
				//between them
				//DB Sheet will have all the records in the db
				//Root folder will have all the records in the root folder
				//All this based on the table view items
				throw new UnsupportedOperationException("Action not yet implemented");
			default:
				//Shouldn't be here
				return;
		}
	}	

	private void refreshDB() {
		DatabaseWorker worker = new DatabaseWorker(ContainerType.DBContainer.toString(), dbDriver, 
													null, DBAction.Upload);
		ListenerWoker viewerListener = new TableButtonListener(this.dbTable, startActionButton, worker, container);
		worker.addListener(viewerListener);
		service.execute(viewerListener);
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
		if (!isDBConnected()) {
			return;
		}
		List<MP3Info> dataToUpload = container.setDifferencerRight(ContainerType.DBContainer, ContainerType.FolderContainer);
		DatabaseWorker worker = new DatabaseWorker(ContainerType.DBContainer.toString(), dbDriver, dataToUpload, DBAction.Upload);
		ListenerWoker viewerListener = new TableButtonListener(this.dbTable, startActionButton, worker, container);
		worker.addListener(viewerListener);
		service.execute(viewerListener);
	}

	private List<MP3Info> generateMP3ToUpload() {
		
		
		
		return null;
	}
}

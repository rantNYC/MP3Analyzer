package com.projects.mp3.controller.storage.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.mp3.controller.engine.EngineUtilities;
import com.projects.mp3.controller.storage.DBStatus;
import com.projects.mp3.model.MP3Info;

public class MySQLDriver {

	private static final Logger log = LoggerFactory.getLogger(MySQLDriver.class);

	Connection conn;
	DBStatus status;
	String connectionPath;

	//	public MySQLDriver() throws SQLException {
	//		conn = DriverManager.getConnection(connectionString, username, password);
	//		status = DBStatus.Connected;
	//		EngineUtilities.printConsole("Successfully connected to DB");
	//	}

	public MySQLDriver(String dbConnection, String username, String password) throws SQLException {
		conn = DriverManager.getConnection(dbConnection, username, password);
		status = DBStatus.Connected;
		connectionPath = dbConnection;
	}

	public DBStatus getStatus() {
		return status;
	}
	
	public String getConnectionPath() {
		return connectionPath;
	}

	public void insertMP3ToDB(MP3Info mp3Info) throws SQLException {
		if(EngineUtilities.isNullorEmpty(mp3Info.getSongName())) 
			throw new IllegalArgumentException(String.format("Song name %s cannot be null or empty in %s", mp3Info, mp3Info.getPath()));

		if(EngineUtilities.isNullorEmpty(mp3Info.getArtistName())) 
			throw new IllegalArgumentException(String.format("Artist name %s cannot be null or empty in %s", mp3Info, mp3Info.getPath()));

		try (CallableStatement statement = conn.prepareCall("{call insert_info(?, ?, ? , ?, ?, ?, ?, ?, ?)}")){
			statement.setQueryTimeout(EngineUtilities.TIMEOUT_SECONDS);
			statement.setString(1, mp3Info.getSongName());
			statement.setString(2, mp3Info.getArtistName());
			statement.setString(3, mp3Info.getAlbum());
			statement.setString(4, mp3Info.getGenre());
			statement.setString(5, mp3Info.getBitRate());
			statement.setString(6, mp3Info.getDescription());
			statement.setString(7, mp3Info.getPath());
			statement.setDouble(8, mp3Info.getDuration());
			statement.setDouble(9, mp3Info.getSizeMb());
			//			printConsole(statement);

			statement.execute();
			//			printConsole("MP3 Information stored in the DB");
		}
	}

	public List<MP3Info> getAllDataInDB() throws SQLException{
		List<MP3Info> data = new ArrayList<MP3Info>();
		try (CallableStatement statement = conn.prepareCall("{call select_all_info}")){
			statement.setQueryTimeout(EngineUtilities.TIMEOUT_SECONDS);

			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				MP3Info info = new MP3Info(rs.getString("songName"), rs.getString("artistName"), rs.getString("albumName"), 
						rs.getString("genreName"), rs.getString("bitrate"), rs.getString("path"),
						rs.getDouble("duration"),rs.getDouble("size"));
				info.setDescription(rs.getString("description"));
				log.info(String.format("Fetched %s from the Database", info.toString()));
				data.add(info);
			}
		}
		return data;
	}

	public final void closeConnection() throws SQLException {
		if(conn != null && !conn.isClosed()) conn.close();
		status = DBStatus.Disconnected;
	}

	protected void finalize() throws SQLException {
		closeConnection();
	}

}
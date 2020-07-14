package com.projects.mp3.controller.storage.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.projects.mp3.controller.engine.EngineUtilities;
import com.projects.mp3.model.DBStatus;
import com.projects.mp3.model.MP3Annotation;
import com.projects.mp3.model.MP3Info;

public class MySQLDriver {

	final String connectionString = "jdbc:mysql://localhost:3306/mp3schema";
	final String username = "java";
	final String password = "password";

	Connection conn;
	DBStatus status;

	public MySQLDriver() throws SQLException {
		conn = DriverManager.getConnection(connectionString, username, password);
		status = DBStatus.Connected;
//		EngineUtilities.printConsole("Successfully connected to DB");
	}
	
	public MySQLDriver(String dbConnection, String username, String password) throws SQLException {
		conn = DriverManager.getConnection(dbConnection, username, password);
		status = DBStatus.Connected;
	}

	public DBStatus getStatus() {
		return status;
	}

	public void insertMP3ToDB(MP3Info mp3Info) throws SQLException {
		if(EngineUtilities.isNullorEmpty(mp3Info.getSongName())) 
			throw new IllegalArgumentException(String.format("Song name %s cannot be null or empty in %s", mp3Info, mp3Info.getPath()));
		
		if(EngineUtilities.isNullorEmpty(mp3Info.getArtistName())) 
			throw new IllegalArgumentException(String.format("Artist name %s cannot be null or empty in %s", mp3Info, mp3Info.getPath()));
		
		CallableStatement statement = null;
		try {
			statement = conn.prepareCall("{call insert_info(?, ?, ? , ?, ?, ?, ?, ?, ?)}");
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
		} finally {
			if(statement != null) statement.close();
		}
	}

	public List<MP3Info> getAllDataInDB() throws SQLException{
		CallableStatement statement = null;
		List<MP3Info> data = new ArrayList<MP3Info>();
		try {
			statement = conn.prepareCall("{call select_all_info}");
//			printConsole(statement);
			
//			boolean hasResults = statement.execute();
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				MP3Info info = new MP3Info(rs.getString("songName"), rs.getString("artistName"), rs.getString("albumName"), 
										   rs.getString("genreName"), rs.getString("bitrate"), rs.getString("path"),
										   rs.getDouble("duration"),rs.getDouble("size"));
				info.setDescription(rs.getString("description"));
				data.add(info);
			}
			
//			printConsole("MP3 Information stored in the DB");
		}
		finally {
			if(statement != null) statement.close();
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
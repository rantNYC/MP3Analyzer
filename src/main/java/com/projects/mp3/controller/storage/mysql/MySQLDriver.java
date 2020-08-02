package com.projects.mp3.controller.storage.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.projects.mp3.controller.engine.EngineUtilities;
import com.projects.mp3.controller.storage.DBStatus;
import com.projects.mp3.model.AudioInfo;

public class MySQLDriver {

	Connection conn;
	DBStatus status;
	String connectionPath;

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

	public void insertMP3ToDB(AudioInfo AudioInfo) throws SQLException {
		if(EngineUtilities.isNullorEmpty(AudioInfo.getSongName()) || EngineUtilities.isNullorEmpty(AudioInfo.getArtistName())) 
			throw new IllegalArgumentException(String.format("%s contains null for primary keys", AudioInfo));

		try (CallableStatement statement = conn.prepareCall("{call insert_info(?, ?, ? , ?, ?, ?, ?, ?, ?)}")){
			statement.setQueryTimeout(EngineUtilities.TIMEOUT_SECONDS);
			statement.setString(1, AudioInfo.getSongName());
			statement.setString(2, AudioInfo.getArtistName());
			statement.setString(3, AudioInfo.getAlbum());
			statement.setString(4, AudioInfo.getGenre());
			statement.setString(5, AudioInfo.getBitRate());
			statement.setString(6, AudioInfo.getDescription());
			statement.setString(7, AudioInfo.getLocalPath());
			statement.setDouble(8, AudioInfo.getDuration());
			statement.setDouble(9, AudioInfo.getSizeMb());
			//			printConsole(statement);

			statement.execute();
			//			printConsole("MP3 Information stored in the DB");
		}
	}

	public List<AudioInfo> getAllDataInDB() throws SQLException{
		List<AudioInfo> data = new ArrayList<AudioInfo>();
		try (CallableStatement statement = conn.prepareCall("{call select_all_info}")){
			statement.setQueryTimeout(EngineUtilities.TIMEOUT_SECONDS);

			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				AudioInfo info = new AudioInfo(rs.getString("songName"), rs.getString("artistName"), rs.getString("albumName"), 
						rs.getString("genreName"), rs.getString("bitrate"), rs.getString("path"),
						rs.getDouble("duration"),rs.getDouble("size"));
				info.setDescription(rs.getString("description"));
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
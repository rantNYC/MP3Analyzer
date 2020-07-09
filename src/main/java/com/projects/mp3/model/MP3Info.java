package com.projects.mp3.model;

import com.projects.mp3.controller.engine.EngineUtilities;

public class MP3Info {
	
	@MP3Annotation("Song Name")
	String songName;
	@MP3Annotation("Artist Name")
	String artistName;
	@MP3Annotation("Album Name")
	String album;
	@MP3Annotation("Genre")
	String genre;
	@MP3Annotation("Bitrate")
	String bitRate;
	@MP3Annotation("Description")
	String description;
	@MP3Annotation("File path")
	String path;
	
	@MP3Annotation("Duration")
	double duration;
	@MP3Annotation("Size(Mb)")
	double sizeMb;
	
	public MP3Info(String songName, String artistName, String album, String genre, String bitRate,
					String path, String duration, long sizeBytes) {
		this.songName = songName;
		this.artistName = artistName;
		this.album = album;
		this.genre = genre;
		this.bitRate = bitRate;
		this.path = path;
		this.duration = Math.floor(Double.parseDouble(duration)/EngineUtilities.MILISECONDS_TO_MINUTE * 100) / 100;
		this.sizeMb = sizeBytes/EngineUtilities.BYTES_TO_MEGABYTES;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSongName() {
		return songName;
	}

	public String getArtistName() {
		return artistName;
	}

	public String getAlbum() {
		return album;
	}

	public String getGenre() {
		return genre;
	}

	public String getBitRate() {
		return bitRate;
	}

	public String getPath() {
		return path;
	}

	public double getDuration() {
		return duration;
	}

	public double getSizeMb() {
		return sizeMb;
	}

	@Override
	public String toString() {
		return "MP3Info [songName=" + songName + ", artistName=" + artistName + "]";
	}

}

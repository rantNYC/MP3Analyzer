package com.projects.mp3.model;

public class AudioInfo {
	
	@AudioFileAnnotation("Song Name")
	private String songName;
	@AudioFileAnnotation("Artist Name")
	private String artistName;
	@AudioFileAnnotation("Album Name")
	private String album;
	@AudioFileAnnotation("Genre")
	private String genre;
	@AudioFileAnnotation("Bitrate")
	private String bitRate;
	@AudioFileAnnotation("Description")
	private String description;
	@AudioFileAnnotation("File path")
	private String localPath;

	
	@AudioFileAnnotation("Duration")
	private double duration;
	@AudioFileAnnotation("Size(Mb)")
	private double sizeMb;
	
	public AudioInfo(String songName, String artistName, String album, String genre, 
					String bitRate,	String path, double duration, double sizeMB) {
		this.songName = songName;
		this.artistName = artistName;
		this.album = album;
		this.genre = genre;
		this.bitRate = bitRate;
		this.localPath = path;
		this.duration = duration;
		this.sizeMb = sizeMB;
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

	public String getLocalPath() {
		return localPath;
	}

	public double getDuration() {
		return duration;
	}

	public double getSizeMb() {
		return sizeMb;
	}

	public String getRemotePath() {
		if(getSongName() == null || getSongName().isEmpty()) {
			throw new IllegalArgumentException("Need song name to get remote path");
		}
		//TODO: Remove it and do it per user
		return String.format("/Songs/Test/%s.file", getSongName());
	}

	
	@Override
	public String toString() {
		return "AudioInfo [songName=" + songName + ", artistName=" + artistName + ", path=" + localPath + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artistName == null) ? 0 : artistName.hashCode());
		result = prime * result + ((localPath == null) ? 0 : localPath.hashCode());
		result = prime * result + ((songName == null) ? 0 : songName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AudioInfo)) {
			return false;
		}
		AudioInfo other = (AudioInfo) obj;
		if (artistName == null) {
			if (other.artistName != null) {
				return false;
			}
		} else if (!artistName.equals(other.artistName)) {
			return false;
		}
		if (localPath == null) {
			if (other.localPath != null) {
				return false;
			}
		} else if (!localPath.equals(other.localPath)) {
			return false;
		}
		if (songName == null) {
			if (other.songName != null) {
				return false;
			}
		} else if (!songName.equals(other.songName)) {
			return false;
		}
		return true;
	}


}

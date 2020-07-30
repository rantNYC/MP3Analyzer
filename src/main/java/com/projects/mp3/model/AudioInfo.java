package com.projects.mp3.model;

public class AudioInfo {
	
	@AudioFileAnnotation("Song Name")
	String songName;
	@AudioFileAnnotation("Artist Name")
	String artistName;
	@AudioFileAnnotation("Album Name")
	String album;
	@AudioFileAnnotation("Genre")
	String genre;
	@AudioFileAnnotation("Bitrate")
	String bitRate;
	@AudioFileAnnotation("Description")
	String description;
	@AudioFileAnnotation("File path")
	String path;
	
	@AudioFileAnnotation("Duration")
	double duration;
	@AudioFileAnnotation("Size(Mb)")
	double sizeMb;
	
	public AudioInfo(String songName, String artistName, String album, String genre, 
					String bitRate,	String path, double duration, double sizeMB) {
		this.songName = songName;
		this.artistName = artistName;
		this.album = album;
		this.genre = genre;
		this.bitRate = bitRate;
		this.path = path;
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
		return "AudioInfo [songName=" + songName + ", artistName=" + artistName + ", path=" + path + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artistName == null) ? 0 : artistName.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
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

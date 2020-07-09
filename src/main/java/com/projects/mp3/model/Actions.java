package com.projects.mp3.model;

public enum Actions {
	Upload,
	GetSongs,
	GetMP3,
	GenerateReport;
	
	private static Actions[] values = Actions.values();
	
	public static Actions getAction(int index) {
		if(index < 0 || index >= values.length)
			throw new IllegalArgumentException("Index is out of bounds");
		
		return values[index];
	}
}

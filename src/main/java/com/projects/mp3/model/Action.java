package com.projects.mp3.model;

public enum Action {
	Upload,
	GetSongs,
	GetMP3,
	GenerateReport;
	
	private static Action[] values = Action.values();
	
	public static Action getAction(int index) {
		if(index < 0 || index >= values.length)
			throw new IllegalArgumentException("Index is out of bounds");
		
		return values[index];
	}
}

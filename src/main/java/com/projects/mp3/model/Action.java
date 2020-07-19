package com.projects.mp3.model;

public enum Action {
	Upload,
	RefreshDB,
	GenerateReport;
	//TODO: Assign names to each one when to String
	private static Action[] values = Action.values();
	
	public static Action getAction(int index) {
		if(index < 0 || index >= values.length)
			throw new IllegalArgumentException("Index is out of bounds");
		
		return values[index];
	}
}

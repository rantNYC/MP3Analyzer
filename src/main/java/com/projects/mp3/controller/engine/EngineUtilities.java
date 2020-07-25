package com.projects.mp3.controller.engine;

public class EngineUtilities {

	public static final int MILISECONDS_TO_MINUTE = 60000;
	public static final int BYTES_TO_MEGABYTES = 1000000;
		
	public static final int TIMEOUT_SECONDS = 120;
	
	private EngineUtilities() {}
	
//	public static void printConsole(String message) {
//		System.out.println(message);
//	}
	
	public static boolean isNullorEmpty(String s) {
		return s == null || s.isEmpty() || s.isBlank();
	}
}

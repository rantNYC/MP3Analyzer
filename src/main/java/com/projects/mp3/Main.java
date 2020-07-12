package com.projects.mp3;

import java.io.File;
import java.util.List;

import com.projects.mp3.controller.engine.Engine;
import com.projects.mp3.controller.engine.EngineUtilities;
import com.projects.mp3.controller.engine.MP3Decoder;
import com.projects.mp3.controller.storage.mysql.MySQLDriver;
import com.projects.mp3.model.MP3Info;

public class Main {

	public static void main(String[] args) throws Exception {
		String dir = "D:\\Songs";
		Engine engine = new Engine(dir);

		MySQLDriver driver = new MySQLDriver();

		List<File> files = engine.getMP3Files();
		int totalFilesInSystem = files.size();
		int totalFilesProcessed = 0;
		for(File file : files) {	
			try {
				MP3Decoder decoder = new MP3Decoder(file.getAbsolutePath());
				MP3Info info = decoder.decodeInformation();
				driver.insertMP3ToDB(info);
				EngineUtilities.printConsole("Succesfully inserted " + info + " into the database");
				++totalFilesProcessed;
			}catch (Exception ex){
//				EngineUtilities.printConsole(String.format("Exception found while inserting %s", info));
				EngineUtilities.printConsole(ex.getMessage());
//				ex.printStackTrace();
			}
		}
		
		EngineUtilities.printConsole(String.format("Finished processing %d out of %d", totalFilesProcessed, totalFilesInSystem));
	}

}

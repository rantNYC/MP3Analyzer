package com.projects.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.tika.Tika;

public class Main {
	
	public static void main(String[] args) throws Exception {
		File dir = new File("F:\\Misc\\Songs");

		List<File> mp3Files = (List<File>) FileUtils.listFiles(dir, new RegexFileFilter(".+\\..+"), DirectoryFileFilter.DIRECTORY);
//		Decoder dec = new Decoder();
		for(File file : mp3Files) {	
			String mediaType = new Tika().detect(new FileInputStream(file));
            System.out.println(mediaType);
//			try {
//				AudioInfo output = dec.decodeInformation(file);
//				System.out.println(output);
//			}catch (Exception ex){
//				ex.printStackTrace();
//			}
		}
		
//		EngineUtilities.printConsole(String.format("Finished processing %d out of %d", totalFilesProcessed, totalFilesInSystem));
	}

}

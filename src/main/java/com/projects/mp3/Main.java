package com.projects.mp3;

import com.projects.mp3.controller.storage.dropbox.DropboxAccount;

public class Main {
	
	public static void main(String[] args) throws Exception {
		DropboxAccount acc = new DropboxAccount();
		acc.downloadAllUserFiles("D:\\Downloads\\demo\\demo\\Test");
//		File dir = new File("F:\\Misc\\Songs");

//		List<File> mp3Files = (List<File>) FileUtils.listFiles(dir, new RegexFileFilter(".+\\..+"), DirectoryFileFilter.DIRECTORY);
//		Decoder dec = new Decoder();
//		for(File file : mp3Files) {	
//			String mediaType = new Tika().detect(new FileInputStream(file));
//            System.out.println(mediaType);
//			try {
//				AudioInfo output = dec.decodeInformation(file);
//				System.out.println(output);
//			}catch (Exception ex){
//				ex.printStackTrace();
//			}
//		}
		
//		EngineUtilities.printConsole(String.format("Finished processing %d out of %d", totalFilesProcessed, totalFilesInSystem));
	}

}

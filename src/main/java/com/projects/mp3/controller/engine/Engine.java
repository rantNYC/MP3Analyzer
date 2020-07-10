package com.projects.mp3.controller.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

public class Engine {

	File dir;
	List<File> mp3Files;
	
	public Engine(String dir) {
		File posDir = new File(dir);
		verifyFolder(posDir);
		this.dir = posDir;
	}
	
	public Engine(File dir) {
		verifyFolder(dir);
		this.dir = dir;
	}
	
	private void verifyFolder(File dir) {
		if(!dir.exists()) throw new IllegalArgumentException(String.format("Directory %s does not exists", dir));
		if(!dir.isDirectory()) throw new IllegalArgumentException(String.format("Input %s is not a directory", dir));
	}
	
	public List<File> getMP3Files() {
		if(mp3Files == null) {
			generateMP3Files();
			return new ArrayList<File>(mp3Files);
		}else {
			return new ArrayList<File>(mp3Files);
		}
	}
	
	private void generateMP3Files() {
		//TODO: Deal with more formats
		mp3Files = (List<File>) FileUtils.listFiles(dir, new RegexFileFilter(".+\\.mp3"), DirectoryFileFilter.DIRECTORY);
	}
}

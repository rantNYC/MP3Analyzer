package com.projects.mp3.controller.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Engine {

	private static final Logger log = LoggerFactory.getLogger(Engine.class);
	
	File dir;
	List<File> files;
	
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
	
	private void getFilesFromDirectory() {
		log.info("Processing: " + dir);
		files = (List<File>) FileUtils.listFiles(dir, new RegexFileFilter(".+\\..+"), DirectoryFileFilter.DIRECTORY);
	}
	
	public List<File> getAllFiles() {
		if(files == null) {
			getFilesFromDirectory();
			return new ArrayList<File>(files);
		}else {
			return new ArrayList<File>(files);
		}
	}
	
	public int getNumFiles() {
		return files == null ? 0 : files.size();
	}
}

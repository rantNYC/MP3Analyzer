package com.projects.mp3.controller.storage.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.projects.mp3.model.AudioInfo;

public class DropboxAccount {

	private final Logger log = LoggerFactory.getLogger(DropboxAccount.class);
	
	//TODO: Remove access token from here, properties
	private final String ACCESS_TOKEN = "4LRuQ5azJFAAAAAAAAAADypjZLKHdGHWho9D65MBmbqJ4864kw-Y_KFAcMpQ45bv";
	private DbxClientV2 client;

    public DropboxAccount() {
    	//TODO: Make configurable, properties
    	DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/Apps").build();
        this.client = new DbxClientV2(config, ACCESS_TOKEN);
    }
    
    //TODO: Encrypt file
    public void uploadFileToRemote(AudioInfo info) throws Exception {
    	try (InputStream in = new FileInputStream(info.getLocalPath())) {
            FileMetadata metadata = client.files().uploadBuilder(info.getRemotePath())
            								.uploadAndFinish(in);
            log.info("Succesfully uploaded: " + metadata.getName());
        }
    }
    
    //TODO: Untested
    public File downloadFileToFolder(String outputFolder, AudioInfo info) throws Exception {
    	File file = new File(outputFolder+File.separator+info.getSongName());
    	if(file.exists())
    		file.delete();
    	
    	OutputStream outputStream = new FileOutputStream(file);
    	FileMetadata metadata = client.files()
						    	        .downloadBuilder(info.getRemotePath())
						    	        .download(outputStream);
    	log.info(String.format("Successfully downloaded %s", metadata.getName()));
    	return file;
    }
    

    public void downloadAllUserFiles(String outputFolder) throws Exception {
    	File file = new File(outputFolder+File.separator+"file");
    	if(!file.exists())
    		file.createNewFile();
    	
    	OutputStream outputStream = new FileOutputStream(file);
    	FileMetadata metadata = client.files()
    	        .downloadBuilder("/Songs/Russian HardBass/Cheeki Breeki Hardbass Anthem (128kbit_AAC).m4a")
    	        .download(outputStream);
    	
    	log.info("Downlaoded " + metadata.getName());
    }

}

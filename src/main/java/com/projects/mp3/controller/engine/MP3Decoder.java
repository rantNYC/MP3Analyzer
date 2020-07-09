package com.projects.mp3.controller.engine;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.projects.mp3.model.MP3Info;

public class MP3Decoder {

	File mp3Path;

	public MP3Decoder(String mp3Path) {
		this.mp3Path = new File(mp3Path);
		if(!this.mp3Path.exists()) 
			throw new IllegalArgumentException(String.format("MP3File %s does not exists", mp3Path));

		if(!FilenameUtils.getExtension(mp3Path).equals("mp3")) 
			throw new IllegalArgumentException(String.format("File %s is not mp3", mp3Path));
	}

	public MP3Info decodeInformation() throws Exception {
		FileInputStream inputstream = null;
		try {
			Parser parser = new Mp3Parser();
			ContentHandler  handler = new DefaultHandler();
			Metadata metadata = new Metadata();   //empty metadata object 
			inputstream = new FileInputStream(mp3Path);
			ParseContext context = new ParseContext();
			parser.parse(inputstream, handler, metadata, context);

			return new MP3Info(metadata.get("title"), metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), 
							   metadata.get("xmpDM:genre"), metadata.get("xmpDM:audioSampleRate"), mp3Path.getAbsolutePath(),
							   metadata.get("xmpDM:duration"), mp3Path.length());
		} finally {
			if(inputstream != null) inputstream.close();
			EngineUtilities.printConsole("Finished decoding file: " + mp3Path.getAbsolutePath());
		}
	}
}

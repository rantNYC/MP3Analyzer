package com.projects.mp3.controller.engine.decoder;

import java.io.File;
import java.io.FileInputStream;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

import com.projects.mp3.controller.engine.EngineUtilities;
import com.projects.mp3.model.AudioInfo;

public class Decoder implements IDecoder{

	private static final Logger log = LoggerFactory.getLogger(Decoder.class);
	
	protected Parser parser;
	protected ContentHandler  handler;
	protected Metadata metadata; 
	protected ParseContext context;
	
	public Decoder() {
		parser = new  AutoDetectParser();
		handler = new BodyContentHandler();
		metadata = new Metadata();   //empty metadata object 
		context = new ParseContext();
//		this.fileToDecode = new File(fileToDecode);
//		if(!this.fileToDecode.exists()) 
//			throw new IllegalArgumentException(String.format("MP3File %s does not exists", fileToDecode));
	}
	
	@Override
	public boolean isAudioFile(File file) {
		try {
			String mediaType = new Tika().detect(new FileInputStream(file));
			if(mediaType.startsWith("audio") || mediaType.startsWith("video")
					|| mediaType.contains("ogg")) {
				return true;
			}
		} catch (Exception ex) {
			log.error(String.format("Error chcecking if %s is audio file", file), ex);
		}
		
		return false;
	}
	
	@Override
	public AudioInfo decodeInformation(File audioFile) throws Exception {
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(audioFile);
			parser.parse(inputstream, handler, metadata, context);
			double durationMinutes = Math.floor(Double.parseDouble(metadata.get("xmpDM:duration") == null ? "0" : metadata.get("xmpDM:duration"))/EngineUtilities.MILISECONDS_TO_MINUTE * 100) / 100;
			return new AudioInfo(metadata.get("title"), metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), 
							   metadata.get("xmpDM:genre"), metadata.get("xmpDM:audioSampleRate"), audioFile.getAbsolutePath(),
							   durationMinutes, audioFile.length()/EngineUtilities.BYTES_TO_MEGABYTES);
		} finally {
			if(inputstream != null) inputstream.close();
			log.info("Finished processing file: " + audioFile.getAbsolutePath());
		}
	}
	
}

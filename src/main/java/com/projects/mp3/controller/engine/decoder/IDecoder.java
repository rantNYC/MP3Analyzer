package com.projects.mp3.controller.engine.decoder;

import java.io.File;

import com.projects.mp3.model.AudioInfo;

public interface IDecoder {
	AudioInfo decodeInformation(File audioFile) throws Exception;
	boolean isAudioFile(File file);
	AudioInfo parseFileName(AudioInfo audioInfo);
}
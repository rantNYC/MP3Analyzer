package com.projects.mp3.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.projects.mp3.controller.engine.EngineUtilities;


public final class SynchronizedDataContainer {

	Map<String, Set<MP3Info>> data;
	
	public SynchronizedDataContainer() {
		// TODO Add concurrent map, <String, Set<MP3Info>> and method to modify 
		// them, this should be synchronized
		this.data = new ConcurrentHashMap<String, Set<MP3Info>>();
	}
	
	public boolean addConcurrentSet(String name, Set<MP3Info> set) {
		if(EngineUtilities.isNullorEmpty(name) || set == null) {
			return false;
		}
		if(data.containsKey(name)) {
			Set<MP3Info> dataInContainer = data.get(name);
			if(dataInContainer == null) data.put(name, Sets.newConcurrentHashSet(set));
			else dataInContainer.addAll(Sets.newConcurrentHashSet(set));
		}else {
			data.put(name, Sets.newConcurrentHashSet(set));
		}
		
		return true;
	}
	
	public boolean addDataToContainer(String name, MP3Info info) {
		if(EngineUtilities.isNullorEmpty(name) || info == null) {
			return false;
		}
		
		if(data.containsKey(name)) {
			Set<MP3Info> dataInContainer = data.get(name);
			if(dataInContainer == null) data.put(name, Sets.newConcurrentHashSet());
			return data.get(name).add(info);
		}else {
			Set<MP3Info> inData = new HashSet<MP3Info>();
			inData.add(info);
			data.put(name, Sets.newConcurrentHashSet(inData));
		}
		
		return true;
	}
	
	public Set<MP3Info> getDataSet(String name){
		if(EngineUtilities.isNullorEmpty(name)) return null;
		if(!data.containsKey(name)) {
			return null;
		}
		
		return ImmutableSet.copyOf(data.get(name));
	}
	
	public List<MP3Info> getDataList(String name){
		Set<MP3Info> data = getDataSet(name);
		return data == null ? null : ImmutableList.copyOf(data);
	}

	public void addEmptyConcurrentSet(String name) {
		if(EngineUtilities.isNullorEmpty(name)) return;
		if(!data.containsKey(name)) {
			data.put(name, Sets.newConcurrentHashSet());
		}	
	}

	public boolean containsDataInContainer(String name, MP3Info info) {
		if(data.containsKey(name)) {
			if(data.get(name) != null) {
				return data.get(name).contains(info);
			}
		}
		return false;
	}
}

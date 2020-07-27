package com.projects.mp3.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


public final class SynchronizedDataContainer {

	Map<ContainerType, Set<AudioInfo>> data;
	
	public SynchronizedDataContainer() {
		this.data = new ConcurrentHashMap<ContainerType, Set<AudioInfo>>();
		for(ContainerType type : ContainerType.values()) {
			data.put(type, Sets.newConcurrentHashSet());
		}
	}
	
	public boolean addConcurrentSet(ContainerType name, Set<AudioInfo> set) {
		if(name == null || set == null) {
			return false;
		}
		if(data.containsKey(name)) {
			Set<AudioInfo> dataInContainer = data.get(name);
			if(dataInContainer == null) data.put(name, Sets.newConcurrentHashSet(set));
			else dataInContainer.addAll(Sets.newConcurrentHashSet(set));
		}else {
			data.put(name, Sets.newConcurrentHashSet(set));
		}
		
		return true;
	}
	
	public boolean addDataToContainer(ContainerType name, AudioInfo info) {
		if(name == null || info == null) {
			return false;
		}
		
		if(data.containsKey(name)) {
			Set<AudioInfo> dataInContainer = data.get(name);
			if(dataInContainer == null) data.put(name, Sets.newConcurrentHashSet());
			return data.get(name).add(info);
		}else {
			Set<AudioInfo> inData = new HashSet<AudioInfo>();
			inData.add(info);
			data.put(name, Sets.newConcurrentHashSet(inData));
		}
		
		return true;
	}
	
	public Set<AudioInfo> getDataSet(ContainerType name){
		if(!data.containsKey(name)) {
			return null;
		}
		
		return ImmutableSet.copyOf(data.get(name));
	}
	
	public List<AudioInfo> getDataList(ContainerType name){
		Set<AudioInfo> data = getDataSet(name);
		return data == null ? null : ImmutableList.copyOf(data);
	}

	public void addEmptyConcurrentSet(ContainerType name) {
		if(!data.containsKey(name)) {
			data.put(name, Sets.newConcurrentHashSet());
		}	
	}

	public boolean containsDataInContainer(ContainerType name, AudioInfo info) {
		if(data.containsKey(name)) {
			if(data.get(name) != null) {
				return data.get(name).contains(info);
			}
		}
		return false;
	}
	
	public List<AudioInfo> getDifferencerRight(ContainerType leftType, ContainerType rightType){
		Set<AudioInfo> leftSet = data.get(leftType);
		Set<AudioInfo> rightSet = data.get(rightType);
		
		if(leftSet == null || leftSet.size() == 0) {
			return ImmutableList.copyOf(rightSet);
		}else {
			if(rightSet == null) return ImmutableList.copyOf(new ArrayList<AudioInfo>());
			List<AudioInfo> notInLeftSet = new ArrayList<AudioInfo>();
			for(AudioInfo info : rightSet) {
				if(!leftSet.contains(info)) {
					notInLeftSet.add(info);
				}
			}
			
			return ImmutableList.copyOf(notInLeftSet);
		}
	}

	public int getSizeContainer(ContainerType name) {
		if (data == null || !data.containsKey(name) || data.get(name) == null) return 0;
		else return data.get(name).size();
	}
}

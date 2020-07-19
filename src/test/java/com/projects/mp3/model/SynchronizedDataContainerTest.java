package com.projects.mp3.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projects.mp3.model.ContainerType;
import com.projects.mp3.model.MP3Info;
import com.projects.mp3.model.SynchronizedDataContainer;

class SynchronizedDataContainerTest {

	public SynchronizedDataContainer container;
	
	@BeforeEach
	void setUp() throws Exception {
		container = new SynchronizedDataContainer();
	}

	@Test
	void testSynchronizedDataContainer() {
		assertEquals(container.size(), ContainerType.values().length);
	}

	@Test
	void testAddConcurrentSet() {
		fail("Not yet implemented");
	}

	@Test
	void testAddDataToContainer() {
		fail("Not yet implemented");
	}

	@Test
	void testGetDataSet() {
		fail("Not yet implemented");
	}

	@Test
	void testGetDataList() {
		fail("Not yet implemented");
	}

	@Test
	void testAddEmptyConcurrentSet() {
		fail("Not yet implemented");
	}

	@Test
	void testContainsDataInContainer() {
		fail("Not yet implemented");
	}

	@Test
	void testSetDifferencerRight() {
		Set<MP3Info> leftSet = new HashSet<MP3Info>();
		leftSet.add(new MP3Info("1","1",null,null,null,null,0,0));
		leftSet.add(new MP3Info("2","1",null,null,null,null,0,0));
		leftSet.add(new MP3Info("3","1",null,null,null,null,0,0));
		Set<MP3Info> rightSet = new HashSet<MP3Info>();
		rightSet.add(new MP3Info("1","1",null,null,null,null,0,0));
		rightSet.add(new MP3Info("4","4",null,null,null,null,0,0));
		rightSet.add(new MP3Info("5","5",null,null,null,null,0,0));
		container.addConcurrentSet(ContainerType.DBContainer, leftSet);
		container.addConcurrentSet(ContainerType.FolderContainer, rightSet);
		List<MP3Info> result = container.setDifferencerRight(ContainerType.DBContainer, ContainerType.FolderContainer);
		
		assertEquals(result.size(), 2);
		assertTrue(result.contains(new MP3Info("4","4",null,null,null,null,0,0)));
		assertThrows(UnsupportedOperationException.class, () -> result.add(null), "Cannot modify Immuatable List");
		
		
	}

}

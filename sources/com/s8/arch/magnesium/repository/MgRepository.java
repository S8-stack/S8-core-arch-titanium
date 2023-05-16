package com.s8.arch.magnesium.repository;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.arch.magnesium.branch.MgBranchHandler;
import com.s8.arch.magnesium.handler.MgUnmountable;
import com.s8.arch.magnesium.store.MgStore;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;




public class MgRepository {
	
	/*
	public static MgRepository create(MgStore store) {
		
		MgRepository repository = new MgRepository();
		
		repository.branchHandlers = new HashMap<>();
		
		MgBranchHandler branchHandler = MgBranchHandler.create(store, "Default (prime) branch");
		repository.branchHandlers.put(branchHandler.id, branchHandler);
		
		return repository;
	}
	*/
	
	
	@JOOS_Type(name = "repository")
	public static class Serialized {
		
		@JOOS_Field(name = "address") 
		public String address;
		
		
		@JOOS_Field(name = "branches")
		public Map<String, MgBranchHandler.Serialized> branches;
		
		
		public MgRepository deserialize(SiliconEngine ng, MgStore store) {
			Path path = store.composeRepositoryPath(address);
			MgRepository repository = new MgRepository(address, path);
			branches.forEach((name, branch) -> {
				repository.branchHandlers.put(name, branch.deserialize(ng, store, repository));
			});
			
			return repository;
		}
	}

	
	public final String address;
	
	public final Path path;

	public final Map<String, MgBranchHandler> branchHandlers = new HashMap<>();
	
	
	
	/**
	 * 
	 */
	public MgRepository(String address, Path path) {
		super();
		this.address = address;
		this.path = path;
	}

	
	public void crawl(List<MgUnmountable> unmountables) {
		branchHandlers.forEach((k, branch) -> unmountables.add(branch));
	}


	public Path getPath() {
		return null;
	}
	
	
	public Serialized serialize() {
		Serialized serialized = new Serialized();
		
		// address
		serialized.address = address;
		
		// map
		HashMap<String, MgBranchHandler.Serialized> map = new HashMap<>();
		branchHandlers.forEach((key, branchHandler) -> {
			map.put(key, branchHandler.serialize());
		});
		serialized.branches = map;
		
		return serialized;
	}
	
}
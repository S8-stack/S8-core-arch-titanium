package com.s8.arch.magnesium.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.arch.magnesium.branch.MgBranchHandler;
import com.s8.arch.magnesium.handler.MgUnmountable;
import com.s8.arch.magnesium.store.MgStore;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;



@JOOS_Type(name = "repository")
public class MgRepository {
	
	public static MgRepository create(MgStore store) {
		
		MgRepository repository = new MgRepository();
		
		repository.branchHandlers = new HashMap<>();
		
		MgBranchHandler branchHandler = MgBranchHandler.create(store, "Default (prime) branch");
		repository.branchHandlers.put(branchHandler.id, branchHandler);
		
		return repository;
	}

	
	public @JOOS_Field(name = "name") String name;
	
	
	public @JOOS_Field(name = "branches") Map<String, MgBranchHandler> branchHandlers;
	
	
	/**
	 * 
	 */
	public MgRepository() {
		super();
	}

	
	public void crawl(List<MgUnmountable> unmountables) {
		branchHandlers.forEach((k, branch) -> unmountables.add(branch));
	}
	
}
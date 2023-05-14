package com.s8.arch.magnesium.repository;

import java.util.HashMap;
import java.util.Map;

import com.s8.arch.magnesium.branch.MgBranchHandler;
import com.s8.arch.magnesium.callbacks.VoidMgCallback;
import com.s8.arch.magnesium.store.MgStore;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;



@JOOS_Type(name = "repository")
public class MgRepository implements MgResource {
	
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



	
	@Override
	public void detach(VoidMgCallback onProbablyDetachable) {
		int n = branchHandlers.size();
		boolean[] isDetached = new boolean[n];
		Object innerLock = new Object();

		class IndexWrapper { public int value = 0; }
		IndexWrapper index = new IndexWrapper();
		
		
		branchHandlers.forEach((k, branch) -> {
			int i = index.value++;
			branch.detach(new VoidMgCallback() {
				
				@Override
				public void call() {
					synchronized (innerLock) {
						isDetached[i] = true;
						
						boolean isAllDetached = true;
						for(int j = 0; j<n; j++) {
							if(isDetached[j]) { isAllDetached = false; } 
						}
						
						if(isAllDetached) {
							onProbablyDetachable.call();
						}
					}
				}
			});
		});
	}
	


	@Override
	public boolean isDetachable() {
		class Wrapper { public boolean flag = true; }
		Wrapper wrapper = new Wrapper();
		branchHandlers.forEach((k, branch) -> {
			if(!branch.isDetachable()) { wrapper.flag = false; }
		});
		return wrapper.flag;
	}
	
}
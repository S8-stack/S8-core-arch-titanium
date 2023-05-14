package com.s8.arch.magnesium.store;

import java.util.ArrayDeque;
import java.util.Deque;

import com.s8.arch.magnesium.branch.CommitMgOperation;
import com.s8.arch.magnesium.branch.ThenMgOperation;
import com.s8.arch.magnesium.store.operations.MgOperation;
import com.s8.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class MgFlow extends S8Flow {
	
	
	/**
	 * 
	 */
	public final Deque<MgOperation> operations = new ArrayDeque<>();
	
	
	
	

	@Override
	public void then(MthProfile profile, S8Runnable runnable) {
		operations.addLast(new ThenMgOperation(this, profile, runnable));
	}
	

	@Override
	public void prior(MthProfile profile, S8Runnable runnable) {
		operations.addFirst(new ThenMgOperation(this, profile, runnable));
	}

	@Override
	public void commit(S8Runnable pre, S8Runnable post) {
		int nObjects = objects.length;
		Object[] exposure = new Object[nObjects];
		for(int i = 0; i < nObjects; i++) { 
			exposure[i] = objects[i];
		}
		operations.addLast(new CommitMgOperation(this, repository, branch, exposure) {

			@Override
			public void onSucceed(long version) {
				
			}

			@Override
			public void onFailed(String reason) {
				
			}
			
		});
	}

	@Override
	public void cloneHead(S8Runnable pre, S8Runnable post) {
		
	}

	@Override
	public void cloneVersion(S8Runnable pre, S8Runnable post) {
		
	}


}

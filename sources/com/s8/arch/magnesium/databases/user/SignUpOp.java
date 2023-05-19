package com.s8.arch.magnesium.databases.user;

import com.s8.arch.magnesium.callbacks.BooleanMgCallback;
import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;

public class SignUpOp extends UserH3MgOperation<BeBranch> {
	
	public final UserMgDatabase handler;
	
	public final String username;
	
	public final String password;
	
	public final BooleanMgCallback onProcessed;
	
	public final ExceptionMgCallback onFailed;

	public SignUpOp(long timeStamp, UserMgDatabase handler, String username, String password, BooleanMgCallback onProcessed, ExceptionMgCallback onFailed) {
		super(timeStamp);
		this.handler = handler;
		this.username = username;
		this.password = password;
		this.onProcessed = onProcessed;
		this.onFailed = onFailed;
	}

	@Override
	public ConsumeResourceMgTask<BeBranch> createConsumeResourceTask(BeBranch branch) {
		return new ConsumeResourceMgTask<BeBranch>(branch) {

			@Override
			public String describe() {
				return "login op";
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public H3MgHandler<BeBranch> getHandler() {
				return handler;
			}

			@Override
			public void consumeResource(BeBranch branch) {
				try {
					MgUser user =  (MgUser) branch.get(username);
					
					if(user == null) {
						
						MgUser signedUpUser = new MgUser(username);
						signedUpUser.password = password;
						
						
						
						
						onProcessed.call(true);
					}
					else {
						onProcessed.call(false);
					}
					
				} catch (BeIOException e) {
					e.printStackTrace();
					onFailed.call(e);
				}
			}
		};
	}

	@Override
	public CatchExceptionMgTask createCatchExceptionTask(Exception exception) {
		return new CatchExceptionMgTask(exception) {
			
			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}
			
			@Override
			public String describe() {
				return "catching exception";
			}
			
			@Override
			public void catchException(Exception exception) {
				onFailed.call(exception);	
			}
		};
	}

	@Override
	public boolean isModifyingResource() {
		return false;
	}

}

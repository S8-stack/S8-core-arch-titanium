package com.s8.arch.magnesium.userbase;

import com.s8.arch.magnesium.core.callbacks.BooleanMgCallback;
import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.handler.CatchExceptionMgTask;
import com.s8.arch.magnesium.core.handler.ConsumeResourceMgTask;
import com.s8.arch.magnesium.core.handler.MgHandler;
import com.s8.arch.magnesium.core.handler.UserMgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;

public class SignUpOp extends UserMgOperation<BeBranch> {
	
	public final MgUserbase handler;
	
	public final String username;
	
	public final String password;
	
	public final BooleanMgCallback onProcessed;
	
	public final ExceptionMgCallback onFailed;

	public SignUpOp(long timeStamp, MgUserbase handler, String username, String password, BooleanMgCallback onProcessed, ExceptionMgCallback onFailed) {
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
			public MgHandler<BeBranch> getHandler() {
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

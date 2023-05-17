package com.s8.arch.magnesium.userbase;

import com.s8.arch.magnesium.core.handler.MgIOModule;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.branch.BeInbound;
import com.s8.io.bohr.beryllium.branch.BeOutbound;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.io.bytes.linked.LinkedBytesIO;


/**
 * 
 * @author pierreconvert
 *
 */
public class IOModule implements MgIOModule<BeBranch> {

	
	public final MgUserbase handler;
	
	
	/**
	 * 
	 * @param handler
	 */
	public IOModule(MgUserbase handler) {
		super();
		this.handler = handler;
	}
	

	@Override
	public BeBranch load() throws Exception {
		
		/* read from disk */
		LinkedBytes head = LinkedBytesIO.read(handler.getPath(), true);

		/* build inflow */
		ByteInflow inflow = new LinkedByteInflow(head);

		/* build inbound session */
		BeInbound inbound = new BeInbound(handler.getCodebase());

		/* build branch */
		BeBranch branch = new BeBranch(handler.getCodebase());

		/* load branch */
		inbound.pullFrame(inflow, delta -> branch.pushDelta(delta));

		return branch;
	}

	
	
	
	@Override
	public void save(BeBranch branch) throws Exception {
		

		/* build inflow */
		LinkedByteOutflow outflow = new LinkedByteOutflow();

		/* build outbound session */
		BeOutbound outbound = new BeOutbound(handler.getCodebase());

		/* push branch */
		outbound.pushFrame(outflow, branch.pullDeltas());

		/* read from disk */
		LinkedBytesIO.write(outflow.getHead() ,handler.getPath(), true);
	}

}

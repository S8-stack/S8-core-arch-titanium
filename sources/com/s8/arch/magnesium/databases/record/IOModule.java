package com.s8.arch.magnesium.databases.record;

import java.io.IOException;

import com.s8.api.bytes.ByteInflow;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.branch.BeInbound;
import com.s8.io.bohr.beryllium.branch.BeOutbound;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.io.bytes.linked.LinkedBytesIO;


/**
 * 
 * @author pierreconvert
 *
 */
public class IOModule implements H3MgIOModule<BeBranch> {

	
	public final RecordsMgDatabase handler;
	
	
	/**
	 * 
	 * @param handler
	 */
	public IOModule(RecordsMgDatabase handler) {
		super();
		this.handler = handler;
	}
	

	@Override
	public BeBranch load() throws IOException {
		
		/* read from disk */
		LinkedBytes head = LinkedBytesIO.read(handler.getDataFilePath(), true);

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
	public void save(BeBranch branch) throws IOException {
		

		/* build inflow */
		LinkedByteOutflow outflow = new LinkedByteOutflow();

		/* build outbound session */
		BeOutbound outbound = new BeOutbound(handler.getCodebase());

		/* push branch */
		outbound.pushFrame(outflow, branch.pullDeltas());

		/* read from disk */
		LinkedBytesIO.write(outflow.getHead() ,handler.getDataFilePath(), true);
	}

}

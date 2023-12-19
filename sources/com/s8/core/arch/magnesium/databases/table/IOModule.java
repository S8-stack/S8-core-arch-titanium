package com.s8.core.arch.magnesium.databases.table;

import java.io.IOException;

import com.s8.api.bytes.ByteInflow;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.bohr.beryllium.branch.BeBranch;
import com.s8.core.bohr.beryllium.branch.BeInbound;
import com.s8.core.bohr.beryllium.branch.BeOutbound;
import com.s8.core.io.bytes.linked.LinkedByteInflow;
import com.s8.core.io.bytes.linked.LinkedByteOutflow;
import com.s8.core.io.bytes.linked.LinkedBytes;
import com.s8.core.io.bytes.linked.LinkedBytesIO;


/**
 * 
 * @author pierreconvert
 *
 */
public class IOModule implements H3MgIOModule<BeBranch> {

	
	public final TableMgDatabase handler;
	
	
	/**
	 * 
	 * @param handler
	 */
	public IOModule(TableMgDatabase handler) {
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

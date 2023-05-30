package com.s8.arch.magnesium.databases.repo.branch;

import java.io.IOException;

import com.s8.arch.magnesium.databases.repo.store.MgRepoStore;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.branch.endpoint.NdInbound;
import com.s8.io.bohr.neodymium.branch.endpoint.NdOutbound;
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
public class IOModule implements H3MgIOModule<NdBranch> {

	
	public final MgBranchHandler handler;
	
	
	/**
	 * 
	 * @param handler
	 */
	public IOModule(MgBranchHandler handler) {
		super();
		this.handler = handler;
	}
	

	@Override
	public NdBranch load() throws IOException {
		
		/* read from disk */
		LinkedBytes head = LinkedBytesIO.read(handler.getPath(), true);

		/* build inflow */
		ByteInflow inflow = new LinkedByteInflow(head);

		/* build inbound session */
		MgRepoStore store = handler.getStore();
		NdInbound inbound = new NdInbound(store.getCodebase());

		/* build branch */
		NdBranch branch = new NdBranch(store.getCodebase(), handler.getIdentifier());

		/* load branch */
		inbound.pullFrame(inflow, delta -> branch.appendDelta(delta));

		return branch;
	}

	
	@Override
	public void save(NdBranch branch) throws IOException {
		

		/* build inflow */
		LinkedByteOutflow outflow = new LinkedByteOutflow();

		/* build outbound session */
		MgRepoStore store = handler.getStore();
		NdOutbound outbound = new NdOutbound(store.getCodebase());

		/* push branch */
		outbound.pushFrame(outflow, branch.getSequence());

		/* read from disk */
		LinkedBytesIO.write(outflow.getHead() ,handler.getPath(), true);
	}

}

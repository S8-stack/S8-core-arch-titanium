package com.s8.arch.magnesium.branch;

import com.s8.arch.magnesium.handler.MgIOModule;
import com.s8.arch.magnesium.store.MgStore;
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
public class MgBranchIO implements MgIOModule<NdBranch> {

	
	public final MgBranchHandler handler;
	
	
	/**
	 * 
	 * @param handler
	 */
	public MgBranchIO(MgBranchHandler handler) {
		super();
		this.handler = handler;
	}
	

	@Override
	public NdBranch load() throws Exception {
		
		/* read from disk */
		LinkedBytes head = LinkedBytesIO.read(handler.getPath(), true);

		/* build inflow */
		ByteInflow inflow = new LinkedByteInflow(head);

		/* build inbound session */
		MgStore store = handler.getStore();
		NdInbound inbound = new NdInbound(store.getCodebase());

		/* build branch */
		NdBranch branch = new NdBranch(store.getCodebase(), handler.id);

		/* load branch */
		inbound.pullFrame(inflow, delta -> branch.appendDelta(delta));

		return branch;
	}

	
	@Override
	public void save(NdBranch branch) throws Exception {
		

		/* build inflow */
		LinkedByteOutflow outflow = new LinkedByteOutflow();

		/* build outbound session */
		MgStore store = handler.getStore();
		NdOutbound outbound = new NdOutbound(store.getCodebase());

		/* push branch */
		outbound.pushFrame(outflow, branch.getSequence());

		/* read from disk */
		LinkedBytesIO.write(outflow.getHead() ,handler.getPath(), true);
	}

}

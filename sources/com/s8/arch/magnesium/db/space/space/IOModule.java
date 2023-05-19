package com.s8.arch.magnesium.db.space.space;

import com.s8.arch.magnesium.db.space.store.MgS1Store;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.branches.LiInbound;
import com.s8.io.bohr.lithium.branches.LiOutbound;
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
public class IOModule implements H3MgIOModule<LiBranch> {


	public final MgSpaceHandler handler;


	/**
	 * 
	 * @param handler
	 */
	public IOModule(MgSpaceHandler handler) {
		super();
		this.handler = handler;
	}


	@Override
	public LiBranch load() throws Exception {

		/* read from disk */
		LinkedBytes head = LinkedBytesIO.read(handler.getPath(), true);

		/* build inflow */
		ByteInflow inflow = new LinkedByteInflow(head);

		/* build inbound session */
		MgS1Store store = handler.getStore();
		LiInbound inbound = new LiInbound(store.getCodebase());

		/* build branch */
		LiBranch branch = new LiBranch("m", store.getCodebase());

		/* load branch */
		inbound.pullFrame(inflow, branch);

		return branch;
	}


	@Override
	public void save(LiBranch branch) throws Exception {


		/* build inflow */
		LinkedByteOutflow outflow = new LinkedByteOutflow();

		/* build outbound session */
		MgS1Store store = handler.getStore();
		LiOutbound outbound = new LiOutbound(store.getCodebase());

		/* push branch */
		outbound.pushFrame(outflow, branch.pullDeltas());

		/* read from disk */
		LinkedBytesIO.write(outflow.getHead() ,handler.getPath(), true);
	}

}

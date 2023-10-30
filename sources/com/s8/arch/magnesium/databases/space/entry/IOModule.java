package com.s8.arch.magnesium.databases.space.entry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import com.s8.api.bytes.ByteInflow;
import com.s8.api.exceptions.S8BuildException;
import com.s8.api.exceptions.S8IOException;
import com.s8.arch.magnesium.databases.space.store.SpaceMgStore;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.io.bytes.linked.LinkedByteInflow;
import com.s8.core.io.bytes.linked.LinkedByteOutflow;
import com.s8.core.io.bytes.linked.LinkedBytes;
import com.s8.core.io.bytes.linked.LinkedBytesIO;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.branches.LiInbound;
import com.s8.io.bohr.lithium.branches.LiOutbound;


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
	public LiBranch load() throws IOException {

		SpaceMgStore store = handler.getStore();

		boolean isExisting = Files.exists(handler.getDataFilePath(), LinkOption.NOFOLLOW_LINKS);

		if(isExisting) {

			/* read from disk */
			LinkedBytes head = LinkedBytesIO.read(handler.getDataFilePath(), true);

			/* build inflow */
			ByteInflow inflow = new LinkedByteInflow(head);

			/* build inbound session */

			LiInbound inbound = new LiInbound(store.getCodebase());

			/* build branch */
			LiBranch branch = new LiBranch("m", store.getCodebase());

			/* load branch */
			inbound.pullFrame(inflow, branch);

			return branch;

		}
		else {
			LiBranch branch = new LiBranch("m", store.getCodebase());

			return branch;
		}


	}


	@Override
	public void save(LiBranch branch) throws IOException {

		/* commit changes */
		try {
			branch.commit();
		} catch (S8BuildException e) {
			e.printStackTrace();
			throw new S8IOException(e.getMessage());
		}

		/* build inflow */
		LinkedByteOutflow outflow = new LinkedByteOutflow();

		/* build outbound session */
		SpaceMgStore store = handler.getStore();
		LiOutbound outbound = new LiOutbound(store.getCodebase());

		/* push branch */
		outbound.pushFrame(outflow, branch.pullDeltas());

		/* read from disk */
		LinkedBytesIO.write(outflow.getHead(), handler.getDataFilePath(), true);
	}

}

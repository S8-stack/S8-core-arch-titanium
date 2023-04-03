package com.s8.stack.arch.tests.db.mappings.m1;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.io.bytes.linked.LinkedBytesIO;
import com.s8.stack.arch.magnesium.stores.m1.M1ModelPrototype;

public class PseudoRepo {

	
	
	public final static M1ModelPrototype<PseudoRepo> PROTOTYPE = new M1ModelPrototype<PseudoRepo>() {

		@Override
		public void save(Path path, PseudoRepo model) throws IOException {
			LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
			model.serialize(outflow);
			LinkedBytes head = outflow.getHead();
			LinkedBytesIO.write(head, path, false);
		}

		@Override
		public PseudoRepo load(Path path, String address) throws IOException {
			LinkedBytes head = LinkedBytesIO.read(path, false);
			LinkedByteInflow inflow = new LinkedByteInflow(head);
			PseudoRepo repo = new PseudoRepo();
			repo.deserialize(inflow);
			return repo;
		}

		@Override
		public long getBytecount(PseudoRepo model) {
			return model.value.length();
		}
	};
	
	public String value;
	
	public PseudoRepo() {
		super();
	}
	
	
	public PseudoRepo(String value) {
		super();
		this.value = value;
	}


	/**
	 * 
	 * @param outflow
	 * @throws IOException
	 */
	public void serialize(ByteOutflow outflow) throws IOException {
		outflow.putStringUTF8(value);
	}
	
	
	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public void deserialize(ByteInflow inflow) throws IOException {
		value = inflow.getStringUTF8();
	}
}

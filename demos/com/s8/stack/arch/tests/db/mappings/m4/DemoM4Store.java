package com.s8.stack.arch.tests.db.mappings.m4;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.arch.silicon.SiException;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.stack.arch.magnesium.stores.m4.LeafM4Node;
import com.s8.stack.arch.magnesium.stores.m4.M4Store;

public class DemoM4Store extends M4Store<DemoStock, DemoFlow>{
	
	
	public static class DemoLeaf extends LeafM4Node<DemoStock, DemoFlow> {

		public DemoLeaf(long id, Path path, long index0) {
			super(id, path, index0);
		}

		@Override
		public DemoFlow[] initializeBody(int dimension) {
			return new DemoFlow[dimension];
		}

		@Override
		public Body<DemoStock, DemoFlow> deserializeBody(ByteInflow inflow, int position, int capacity)
				throws IOException {
			DemoStock stock = new DemoStock(inflow.getInt64());
			DemoFlow[] flows = new DemoFlow[capacity];
			for(int i=0; i<position; i++) {
				flows[i] = new DemoFlow(inflow.getInt64());
			}
			return new Body<DemoStock, DemoFlow>(stock, flows);
		}

		@Override
		public void serializeBody(DemoStock stock, DemoFlow[] flows, int position, ByteOutflow outflow)
				throws IOException {
			outflow.putInt64(stock.value);
			for(int i=0; i<position; i++) {
				outflow.putInt64(flows[i].delta);
			}
		}	
	}


	public DemoM4Store(Path root, int dimension, boolean isVerbose) {
		super(root, dimension, isVerbose);
	}

	
	

	@Override
	public LeafM4Node<DemoStock, DemoFlow> createLeaf(long id, Path path, long index0) {
		return new DemoLeaf(id, path, index0);
	}

	@Override
	public void operate(DemoStock stock, DemoFlow flow) {
		stock.value += flow.delta;
	}

	@Override
	public DemoStock copy(DemoStock stock) {
		return new DemoStock(stock.value);
	}

	@Override
	public void initialize(long index0, DemoStock stock) throws SiException, IOException {
		initializeStore(index0, stock);
	}

	@Override
	public void deserialize(ByteInflow inflow) throws IOException {
		deserializeStore(inflow);
	}

	@Override
	public DemoStock deserializeStock(ByteInflow inflow) throws IOException {
		return new DemoStock(inflow.getInt64());
	}

	@Override
	public void serialize(ByteOutflow outflow) throws IOException {
		serializeStore(outflow);
	}

	@Override
	public void serializeStock(DemoStock stock, ByteOutflow outflow) throws IOException {
		outflow.putInt64(stock.value);
	}
	
}

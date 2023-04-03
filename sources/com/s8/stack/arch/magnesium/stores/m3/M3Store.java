package com.s8.stack.arch.magnesium.stores.m3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.io.bytes.linked.LinkedBytesIO;
import com.s8.stack.arch.magnesium.stores.m3.nodes.ForkM3Node;
import com.s8.stack.arch.magnesium.stores.m3.requests.PutM3Request;

/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public abstract class M3Store<T> {


	public final static String FILENAME = "store.m3";

	public static boolean DEBUG_isVerbose;

	public final PathComposer pathComposer;

	private int nbits;
	private int threshold;
	private int capacity;
	private int mask;

	ForkM3Node<T> head;

	private long idGenerator;


	private boolean isLoaded = false;

	private ActiveList<T> activeList;

	private Path path;




	/**
	 * 
	 * @return
	 */
	public abstract M3ModelPrototype<T> getPrototype();


	/**
	 * 
	 * @param root
	 */
	public M3Store(Path root) {
		super();
		path = root.resolve(FILENAME);
		pathComposer = new PathComposer(root);
	}
	
	/**
	 * 
	 * @param root
	 * @return
	 */
	public static boolean isExisting(Path root) {
		return root.resolve(FILENAME).toFile().exists();
	}



	/**
	 * 
	 * @param currentNodeId
	 * @param nbits
	 * @param threshold
	 * @param maxNbLoaded
	 */
	private void setup(M3Config config, long currentNodeId) {
		this.idGenerator = currentNodeId;
		this.nbits = config.nbits;
		this.threshold = config.threshold;
		capacity = (int) HashcodeModule.powerOf2(nbits);
		mask = HashcodeModule.generateMask(nbits);
		this.activeList = new ActiveList<>(this, config.maxNbLoaded, config.saveFrequency);
	}


	public long createNewId() {
		return idGenerator++;
	}



	public void put(String key, T value, boolean isTransactional) throws IOException {
		new PutM3Request<T>(this, key, value, isTransactional).serve();
	}
	
	

	public ActiveList<T>.NodeHandler record(ForkM3Node<T> forkNode) {
		return activeList.record(forkNode);
	}


	public void rollOver() throws IOException {
		activeList.rollOver();
	}

	public void dim() throws IOException {
		activeList.dim();
	}

	public void persist() throws IOException {
		activeList.persist();
	}

	public ForkM3Node<T> getHead(){
		return head;
	}







	/**
	 * 
	 * @param nbits
	 * @param threshold
	 * @param nLoaded
	 * @throws IOException
	 */
	public void boot(M3Config config) {
		setup(config, 0x02L);

		long id = createNewId();
		Path path = pathComposer.compose(id);
		head = new ForkM3Node<T>(id, path, capacity, 0, mask);
		head.init(this);
		isLoaded = true;
	}



	/**
	 * 
	 * @param store
	 * @throws IOException
	 */
	public void load() throws IOException {
		if(!isLoaded) {
			try {
				LinkedBytes head = LinkedBytesIO.read(path, false);
				LinkedByteInflow inflow = new LinkedByteInflow(head);
				deserialize(inflow);	
			}
			catch(IOException exception) {
				exception.printStackTrace();
			}
			isLoaded = true;
		}
	}



	/**
	 * 
	 * @param store
	 * @throws IOException
	 */
	public void save() throws IOException {
		if(isLoaded) {
			activeList.saveNodes();

			LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
			serialize(outflow);
			LinkedBytes head = outflow.getHead();
			LinkedBytesIO.write(head, path, false);
		}
	}



	public final static byte[] OPENING_TAG = "<M3Store:>".getBytes(StandardCharsets.US_ASCII);

	public final static byte[] CLOSING_TAG = "</M3Store>".getBytes(StandardCharsets.US_ASCII);



	/**
	 * <p>MUST call <code>serializeStore(ByteOutflow outflow)</code>.</p>
	 * @param outflow
	 * @throws IOException
	 */
	public abstract void serialize(ByteOutflow outflow) throws IOException;



	/**
	 * 
	 * @param store
	 * @param outflow
	 * @throws IOException
	 */
	public void serializeStore(ByteOutflow outflow) throws IOException {


		outflow.putByteArray(OPENING_TAG);

		// configuration
		getConfig().serialize(outflow);

		// id state
		outflow.putInt64(idGenerator);

		head.serializeHeader(outflow);

		outflow.putByteArray(CLOSING_TAG);	
	}



	/**
	 * p>MUST call <code>deserializeStore()</code>.</p>
	 * @param inflow
	 * @throws IOException
	 */
	public abstract void deserialize(ByteInflow inflow) throws IOException;


	/**
	 * 
	 * @param store
	 * @param inflow
	 * @throws IOException
	 */
	public void deserializeStore(ByteInflow inflow) throws IOException {

		if(!inflow.matches(OPENING_TAG)) {
			throw new IOException("Failed to match opening tag");
		}
		/* <params> */
		M3Config config = M3Config.deserialize(inflow);		
		long idGen = inflow.getInt64();
		/* <params> */

		setup(config, idGen);

		head = ForkM3Node.deserializeHeader(this, 0, inflow);

		if(!inflow.matches(CLOSING_TAG)) {
			throw new IOException("Failed to match opening tag");
		}
	}


	/**
	 * 
	 * @return
	 */
	public int getMask() {
		return mask;
	}


	/**
	 * 
	 * @return
	 */
	public int getNBits() {
		return nbits;
	}


	/**
	 * 
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}


	/**
	 * 
	 * @return
	 */
	public int getThreshold() {
		return threshold;
	}




	/**
	 * 
	 * @return
	 */
	public M3Config getConfig() {
		return new M3Config(nbits, 
				threshold, 
				activeList.getMaxNbLoaded(), 
				activeList.getSaveFrequency());
	}

}

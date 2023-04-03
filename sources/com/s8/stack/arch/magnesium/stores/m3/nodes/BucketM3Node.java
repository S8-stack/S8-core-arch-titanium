package com.s8.stack.arch.magnesium.stores.m3.nodes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.stack.arch.magnesium.stores.m3.M3ModelPrototype;
import com.s8.stack.arch.magnesium.stores.m3.M3Store;
import com.s8.stack.arch.magnesium.stores.m3.requests.Action;
import com.s8.stack.arch.magnesium.stores.m3.requests.Query;


/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public class BucketM3Node<T> implements M3Node<T> {

	public @Override M3Node.Kind getKind() { return Kind.BUCKET; }

	private LinkM3Node<T> head;

	private int size;




	/**
	 * 	
	 * @param index
	 */
	public BucketM3Node() {
		super();
		this.size = 0;
		this.head = null;
	}


	public boolean isEmpty() {
		return head == null;
	}


	public int size() {
		return size;
	}


	/**
	 * 
	 * @param query
	 * @throws IOException
	 */
	public void descend(Query<T> query) throws IOException {

		//M3Store<T> store = query.store;
		Action action = query.action;

		boolean isFound = false;

		LinkM3Node<T> link = head, previous = null;
		while(!isFound && link!=null) {
			if(link.key.equals(query.key)) {
				switch(action) {
				case RETRIEVE:
				case APPEND:
					query.setLinkFollowUp(link);
					isFound = true;
					break;

				case REMOVE:
					if(previous!=null) { // link is not head
						previous.next = link.next;
					}
					else {
						head = link.next;
					}
					query.getLastFork().notifyChange();
					size--;
					query.setLinkFollowUp(link);
					isFound = true;
					break;
				}
			}
			else {
				previous = link;
				link = link.next;
			}
		}

		/* no matching entry found */
		if(!isFound) {
			switch(query.action) {
			case APPEND:
				LinkM3Node<T> appendedLink = new LinkM3Node<T>(query.key);
				appendedLink.next = head;
				head = appendedLink;
				query.getLastFork().notifyChange();
				
				size++;
				query.setLinkFollowUp(appendedLink);
				break;

			case REMOVE:
			case RETRIEVE:
			default:
				query.setLinkFollowUp(null);
				break;
			}	
		}

	}

	
	
	public void traverse(BiConsumer<String, T> consumer) {
		LinkM3Node<T> link = head;
		while(link!=null) {
			consumer.accept(link.key, link.value);
			link = link.next;
		}
	}



	/**
	 * 
	 * @param store
	 * @throws IOException 
	 */
	public ForkM3Node<T> explode(M3Store<T> store, int parentShift) throws IOException {
		// create fork node
		long id = store.createNewId();
		Path path = store.pathComposer.compose(id);
		ForkM3Node<T> fork = new ForkM3Node<T>(id, path, 
				store.getCapacity(), 
				parentShift + store.getNBits(), 
				store.getMask());

		fork.init(store);

		LinkM3Node<T> link = head;
		while(link!=null) {

			new Query<T>(store, Action.APPEND, link.key, link.hashcode, fork).lookUp().value = link.value;

			// move next
			link = link.next;
		}

		return fork;
	}


	public static final int HAS_LINK_NEXT = 0x36;

	public static final int NO_MORE_LINK = 0x53;


	/**
	 * 
	 * @param prototype
	 * @param outflow
	 * @throws IOException
	 */
	public void serialize(M3ModelPrototype<T> prototype, ByteOutflow outflow) throws IOException {
		LinkM3Node<T> node = head;
		while(node!=null) {
			outflow.putUInt8(HAS_LINK_NEXT);
			node.serialize(prototype, outflow);
			node = node.next;
		}
		outflow.putUInt8(NO_MORE_LINK);
	}


	public void deserialize(M3ModelPrototype<T> prototype, ByteInflow inflow) throws IOException {
		while(inflow.getUInt8() != NO_MORE_LINK){
			LinkM3Node<T> node = LinkM3Node.deserialize(prototype, inflow);
			node.next = head;
			head = node;
		}
	}

}

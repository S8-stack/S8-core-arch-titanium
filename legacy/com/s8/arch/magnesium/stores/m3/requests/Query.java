package com.s8.arch.magnesium.stores.m3.requests;

import java.io.IOException;

import com.s8.arch.magnesium.stores.m3.M3Store;
import com.s8.arch.magnesium.stores.m3.nodes.BucketM3Node;
import com.s8.arch.magnesium.stores.m3.nodes.ForkM3Node;
import com.s8.arch.magnesium.stores.m3.nodes.LinkM3Node;
import com.s8.arch.magnesium.stores.m3.nodes.M3Node;
import com.s8.arch.magnesium.stores.m3.nodes.M3Node.Kind;


/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public class Query<T> {

	public final M3Store<T> store;

	public final Action action;

	public final String key;

	public final long hashcode;

	private ForkM3Node<T> lastFork;

	private BucketM3Node<T> bucket;

	private LinkM3Node<T> link;

	private M3Node.Kind status;

	
	//private List<ForkM3Node<T>> DEBUG_steps = new ArrayList<ForkM3Node<T>>();

	/**
	 * 
	 * @param store
	 * @param action
	 * @param key
	 * @param hashcode
	 * @param lastFork
	 */
	public Query(M3Store<T> store, Action action, String key, long hashcode, ForkM3Node<T> lastFork) {
		super();
		this.store = store;
		this.action = action;
		this.key = key;
		this.hashcode = hashcode;

		this.lastFork = lastFork;
		status = Kind.FORK;
		//DEBUG_steps.add(lastFork);
	}



	/**
	 * 
	 * @param fork
	 */
	public void setForkFollowUp(ForkM3Node<T> fork) {
		
		this.lastFork = fork;
		this.status = Kind.FORK;
		//DEBUG_steps.add(fork);	
	}


	/**
	 * 
	 * @param bucket
	 */
	public void setBucketFollowUp(BucketM3Node<T> bucket) {
		this.bucket = bucket;
		this.status = Kind.BUCKET;
	}


	/**
	 * 
	 * @param link
	 */
	public void setLinkFollowUp(LinkM3Node<T> link) {
		this.link = link;
		this.status = Kind.LINK;

		if(link != null) {
			this.link.hashcode = hashcode;
		}

	}



	/**
	 * 
	 * @param store
	 * @param key
	 * @param hashcode
	 * @param action
	 * @return
	 * @throws IOException
	 */


	public LinkM3Node<T> lookUp() throws IOException{

		boolean isCompleted = false;
		while(!isCompleted) {
			switch(status) {
			case FORK:
				lastFork.descend(this);
				break;

			case BUCKET:
				bucket.descend(this);
				break;

			case LINK:
				isCompleted = true;
				break;
			}
		}

		return link;
	}



	public void setFollowUp(M3Node<T> node) {
		switch(node.getKind()) {

		case FORK:
			this.lastFork = (ForkM3Node<T>) node;
			//DEBUG_steps.add(lastFork);
			this.status = Kind.FORK;
			break;

		case BUCKET:
			this.bucket = (BucketM3Node<T>) node;
			this.status = Kind.BUCKET;
			break;

		case LINK:
			this.link = (LinkM3Node<T>) node;
			this.status = Kind.LINK;
			break;
		}
	}



	public M3Store<T> getStore() {
		return store;
	}



	public Action getAction() {
		return action;
	}


	/**
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}


	/**
	 * 
	 * @return
	 */
	public long getHashcode() {
		return hashcode;
	}


	/**
	 * 
	 * @return
	 */
	public ForkM3Node<T> getLastFork() {
		return lastFork;
	}


	/**
	 * 
	 * @return
	 */
	public BucketM3Node<T> getBucketFollowUp() {
		return bucket;
	}


	/**
	 * 
	 * @return
	 */
	public LinkM3Node<T> getLinkFollowUp() {
		return link;
	}


	/**
	 * 
	 * @return
	 */
	public M3Node.Kind getStatus() {
		return status;
	}

}

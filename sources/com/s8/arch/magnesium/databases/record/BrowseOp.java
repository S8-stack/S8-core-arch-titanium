package com.s8.arch.magnesium.databases.record;

import java.util.List;

import com.s8.arch.fluor.S8Filter;
import com.s8.arch.fluor.outputs.ObjectsListS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;


/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public class BrowseOp<T> extends RequestDbMgOperation<BeBranch> {



	/**
	 * handler
	 */
	public final RecordsMgDatabase dbHandler;


	/**
	 * 
	 */
	public final S8Filter<T> filter;


	/**
	 * on selected
	 */
	public final MgCallback<ObjectsListS8AsyncOutput<T>> onSelected;



	public BrowseOp(long timeStamp, RecordsMgDatabase dbHandler, 
			S8Filter<T> filter,
			MgCallback<ObjectsListS8AsyncOutput<T>> onSelected, long options) {
		super(timeStamp, null, options);
		this.dbHandler = dbHandler;
		this.filter = filter;
		this.onSelected = onSelected;
	}


	@Override
	public H3MgHandler<BeBranch> getHandler() {
		return dbHandler;
	}

	@Override
	public ConsumeResourceMgAsyncTask<BeBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<BeBranch>(dbHandler) {

			@Override
			public String describe() {
				return "login op";
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public boolean consumeResource(BeBranch branch) throws BeIOException {
				ObjectsListS8AsyncOutput<T> output = new ObjectsListS8AsyncOutput<T>();

				List<T> objects = branch.select(filter);
				output.users = objects;
				output.isSuccessful = true;

				onSelected.call(output);
				return false; // no resources modified
			}

			@Override
			public void catchException(Exception exception) {
				exception.printStackTrace();
				ObjectsListS8AsyncOutput<T> output = new ObjectsListS8AsyncOutput<T>();
				output.reportException(exception);
				onSelected.call(output);
			}
		};
	}

}

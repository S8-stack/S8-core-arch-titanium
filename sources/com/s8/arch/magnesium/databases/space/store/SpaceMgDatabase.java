package com.s8.arch.magnesium.databases.space.store;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.SpaceExposureS8AsyncOutput;
import com.s8.api.flow.outputs.SpaceVersionS8AsyncOutput;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.io.joos.JOOS_Lexicon;
import com.s8.core.io.joos.types.JOOS_CompilingException;
import com.s8.core.io.joos.utilities.JOOS_BufferedFileWriter;
import com.s8.io.bohr.lithium.codebase.LiCodebase;


/**
 * 
 * @author pc
 *
 */
public class SpaceMgDatabase extends H3MgHandler<SpaceMgStore> {

	
	public final static String METADATA_FILENAME = "store-meta.js";
	
	
	public final LiCodebase codebase;
	
	public final Path rootFolderPath;
	
	public final IOModule ioModule;
	
	/**
	 * 
	 * @param ng
	 * @param codebase
	 * @param storeInfoPathname
	 * @param initializer
	 * @throws JOOS_CompilingException
	 */
	public SpaceMgDatabase(SiliconEngine ng, 
			LiCodebase codebase, 
			Path rootFolderPath) throws JOOS_CompilingException {
		super(ng);
		this.codebase = codebase;
		this.rootFolderPath = rootFolderPath;
		this.ioModule = new IOModule(this);
	}
	

	@Override
	public String getName() {
		return "store";
	}

	@Override
	public H3MgIOModule<SpaceMgStore> getIOModule() {
		return ioModule;
	}

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		SpaceMgStore store = getResource();
		if(store != null) { 
			return store.getSpaceHandlers(); 
		}
		else {
			return new ArrayList<>();
		}
	}

	
	public Path getFolderPath() {
		return rootFolderPath;
	}
	
	
	public Path getMetadataFilePath() {
		return rootFolderPath.resolve(METADATA_FILENAME);
	}

	
	

	/**
	 * 
	 * @param t
	 * @param spaceId
	 * @param onProceed
	 * @param onFailed
	 */
	public void createSpace(long t, S8User initiator, String spaceId, SpaceS8Object[] exposure,
			MgCallback<SpaceExposureS8AsyncOutput> onProceed, long options) {
		pushOpLast(new CreateSpaceOp(t, initiator, this, spaceId, exposure, onProceed, options));
	}
	
	
	
	/**
	 * 
	 * @param t
	 * @param spaceId
	 * @param onProceed
	 * @param onFailed
	 */
	public void accessSpace(long t, S8User initiator, String spaceId, MgCallback<SpaceExposureS8AsyncOutput> onProceed, long options) {
		pushOpLast(new AccessSpaceOp(t, initiator, this, spaceId, onProceed, options));
	}

	

	/**
	 * 
	 * @param t
	 * @param spaceId
	 * @param onProceed
	 * @param onFailed
	 */
	public void exposeObjects(long t, S8User initiator, String spaceId, Object[] objects, MgCallback<SpaceVersionS8AsyncOutput> onProceed, long options) {
		pushOpLast(new ExposeObjectsOp(t, initiator, this, spaceId, objects, onProceed, options));
	}

	
	
	public static void init(String rootFolderPathname) throws IOException, JOOS_CompilingException {
		
		SpaceMgStoreMetadata metadata = new SpaceMgStoreMetadata();
		metadata.rootFolderPathname = rootFolderPathname;
		
		Path rootFolderPath = Path.of(rootFolderPathname);
		Path metadataFilePath = rootFolderPath.resolve(METADATA_FILENAME);
		
		Files.createDirectories(rootFolderPath);
		FileChannel channel = FileChannel.open(metadataFilePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		JOOS_Lexicon lexicon = JOOS_Lexicon.from(SpaceMgStoreMetadata.class);
		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);
		
		lexicon.compose(writer, metadata, "   ", false);
		writer.close();
	}
	
}

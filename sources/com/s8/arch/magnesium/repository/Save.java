package com.s8.arch.magnesium.repository;

import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import com.s8.arch.magnesium.shared.MgSharedResourceHandler;
import com.s8.arch.magnesium.shared.SaveMgTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.joos.JOOS_Lexicon;
import com.s8.io.joos.utilities.JOOS_BufferedFileWriter;

/**
 * 
 * @author pierreconvert
 *
 */
class Save extends SaveMgTask<MgRepository> {


	public final MgRepositoryHandler handler;

	/**
	 * 
	 * @param handler
	 */
	public Save(MgRepositoryHandler handler, MgRepository repo) {
		super(repo);
		this.handler = handler;
	}


	@Override
	public MgSharedResourceHandler<MgRepository> getHandler() {
		return handler;
	}


	@Override
	public void save(MgRepository repo) throws Exception {

		FileChannel channel = FileChannel.open(handler.getPath(), new OpenOption[]{ 
				StandardOpenOption.READ
		});

		JOOS_Lexicon lexicon = MgRepositoryHandler.JOOS_getLexicon();
		
		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, repo, "   ", false);

		writer.close();
	}


	@Override
	public String describe() {
		return "Write MgRepo def to disk";
	}


	@Override
	public MthProfile profile() {
		return MthProfile.IO_SSD;
	}

}

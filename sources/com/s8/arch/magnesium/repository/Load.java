package com.s8.arch.magnesium.repository;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import com.s8.arch.magnesium.shared.LoadMgTask;
import com.s8.arch.magnesium.shared.MgSharedResourceHandler;
import com.s8.io.joos.JOOS_Lexicon;
import com.s8.io.joos.parsing.JOOS_ParsingException;
import com.s8.io.joos.types.JOOS_CompilingException;
import com.s8.io.joos.utilities.JOOS_BufferedFileReader;

/**
 * 
 * @author pierreconvert
 *
 */
class Load extends LoadMgTask<MgRepository> {


	public final MgRepositoryHandler handler;



	/**
	 * 
	 * @param handler
	 */
	public Load(MgRepositoryHandler handler) {
		super();
		this.handler = handler;
	}


	@Override
	public MgSharedResourceHandler<MgRepository> getHandler() {
		return handler;
	}


	@Override
	public MgRepository load() throws IOException, JOOS_ParsingException, JOOS_CompilingException {

		FileChannel channel = FileChannel.open(handler.getPath(), new OpenOption[]{ 
				StandardOpenOption.READ
		});

		JOOS_Lexicon lexicon = MgRepositoryHandler.JOOS_getLexicon();
		
		JOOS_BufferedFileReader reader = new JOOS_BufferedFileReader(channel, StandardCharsets.UTF_8, 64);
		
		MgRepository repo = (MgRepository) lexicon.parse(reader, true);

		reader.close();

		return repo;

	}

}

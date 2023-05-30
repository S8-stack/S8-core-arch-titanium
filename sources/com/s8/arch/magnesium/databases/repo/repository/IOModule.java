package com.s8.arch.magnesium.databases.repo.repository;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import com.s8.arch.magnesium.databases.repo.branch.MgBranchHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.io.joos.JOOS_Lexicon;
import com.s8.io.joos.composing.JOOS_ComposingException;
import com.s8.io.joos.parsing.JOOS_ParsingException;
import com.s8.io.joos.types.JOOS_CompilingException;
import com.s8.io.joos.utilities.JOOS_BufferedFileReader;
import com.s8.io.joos.utilities.JOOS_BufferedFileWriter;

public class IOModule implements H3MgIOModule<MgRepository> {

	private static JOOS_Lexicon lexicon;
	

	
	public final MgRepositoryHandler handler;
	
	
	public IOModule(MgRepositoryHandler handler) throws JOOS_CompilingException {
		super();
		this.handler = handler;
		
		if(lexicon == null) { 
			lexicon = JOOS_Lexicon.from(MgRepository.Serialized.class, MgBranchHandler.Serialized.class); 
		}
	}


	@Override
	public MgRepository load() throws IOException, JOOS_ParsingException {

		FileChannel channel = FileChannel.open(handler.getPath(), new OpenOption[]{ 
				StandardOpenOption.READ
		});

		/**
		 * lexicon
		 */
		
		JOOS_BufferedFileReader reader = new JOOS_BufferedFileReader(channel, StandardCharsets.UTF_8, 64);
		
		MgRepository.Serialized repo = (MgRepository.Serialized) lexicon.parse(reader, true);

		reader.close();

		return repo.deserialize(handler.ng, handler.store);
	}
	
	

	@Override
	public void save(MgRepository repo) throws IOException, JOOS_ComposingException {

		FileChannel channel = FileChannel.open(handler.getPath(), new OpenOption[]{ 
				StandardOpenOption.WRITE
		});

		
		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, repo.serialize(), "   ", false);

		writer.close();
	}
}

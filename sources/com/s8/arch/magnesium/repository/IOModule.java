package com.s8.arch.magnesium.repository;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import com.s8.arch.magnesium.shared.MgIOModule;
import com.s8.io.joos.JOOS_Lexicon;
import com.s8.io.joos.parsing.JOOS_ParsingException;
import com.s8.io.joos.types.JOOS_CompilingException;
import com.s8.io.joos.utilities.JOOS_BufferedFileReader;
import com.s8.io.joos.utilities.JOOS_BufferedFileWriter;

public class IOModule implements MgIOModule<MgRepository> {

	private static JOOS_Lexicon lexicon;
	
	
	public static JOOS_Lexicon JOOS_getLexicon() throws JOOS_CompilingException {
		if(lexicon == null) { lexicon = JOOS_Lexicon.from(MgRepository.class); }
		return lexicon;
	}

	
	public final MgRepositoryHandler handler;
	
	
	public IOModule(MgRepositoryHandler handler) {
		super();
		this.handler = handler;
	}


	@Override
	public MgRepository load() throws IOException, JOOS_ParsingException, JOOS_CompilingException {

		FileChannel channel = FileChannel.open(handler.getPath(), new OpenOption[]{ 
				StandardOpenOption.READ
		});

		JOOS_Lexicon lexicon = JOOS_getLexicon();
		
		JOOS_BufferedFileReader reader = new JOOS_BufferedFileReader(channel, StandardCharsets.UTF_8, 64);
		
		MgRepository repo = (MgRepository) lexicon.parse(reader, true);

		reader.close();

		return repo;
	}
	
	

	@Override
	public void save(MgRepository repo) throws Exception {

		FileChannel channel = FileChannel.open(handler.getPath(), new OpenOption[]{ 
				StandardOpenOption.READ
		});

		JOOS_Lexicon lexicon = JOOS_getLexicon();
		
		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, repo, "   ", false);

		writer.close();
	}
}

package com.s8.arch.magnesium.databases.space.store;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.io.joos.JOOS_Lexicon;
import com.s8.io.joos.parsing.JOOS_ParsingException;
import com.s8.io.joos.types.JOOS_CompilingException;
import com.s8.io.joos.utilities.JOOS_BufferedFileReader;
import com.s8.io.joos.utilities.JOOS_BufferedFileWriter;

public class IOModule implements H3MgIOModule<SpaceMgStore> {

	private static JOOS_Lexicon lexicon;
	
	
	public static JOOS_Lexicon JOOS_getLexicon() throws JOOS_CompilingException {
	
		return lexicon;
	}

	
	public final SpaceMgDatabase handler;
	
	
	public IOModule(SpaceMgDatabase handler) throws JOOS_CompilingException {
		super();
		this.handler = handler;
		
		if(lexicon == null) { 
			lexicon = JOOS_Lexicon.from(SpaceMgStore.Serialized.class); 
		}
	}


	@Override
	public SpaceMgStore load() throws IOException, JOOS_ParsingException {

		FileChannel channel = FileChannel.open(handler.getInfoPath(), new OpenOption[]{ 
				StandardOpenOption.READ
		});

		/**
		 * lexicon
		 */
		
		JOOS_BufferedFileReader reader = new JOOS_BufferedFileReader(channel, StandardCharsets.UTF_8, 64);
		
		SpaceMgStore.Serialized repo = (SpaceMgStore.Serialized) lexicon.parse(reader, true);

		reader.close();

		return repo.deserialize(handler, handler.codebase);
	}
	
	

	@Override
	public void save(SpaceMgStore repo) throws IOException {

		FileChannel channel = FileChannel.open(handler.getInfoPath(), new OpenOption[]{ 
				StandardOpenOption.WRITE
		});
		
		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, repo.serialize(), "   ", false);

		writer.close();
	}
}

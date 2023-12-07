package com.s8.arch.magnesium.demos.repo;

import java.io.IOException;

import com.s8.core.io.json.composing.JSON_ComposingException;
import com.s8.core.io.json.types.JSON_CompilingException;

public class RepoDemo00 {

	public static void main(String[] args) throws JSON_CompilingException, IOException, JSON_ComposingException {

		/*
		
		JOOS_Lexicon lexicon = JOOS_Lexicon.from(MgRepositoryHandler.class);
		
		MgRepositoryHandler repositoryHandler = new MgRepositoryHandler();
		repositoryHandler.initialize();
		
		
		
		String pathname = "output/repo.joos";
		RandomAccessFile file = new RandomAccessFile(new File(pathname), "rws");

		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(file.getChannel(), StandardCharsets.UTF_8, 64);

		lexicon.compose(writer, repositoryHandler, "\t", true);

		writer.close();
		file.close();
		*/
	}

}

package com.s8.core.arch.magnesium.demos.db;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.s8.core.arch.magnesium.demos.db.resource.MainStubObject;
import com.s8.core.arch.titanium.db.TiIOException;
import com.s8.core.arch.titanium.db.TitaniumIOModule;
import com.s8.core.arch.titanium.db.TiResourceStatus;
import com.s8.core.io.json.JSON_Lexicon;
import com.s8.core.io.json.types.JSON_CompilingException;
import com.s8.core.io.json.utilities.JOOS_BufferedFileReader;
import com.s8.core.io.json.utilities.JOOS_BufferedFileWriter;


/**
 * 
 */
public class IOModule implements TitaniumIOModule<MainStubObject> {


	public final static String FILENAME = "object.json";

	private final JSON_Lexicon lexicon;



	public IOModule() throws JSON_CompilingException {
		super();
		lexicon = JSON_Lexicon.from(MainStubObject.class); 
	}


	@Override
	public boolean hasResource(Path path) {
		return path.resolve(FILENAME).toFile().exists();
	}


	@Override
	public MainStubObject readResource(Path path) throws TiIOException {
		try {
			FileChannel channel = FileChannel.open(path.resolve(FILENAME), new OpenOption[]{ 
					StandardOpenOption.READ
			});


			/**
			 * lexicon
			 */

			JOOS_BufferedFileReader reader = new JOOS_BufferedFileReader(channel, StandardCharsets.UTF_8, 64);

			MainStubObject object = (MainStubObject) lexicon.parse(reader, true);
			reader.close();
			channel.close();
			
			return object;

		} catch (IOException e) {
			e.printStackTrace();
			throw new TiIOException(TiResourceStatus.FAILED_TO_LOAD);
		}
	}




	@Override
	public void writeResource(Path path, MainStubObject resource) throws IOException {

		Files.createDirectories(path);
		FileChannel channel = FileChannel.open(path.resolve(FILENAME), new OpenOption[]{ 
				StandardOpenOption.WRITE, StandardOpenOption.CREATE });


		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, resource, "   ", false);

		writer.close();
		channel.close();
	}



	@Override
	public boolean deleteResource(Path path) {
		path.resolve(FILENAME).toFile().delete();
		return true;
	}
}

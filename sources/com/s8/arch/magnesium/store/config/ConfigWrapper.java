package com.s8.arch.magnesium.store.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.s8.core.arch.silicon.SiliconConfiguration;
import com.s8.core.io.xml.annotations.XML_SetElement;
import com.s8.core.io.xml.annotations.XML_Type;
import com.s8.core.io.xml.codebase.XML_Codebase;
import com.s8.core.io.xml.handler.type.XML_TypeCompilationException;
import com.s8.core.io.xml.parser.XML_ParsingException;

@XML_Type(name = "ConfigWrapper", root = true)
public class ConfigWrapper {
	
	
	
	public SiliconConfiguration siConfig;
	
	@XML_SetElement(tag = "silicon")
	public void setSiliconConfig(SiliconConfiguration configuration) {
		this.siConfig = configuration;
	}
	
	public MgConfiguration mgConfig;
	
	@XML_SetElement(tag = "magnesium")
	public void setSiliconConfig(MgConfiguration configuration) {
		this.mgConfig = configuration;
	}

	/**
	 * 
	 */
	public ConfigWrapper() {
		super();
	}
	
	
	
	
	/**
	 * 
	 * @param pathname
	 * @return
	 * @throws XML_TypeCompilationException
	 * @throws FileNotFoundException
	 */
	public static ConfigWrapper load(String configPathname) 
			throws IOException, XML_TypeCompilationException {
		
		
		XML_Codebase lexicon = XML_Codebase.from(ConfigWrapper.class);

		// retrieve configuration
		InputStream inputStream = new FileInputStream(new File(configPathname));
		try (inputStream){
			return (ConfigWrapper) lexicon.deserialize(inputStream);
		} 
		catch (IOException | XML_ParsingException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}
}
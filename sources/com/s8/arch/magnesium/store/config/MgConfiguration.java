package com.s8.arch.magnesium.store.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.s8.io.xml.annotations.XML_SetElement;
import com.s8.io.xml.annotations.XML_Type;
import com.s8.io.xml.handler.XML_Lexicon;
import com.s8.io.xml.handler.type.XML_TypeCompilationException;
import com.s8.io.xml.parser.XML_ParsingException;



@XML_Type(name = "MgStoreConfiguration")
public class MgConfiguration {
	
	
	/**
	 * root path
	 */
	public String rootPath;
	
	
	@XML_SetElement(tag = "root-path")
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * 
	 */
	public MgConfiguration() {
		super();
	}
	
	
	
	
	/**
	 * 
	 * @param pathname
	 * @return
	 * @throws XML_TypeCompilationException
	 * @throws FileNotFoundException
	 */
	public static MgConfiguration load(XML_Lexicon lexicon, String configPathname) 
			throws 
			XML_TypeCompilationException, 
			FileNotFoundException {

		// retrieve configuration
		InputStream inputStream = new FileInputStream(new File(configPathname));
		MgConfiguration config = null;
		try (inputStream){
			config = (MgConfiguration) lexicon.deserialize(inputStream);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (XML_ParsingException e) {
			e.printStackTrace();
		}	
		return config;
	}
}

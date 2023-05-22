package com.s8.arch.magnesium.demos.repo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.s8.arch.magnesium.store.config.ConfigWrapper;
import com.s8.io.xml.codebase.XML_Codebase;
import com.s8.io.xml.handler.type.XML_TypeCompilationException;

public class XSD_SchemaGenerator {

	
	public static void main(String[] args) throws XML_TypeCompilationException, IOException {
		XML_Codebase context = XML_Codebase.from(ConfigWrapper.class);
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("config/mg-schema.xsd"))));
		context.xsd_writeSchema(writer);
		writer.close();
		
		System.out.println("done!");
	}
}

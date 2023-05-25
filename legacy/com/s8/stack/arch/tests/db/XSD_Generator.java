package com.s8.stack.arch.tests.db;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.s8.arch.silicon.SiliconConfiguration;
import com.s8.io.xml.codebase.XML_Codebase;
import com.s8.io.xml.handler.type.XML_TypeCompilationException;

public class XSD_Generator {

	public static void main(String[] args) throws XML_TypeCompilationException, IOException {
		XML_Codebase context = XML_Codebase.from(SiliconConfiguration.class);
		OutputStreamWriter writer = new FileWriter(new File("config/schema.xsd"));
		context.xsd_writeSchema(writer);
		writer.close();
	}

}

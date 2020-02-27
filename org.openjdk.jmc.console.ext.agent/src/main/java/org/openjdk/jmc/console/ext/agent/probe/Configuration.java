package org.openjdk.jmc.console.ext.agent.probe;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;

public class Configuration {
	private static final String PROBE_DEFINITION_XSD = "probe_definition.xsd";
	private static final Schema PROBE_SCHEMA;

	static {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			PROBE_SCHEMA = factory
					.newSchema(new StreamSource(Configuration.class.getResourceAsStream(PROBE_DEFINITION_XSD)));
		} catch (SAXException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static void validate(Source source) throws IOException, SAXException {
		Validator validator = PROBE_SCHEMA.newValidator();
		validator.validate(source);
	}

	public static void validate(InputStream xmlStream) throws IOException, SAXException {
		validate(new StreamSource(xmlStream));
	}
}

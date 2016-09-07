package de.dkt.eservices.hyperlinking.hyperlinking;

import eu.freme.common.exception.ExternalServiceFailedException;

public class HyperlinkingFactory {

	public static Hyperlinking getHyperlinking(String type) throws Exception {
//		System.out.println(type);
		if(type.equalsIgnoreCase("tfidf")){
			return new TFIDFHyperlinking();
		}
		else if(type.equalsIgnoreCase("ontology")){
			return new OntologyHyperlinking();
		}
		throw new ExternalServiceFailedException("Unsupported Hyperlinking Type");
	}
	
}

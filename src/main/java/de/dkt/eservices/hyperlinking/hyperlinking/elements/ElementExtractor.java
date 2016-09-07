package de.dkt.eservices.hyperlinking.hyperlinking.elements;

import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

public class ElementExtractor {

	public static List<Element> extractElements(List<Model> documents, String granularity) {
		List<Element> elements = new LinkedList<Element>();
		if(granularity.equalsIgnoreCase("document")){
			for (Model m : documents) {
				elements.add(new Document(NIFReader.extractDocumentWholeURI(m),NIFReader.model2String(m, RDFSerialization.TURTLE)));
			}
		}
		else if(granularity.equalsIgnoreCase("paragraph")){
			for (Model m : documents) {
				elements.add(new Document(NIFReader.extractDocumentWholeURI(m),NIFReader.model2String(m, RDFSerialization.TURTLE)));
			}
		}
//		else if(granularity.equalsIgnoreCase("entity")){
//			for (Model m : documents) {
//				elements.add(new Document(NIFReader.extractDocumentWholeURI(m),NIFReader.model2String(m, RDFSerialization.TURTLE)));
//			}
//		}
		return elements;
	}

}

package de.dkt.eservices.ehyperlinking.hyperlinking;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

public interface Hyperlinking {

	public HashMap<String, HashMap<String,Double>>  linkDocuments(List<Model> documents);
	
}

package de.dkt.eservices.hyperlinking.hyperlinking;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.eservices.hyperlinking.hyperlinking.elements.Element;

public interface Hyperlinking {

	public HashMap<String, HashMap<Element,Double>>  linkElements(List<Element> elements);

}

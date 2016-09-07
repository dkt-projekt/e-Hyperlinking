package de.dkt.eservices.hyperlinking;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import de.dkt.common.exceptions.LoggedExceptions;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.hyperlinking.hyperlinking.Hyperlinking;
import de.dkt.eservices.hyperlinking.hyperlinking.HyperlinkingFactory;
import de.dkt.eservices.hyperlinking.hyperlinking.elements.Element;
import de.dkt.eservices.hyperlinking.hyperlinking.elements.ElementExtractor;
import eu.freme.common.exception.BadRequestException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */
@Component
public class HyperlinkingService {
    
	Logger logger = Logger.getLogger(HyperlinkingService.class);

	public Model processDocumentsCollection(Model collectionModel,String hyperlinkingType, String granularity,int limit) throws Exception {
		try {
			List<Model> documents = NIFManagement.extractDocumentsModels(collectionModel);
			List<Model> outputNIFModels = generateLinks(documents,hyperlinkingType,granularity,limit);
			Model outputModel = generateCollectionFromDocuments(collectionModel,outputNIFModels);
			return outputModel;
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}

	private Model generateCollectionFromDocuments(Model collection, List<Model> outputNIFModels) {
		String prefix = NIFManagement.extractCollectionURI(collection);
		Model newCollection = NIFManagement.createDefaultCollectionModel(prefix);
		for (Model m : outputNIFModels) {
			NIFManagement.addDocumentToCollection(newCollection, m);
		}
		return newCollection;
	}

	private List<Model> generateLinks(List<Model> documents,String hyperlinkingType,String granularity,int limit) {
		List<Model> outputList = new LinkedList<Model>();
		Hyperlinking hl = null; 
		try{
			hl = HyperlinkingFactory.getHyperlinking(hyperlinkingType);
		}
		catch(Exception e){
			e.printStackTrace();
			throw LoggedExceptions.generateLoggedExternalServiceFailedException(logger, "Error generating the hyperlinking: "+hyperlinkingType);
		}
		
		List<Element> elements = ElementExtractor.extractElements(documents,granularity);
		
		HashMap<String, HashMap<Element,Double>> scores = hl.linkElements(elements);

//		System.out.println(scores);
		
		for (Model d : documents) {
			String documentUri = NIFManagement.extractCompleteDocumentURI(d);
	        Resource documentResource = d.getResource(documentUri);
			Set<Element> keyset2 = scores.get(documentUri).keySet();
			for (Element ae2 : keyset2) {
//				System.out.println("ADDING SOMETHING");
				NIFWriter.addAnnotationUnit(d, documentUri, ae2.getElementURI(), scores.get(documentUri).get(ae2));
		        d.add(documentResource, DKTNIF.isHyperlinkedTo, d.createResource(ae2.getElementURI()));
			}
			outputList.add(d);
		}
		return outputList;
	}

}

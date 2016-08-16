package de.dkt.eservices.ehyperlinking;

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
import de.dkt.eservices.ehyperlinking.hyperlinking.Hyperlinking;
import de.dkt.eservices.ehyperlinking.hyperlinking.HyperlinkingFactory;
import eu.freme.common.exception.BadRequestException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */
@Component
public class EHyperlinkingService {
    
	Logger logger = Logger.getLogger(EHyperlinkingService.class);

	public Model processDocumentsCollection(Model collectionModel,String hyperlinkingType, int limit) throws Exception {
		try {
			List<Model> documents = NIFManagement.extractDocumentsModels(collectionModel);
			List<Model> outputNIFModels = generateLinksBetweenDocuments(documents,hyperlinkingType,limit);
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

	private List<Model> generateLinksBetweenDocuments(List<Model> documents,String hyperlinkingType,int limit) {
		List<Model> outputList = new LinkedList<Model>();
		Hyperlinking hl = null; 
		try{
			hl = HyperlinkingFactory.getHyperlinking(hyperlinkingType);
		}
		catch(Exception e){
			e.printStackTrace();
			throw LoggedExceptions.generateLoggedExternalServiceFailedException(logger, "Error generating the hyperlinking: "+hyperlinkingType);
		}
		
		HashMap<String, HashMap<String,Double>> scores = hl.linkDocuments(documents);

//		System.out.println(scores);
		
		for (Model d : documents) {
			String documentUri = NIFManagement.extractCompleteDocumentURI(d);
	        Resource documentResource = d.getResource(documentUri);
			Set<String> keyset2 = scores.get(documentUri).keySet();
			for (String k2 : keyset2) {
				System.out.println("ADDING SOMETHING");
				NIFWriter.addAnnotationUnit(d, documentUri, k2, scores.get(documentUri).get(k2));
		        d.add(documentResource, DKTNIF.isHyperlinkedTo, d.createResource(k2));
			}
			outputList.add(d);
		}
		return outputList;
	}

}

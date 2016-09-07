package de.dkt.eservices.hyperlinking.hyperlinking;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dkt.common.exceptions.LoggedExceptions;
import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.hyperlinking.hyperlinking.elements.Element;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

public class OntologyHyperlinking implements Hyperlinking {

	static Logger logger = Logger.getLogger(OntologyHyperlinking.class);

	public String repositoryName = "hyperlinking";
	
	public int maxIterations = 10;
	
	public OntologyHyperlinking() {
	}
	
	public OntologyHyperlinking(String repositoryName) {
		super();
		this.repositoryName = repositoryName;
	}
	
	public HashMap<String, HashMap<Element,Double>> linkElements(List<Element> documents){
		HashMap<String, HashMap<Element,Double>> relatednessScores = new HashMap<String, HashMap<Element,Double>>();
		try{
			String repository = repositoryName + "" + (new Date()).getTime();

			System.out.println(repository);
			//Add every model to the sesame storage.
			for (Model model : documents) {
				System.out.println("\tAdding model to sesame");
				System.out.println(NIFReader.model2String(model,RDFSerialization.TURTLE));
				if(!addModelToSesame(repository, model)){
					System.out.println("ERROR at storing model into sesame: "+NIFManagement.extractCompleteDocumentURI(model));
				}
			}
			
			for (Model model1 : documents) {
				String doc1URI = NIFManagement.extractCompleteDocumentURI(model1);
				HashMap<Element,Double> scores = new HashMap<Element, Double>();
				for (Model model2 : documents) {
					String doc2URI = NIFManagement.extractCompleteDocumentURI(model2);
					double d = similarity(doc1URI, doc2URI, repository);
					scores.put(new Element(doc2URI), d);
				}
				relatednessScores.put(doc1URI, scores);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return relatednessScores;
	}
		
	public static double similarity(String doc1Id, String doc2Id, String storageName) throws Exception {
		String inputSPARQLData = "select ?start ?end (count(?mid) as ?length)" + 
		"		where {" + 
		"		  values (?start ?end) { ("+doc1Id+" "+doc2Id+") }" + 
		"		  ?start :p+ ?mid ." + 
		"		  ?mid :p* ?end ." + 
		"		}" + 
		"		group by ?start ?end"; 
		String similarityXMLString = retrieveTQRTripletsFromSPARQL(storageName, inputSPARQLData);
		
		//TODO extract the similarity output.

		System.out.println("DEBUG: result of similarity for a document: "+similarityXMLString);
		
//		for (BindingSet bs : list) {
//			//					Value valueOfS = bs.getValue("s");
////			Value valueOfS = bs.getValue("start");
////			Value valueOfP = bs.getValue("end");
//			Value valueOfO = bs.getValue("length");
//
//			String pathLength = valueOfO.stringValue();
//			double d = Double.parseDouble(pathLength);
//			return d;
//		}
		return 0;
	}

	private static String retrieveTQRTripletsFromSPARQL(String storageName, String inputSPARQLData) {
		try{
//					.queryString("informat", "turtle")
//					.queryString("outformat", "turtle")
//					.queryString("storageName", storageName)
//					.queryString("storageCreate", true)
////					.queryString("input", )
//					.queryString("inputDataFormat", "body")
//					.queryString("inputDataMimeType", "text/turtle")
//					.body(NIFReader.model2String(m, RDFSerialization.TURTLE))
//					.asString();
			String url = "http://dev.digitale-kuratierung.de/api/e-sesame/retrieveData";
			HttpResponse<String> response = Unirest.post(url)
					.queryString("informat", "text/plain")
					.queryString("input", inputSPARQLData)
					.queryString("outformat", "application/rdf+xml")
					.queryString("storageName", storageName)
//					.queryString("storagePath", storageName)
					.queryString("inputDataFormat", "sparql")
					.queryString("inputData", "")
					.asString();
			if(response.getStatus() == 200){
				return response.getBody();
			}
			throw LoggedExceptions.generateLoggedExternalServiceFailedException(logger, "Error connecting to sesame endpoint: "+response.getBody());
		}
		catch(Exception e){
			throw LoggedExceptions.generateLoggedExternalServiceFailedException(logger, "Exception at connecting to sesame endpoint: " + e.getMessage());
		}
	}

	private static boolean addModelToSesame(String storageName, Model m){
		try{
			String url = "http://dev.digitale-kuratierung.de/api/e-sesame/storeData";
			HttpResponse<String> response = Unirest.post(url)
					.queryString("informat", "turtle")
					.queryString("outformat", "turtle")
					.queryString("storageName", storageName)
					.queryString("storageCreate", true)
//					.queryString("input", )
					.queryString("inputDataFormat", "body")
					.queryString("inputDataMimeType", "text/turtle")
					.body(NIFReader.model2String(m, RDFSerialization.TURTLE))
					.asString();
			if(response.getStatus() == 200){
				return true;
			}
			throw LoggedExceptions.generateLoggedExternalServiceFailedException(logger, "Error connecting to sesame endpoint.");
		}
		catch(Exception e){
			throw LoggedExceptions.generateLoggedExternalServiceFailedException(logger, "Exception at connecting to sesame endpoint: " + e.getMessage());
		}
	}
		
//	public static Map<String,Float> checkPathes(String doc1Id, String storageName, int maxIterations) throws Exception {
//		List<String> addedNodes = new LinkedList<String>();
//		Map<String,Float> map = new HashMap<String, Float>();
//		
//		TreeNode root = new TreeNode(doc1Id);
//		TreeNode nullN = new TreeNode(null);
//		
//		TreeNode current = root;
//		Queue<TreeNode> queue = new LinkedBlockingQueue<>();
////		Stack<TreeNode> stack = new Stack<TreeNode>();
//		queue.add(root);
//		addedNodes.add(root.value);
//		//Extract all the relations of the sesame for this docid, store them into childs and store childs into queue or list to process them later.
//		queue.add(nullN);
//		for (int i = 0; i < maxIterations; i++) {
//			System.out.println("Entering");
//			current = queue.poll();
//			while(current.value!=null){
//				System.out.println("there is some node...");
//				String inputSPARQLData = "select ?s ?p ?p2 ?o where {\n" +
//				        " <"+current.value+"> ?p ?o \n" +
////				        " ?s <"+current.value+"> ?o .\n" +
////				        " ?s ?p2 <"+current.value+"> \n" +
//				        "}";
////				String result = SesameStorage.retrieveTripletsFromSPARQL(storageName, inputSPARQLData);
////				System.out.println(result);
//				List<BindingSet> list = SesameStorage.retrieveTQRTripletsFromSPARQL(storageName, inputSPARQLData);
//				for (BindingSet bs : list) {
////					Value valueOfS = bs.getValue("s");
//					Value valueOfS = bs.getValue("s");
//					Value valueOfP = bs.getValue("p");
//					Value valueOfO = bs.getValue("o");
//					
////					System.out.println("\t\t\t===================");
////					System.out.println("\t\t\t"+valueOfS);
////					System.out.println("\t\t\t"+valueOfP);
////					System.out.println("\t\t\t"+valueOfO);
//					
//					if(!valueOfP.stringValue().contains("http://dkt.dfki.de/isTypeOf")){
//					
//						String objectText = null;
//						if(valueOfS!=null){
//							objectText = valueOfS.stringValue();
//						}
//						else{
//							objectText = valueOfO.stringValue();
//						}
//						if(objectText.startsWith("http")){
//							objectText = objectText.replace(">", "");
//							//Every relation we get (if it is a non literal)
//							if(!addedNodes.contains(objectText)){
//								
//								TreeNode aux = new TreeNode(current, 1, objectText);
//								current.childs.add(aux);
//								queue.add(aux);
//								addedNodes.add(objectText);
//								
//								if(objectText.startsWith("http://dkt.dfki.de/documents") && !objectText.contains("#")){
//									System.out.println("\n\n\nüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüü");
//									System.out.println("\n\n\nüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüü");
//									System.out.println("\n\n\nüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüü");
//									System.out.println("\n\n\nüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüüü");
//								}
//								
//								
//							}
//						}
//						
//						
//					}
//						
//				}
////				String output = SesameStorage.retrieveTriplets(storageName, current.value, null, null);
////				System.out.println("Repository information: "+output);
////				
////				while (result.hasNext()) {  // iterate over the result
////					BindingSet bindingSet = result.next();
////					Value valueOfS = bindingSet.getValue("s");
////					Value valueOfP = bindingSet.getValue("p");
////					Value valueOfO = bindingSet.getValue("o");
////					
////					System.out.println("\t\t\t===================");
////					System.out.println("\t\t\t"+valueOfS);
////					System.out.println("\t\t\t"+valueOfP);
////					System.out.println("\t\t\t"+valueOfO);
////					if(valueOfO.stringValue().contains("http")){
////						String resource=null;
////						//Every relation we get (if it is a non literal)
////						TreeNode aux = new TreeNode(current, 1, valueOfO.stringValue());
////						current.childs.add(aux);
////						queue.add(aux);
////					}
////				}
//				
//				current = queue.poll();
//			}
//			queue.add(nullN);
//		}
////		root.printByLevel("","  ");
//		//Retrieve the possible paths from the ROOT tree.
//		computePath(root, map, 0f, "");
//		return map;
//	}
//
//	public static void computePath(TreeNode n,Map<String,Float> map,Float f,String relationValue){
////		System.out.println("Check Path for element: "+n.value);
//		List<TreeNode> childs = n.childs;
//		for (TreeNode child : childs) {
//			if(child.value.contains("http://dkt.dfki.de/documents/") && !child.value.contains("#")){
//				System.out.println("Added Path for element: "+child.value);
//				map.put(child.value+" --> "+relationValue, f+child.relationsValue);
//			}
//			else{
//				computePath(child, map, f + n.relationsValue,relationValue+";"+child.value);
//			}
//		}
//	}

}

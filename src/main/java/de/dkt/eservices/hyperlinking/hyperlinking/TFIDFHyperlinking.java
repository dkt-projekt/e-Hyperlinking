package de.dkt.eservices.hyperlinking.hyperlinking;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.hyperlinking.hyperlinking.elements.Element;

public class TFIDFHyperlinking implements Hyperlinking{

	static Logger logger = Logger.getLogger(TFIDFHyperlinking.class);

	public TFIDFHyperlinking() {
	}
	
	@Override
	public HashMap<String, HashMap<Element,Double>> linkElements(List<Element> elements) {
		HashMap<String, HashMap<Element,Double>> relatednessScores = new HashMap<String, HashMap<Element,Double>>();
		try{
			List<List<String>> stringDocuments = new LinkedList<List<String>>();
			for (Element e : elements) {
				stringDocuments.add(splitString(e.getText()));
			}

			for (Element e1: elements) {
				String doc1URI = e1.getElementURI();
				HashMap<Element,Double> scores = new HashMap<Element, Double>();
				for (Element e2: elements) {
					List<String> stringDoc1 = splitString(e1.getText());
					List<String> stringDoc2 = splitString(e2.getText());
//					String doc2URI = e2.getElementURI();
					double d = similarity(stringDoc1, stringDoc2, stringDocuments);
					scores.put(e2, d);
//					System.out.println(doc2URI +"---"+d);
				}
				relatednessScores.put(doc1URI, scores);
//				System.out.println("\t"+doc1URI+"---"+scores);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return relatednessScores;
	}
	
	public List<String> splitString(String s){
		String[] parts = s.split(" ");
		List<String> docStringList = new LinkedList<String>(); 
		for (String p : parts) {
			docStringList.add(p);
		}
		return docStringList;
	}
	
	public double tf(List<String> doc, String term) {
	    double result = 0;
	    for (String word : doc) {
	       if (term.equalsIgnoreCase(word))
	              result++;
	       }
		double d2 = result / doc.size();
//		System.out.println("\t\tTF:"+d2);
	    return d2;
	}
	
	public double idf(List<List<String>> docs, String term) {
	    double n = 0;
	    for (List<String> doc : docs) {
	        for (String word : doc) {
	            if (term.equalsIgnoreCase(word)) {
	                n++;
	                break;
	            }
	        }
	    }
		double d2 = Math.log(docs.size() / n);
//		System.out.println("\t\tIDF:"+d2);
	    return d2;
	}
	
	public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
	    return tf(doc, term) * idf(docs, term);
	}
	
	public double similarity(List<String> doc, List<String> doc2, List<List<String>> docs) {
		double similarity = 0.0;
		for (String term : doc) {
			double d2 = tfIdf(doc2, docs, term);
//			System.out.println("\t"+d2);
			similarity += d2;
		}
//		System.out.println(similarity);
//		System.out.println(doc.size());
//		System.out.println(similarity / doc.size());
		return similarity / doc.size();
	}
	
}

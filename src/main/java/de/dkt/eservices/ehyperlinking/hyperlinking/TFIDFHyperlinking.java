package de.dkt.eservices.ehyperlinking.hyperlinking;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFReader;

public class TFIDFHyperlinking implements Hyperlinking{

	static Logger logger = Logger.getLogger(TFIDFHyperlinking.class);

	public TFIDFHyperlinking() {
	}
	
	@Override
	public HashMap<String, HashMap<String,Double>> linkDocuments(List<Model> documents) {
		HashMap<String, HashMap<String,Double>> relatednessScores = new HashMap<String, HashMap<String,Double>>();
		try{
			List<List<String>> stringDocuments = new LinkedList<List<String>>();
			for (Model model : documents) {
				String stringDoc = NIFReader.extractIsString(model);
				stringDocuments.add(splitString(stringDoc));
			}

			for (Model model1 : documents) {
				String doc1URI = NIFManagement.extractCompleteDocumentURI(model1);
				HashMap<String,Double> scores = new HashMap<String, Double>();
				for (Model model2 : documents) {
					List<String> stringDoc1 = splitString(NIFReader.extractIsString(model1));
					List<String> stringDoc2 = splitString(NIFReader.extractIsString(model2));
					String doc2URI = NIFManagement.extractCompleteDocumentURI(model2);
					double d = similarity(stringDoc1, stringDoc2, stringDocuments);
					scores.put(doc2URI, d);
					System.out.println(doc2URI +"---"+d);
				}
				relatednessScores.put(doc1URI, scores);
				System.out.println("\t"+doc1URI+"---"+scores);
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
		System.out.println("\t\tTF:"+d2);
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
		System.out.println("\t\tIDF:"+d2);
	    return d2;
	}
	
	public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
	    return tf(doc, term) * idf(docs, term);
	}
	
	public double similarity(List<String> doc, List<String> doc2, List<List<String>> docs) {
		double similarity = 0.0;
		for (String term : doc) {
			double d2 = tfIdf(doc2, docs, term);
			System.out.println("\t"+d2);
			similarity += d2;
		}
		System.out.println(similarity);
		System.out.println(doc.size());
		System.out.println(similarity / doc.size());
		return similarity / doc.size();
	}
	
//	public static void main(String[] args) {
//		 
//	    List<String> doc1 = Arrays.asList("Lorem", "ipsum", "dolor", "ipsum", "sit", "ipsum");
//	    List<String> doc2 = Arrays.asList("Vituperata", "incorrupte", "at", "ipsum", "pro", "quo");
//	    List<String> doc3 = Arrays.asList("Has", "persius", "disputationi", "id", "simul");
//	    List<List<String>> documents = Arrays.asList(doc1, doc2, doc3);
//	 
//	    TFIDFCalculator calculator = new TFIDFCalculator();
//	    double tfidf = calculator.tfIdf(doc1, documents, "ipsum");
//	    System.out.println("TF-IDF (ipsum) = " + tfidf);
//	 
//	}
}

package de.dkt.eservices.hyperlinking;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.dkt.common.filemanagement.FileFactory;
import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;

public class EHyperlinkingTest {
	
	TestHelper testHelper;
	ValidationHelper validationHelper;

	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
	}
	
	private HttpRequestWithBody baseRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-hyperlinking/testURL";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody processRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-hyperlinking/processDocumentsCollection";
		return Unirest.post(url);
	}
	
	@Test
	public void test1_SanityCheck() throws UnirestException, IOException,
			Exception {
		HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
	}
	
	@Test
	public void test2_ProcessDocumentCollection_TFIDF() throws UnirestException, IOException,Exception {
		BufferedReader br = FileFactory.generateBufferedReaderInstance("storage/collectionExample.nif","utf-8");
		String content = "";
		String line = "";
		while((line=br.readLine())!=null){
			content += line + "\n";
		}
		br.close();
		HttpResponse<String> response = processRequest()
				.queryString("informat", "turtle")
//				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("hyperlinkingType", "tfidf")
				.queryString("limit", 10)
				.body(content)
				//.field("file", f)
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
//		System.out.println(response.getBody());
	}

	@Test
	public void test3_ProcessDocumentCollection_Ontology() throws UnirestException, IOException,Exception {
		BufferedReader br = FileFactory.generateBufferedReaderInstance("storage/collectionExample.nif","utf-8");
		String content = "";
		String line = "";
		while((line=br.readLine())!=null){
			content += line + "\n";
		}
		br.close();
		HttpResponse<String> response = processRequest()
				.queryString("informat", "turtle")
//				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("hyperlinkingType", "ontology")
				.queryString("limit", 10)
				.body(content)
				//.field("file", f)
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
//		System.out.println(response.getBody());
	}

}

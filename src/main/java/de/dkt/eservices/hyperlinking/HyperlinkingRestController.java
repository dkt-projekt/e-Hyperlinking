package de.dkt.eservices.hyperlinking;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.exceptions.LoggedExceptions;
import de.dkt.common.feedback.InteractionManagement;
import de.dkt.common.niftools.NIFReader;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */
@RestController
public class HyperlinkingRestController extends BaseRestController {

	Logger logger = Logger.getLogger(HyperlinkingRestController.class);

	@Autowired
	HyperlinkingService service;

	@Autowired
	RDFConversionService rdfConversionService;

	@RequestMapping(value = "/e-hyperlinking/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testHyperlinkingURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}
	
	@RequestMapping(value = "/e-hyperlinking/processDocumentsCollection", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> hyperlinkDocumentCollection(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,

			@RequestParam(value = "hyperlinkingType", required = false) String hyperlinkingType,
			@RequestParam(value = "granularity", required = false) String granularity,
			@RequestParam(value = "limit", required = false) int limit,
			@RequestBody(required = false) String postBody) throws Exception {

		if (input == null) {
			input = i;
		}
		if (informat == null) {
			informat = f;
		}
		if (outformat == null) {
			outformat = o;
		}
		if (prefix == null) {
			prefix = p;
		}
		if(input==null || input.equalsIgnoreCase("")){
			input=postBody;
			if(input==null || input.equalsIgnoreCase("")){
				throw LoggedExceptions.generateLoggedBadRequestException(logger, "No input collection provided: input and body are NULL or empty.");
			}
		}
//		ParameterChecker.checkNotNullOrEmpty(inputDataFormat, "input data type", logger);
        NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
        Model inModel = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
			rdfConversionService.plaintextToRDF(inModel, nifParameters.getInput(),language, nifParameters.getPrefix());
        } else {
            inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
        }
		try {
			Model outputModel = service.processDocumentsCollection(inModel, hyperlinkingType, granularity,limit);
			String nifOutput = NIFReader.model2String(outputModel, RDFSerialization.TURTLE);
			HttpHeaders responseHeaders = new HttpHeaders();
//			System.out.println("DEBUG: output of the endpoint call: "+ outputModel);
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-hyperlinking/processDocumentsCollection", "Success", "", "", "", "");
			
			return new ResponseEntity<String>(nifOutput, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-Sesame/retrieveData", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}

}

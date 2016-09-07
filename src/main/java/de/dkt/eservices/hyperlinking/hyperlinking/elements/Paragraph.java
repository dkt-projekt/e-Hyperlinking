package de.dkt.eservices.hyperlinking.hyperlinking.elements;

public class Paragraph extends Element{
	
	private String documentURI;

	public Paragraph(String elementURI,String text) {
		super(elementURI,text);
	}

	public Paragraph(String elementURI,String text,String docURI) {
		super(elementURI,text);
		documentURI = docURI;
	}

	public String getDocumentURI() {
		return documentURI;
	}

	public void setDocumentURI(String documentURI) {
		this.documentURI = documentURI;
	}

}

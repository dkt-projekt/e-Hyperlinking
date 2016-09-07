package de.dkt.eservices.hyperlinking.hyperlinking.elements;

public class Element {

	private String elementURI;
	private String text;
	
	public Element(String elementURI, String text) {
		super();
		this.elementURI = elementURI;
		this.text = text;
	}

	public String getElementURI() {
		return elementURI;
	}

	public void setElementURI(String elementURI) {
		this.elementURI = elementURI;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Element){
			Element ae = (Element)obj;
			return elementURI.equals(ae.getElementURI());
		}
		return false;
	}
}

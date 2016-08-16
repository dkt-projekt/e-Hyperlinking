package de.dkt.eservices.ehyperlinking.hyperlinking;

public class DktDocument {

	public String content;
	public String luceneId;
	public String docId;
	public String language;
	public String mimeType;

	public DktDocument(String content, String luceneId, String docId, String language, String mimeType) {
		super();
		this.content = content;
		this.luceneId = luceneId;
		this.docId = docId;
		this.language = language;
		this.mimeType = mimeType;
	}
	
}

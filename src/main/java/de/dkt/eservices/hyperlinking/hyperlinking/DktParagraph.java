package de.dkt.eservices.hyperlinking.hyperlinking;

public class DktParagraph {

	public String content;
	public String originatingDocluceneId;
	public String paragraphId;
	public String language;
	public String mimeType;

	public DktParagraph(String content, String originatingDocluceneId, String paragraphId, String language, String mimeType) {
		super();
		this.content = content;
		this.originatingDocluceneId = originatingDocluceneId;
		this.paragraphId = paragraphId;
		this.language = language;
		this.mimeType = mimeType;
	}
	
}

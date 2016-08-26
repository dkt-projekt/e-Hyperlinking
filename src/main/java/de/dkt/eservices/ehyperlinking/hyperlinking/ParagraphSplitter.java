package de.dkt.eservices.ehyperlinking.hyperlinking;

import java.util.LinkedList;
import java.util.List;

public class ParagraphSplitter {

	public static List<String> splitText(String text){ // this is a placeholder for a more sophisticated way of splitting text into paragraphs (http://www.coli.uni-saarland.de/~csporled/papers/tslp06.pdf)
		
		List<String> paragraphs = new LinkedList<String>();
		String[] pars = text.split("\\n\\s+");
		for (String s : pars){
			paragraphs.add(s);
		}
		
		return paragraphs;
		
	}
	
	
	public static void main(String[] args) {
		String s = 
				"This article is about the provision of medical care. For other uses, see Health care (disambiguation) . “MedicalCare”redirectshere. Forthehealthjournal,see Medical Care (journal) . Health care or healthcare is the maintenance or im- \n" +
						"\n" +
						"  Weill-Cornell New York-Presbyterian Hospital , white complex at centre, one of the world’s busiest \n" +
						"\n" +
						"  provement of health via the diagnosis , treatment , and prevention of disease , illness , injury , and other physical and mental impairments in human beings. Health care is delivered by health professionals (providers or practitioners) in allied health professions , chiropractic , physicians , physician associates, dentistry , midwifery , nursing , medicine , optometry , pharmacy , psychology , and other health professions . It includes the work done in providing primary care , secondary care , and tertiary care , as well as in public health . Access to health care varies across countries, groups, and individuals, largely influenced by social and economic conditions as well as the health policies in place. Coun- tries and jurisdictions have different policies and plans in relation to the personal and population-based health care goals within their societies. Health care systems are organizations established to meet the health needs of tar- get populations. Their exact configuration varies between national and subnational entities. In some countries and jurisdictions, health care planning is distributed among market participants, whereas in others, planning occurs more centrally among governments or other coordinat- ing bodies. In all cases, according to the World Health Organization (WHO), a well-functioning health care sys- temrequiresarobustfinancingmechanism; awell-trained and adequately-paid workforce ; reliable information on which to base decisions and policies ; and well maintained\n" +
						"\n" +
						"  health facilities and logistics to deliver quality medicines \n" +
						"\n" +
						"  Health care can contribute to a significant part of a coun- try’s economy . In 2011, the health care industry con- sumed an average of 9.3 percent of the GDP or US$ 3,322 ( PPP-adjusted ) per capita across the 34 mem- bers of OECD countries. The USA (17.7%, or US$ PPP 8,508), the Netherlands (11.9%, 5,099), France (11.6%, 4,118), Germany (11.3%, 4,495), Canada (11.2%, 5669), and Switzerland (11%, 5,634) were the top spenders, however life expectancy in total population at birth was highest in Switzerland (82.8 years), Japan and Italy (82.7), Spain and Iceland (82.4), France (82.2) and Australia (82.0), while OECD’s average exceeds 80 years for the first time ever in 2011: 80.1 years, a gain of 10 years since 1970. The USA (78.7 years) ranges only on place 26 among the 34 OECD member countries, but has the highest costs by far. All OECD countries have achieved universal (or almost universal) health coverage, \n";
	List<String> paragraphs = splitText(s);
	for (String p : paragraphs){
		System.out.println("############################################################");
		System.out.println(p);
		System.out.println("############################################################");
	}
	
	
	}
	
}

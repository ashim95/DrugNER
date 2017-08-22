package sric.iitkgp.data.preparation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMatch {

	// public static void main(String[] args) {
	// StringMatch sm = new StringMatch();
	// sm.matchAllOccurrences(null, "paracetamol");
	// }
	
	public List<DrugMatchDao> tagAbstracts(List<RawAbstract> abstractList, List<DrugName> drugNameList){
		List<DrugMatchDao> drugMatchList = new ArrayList<DrugMatchDao>();
		
		Long startTime = System.nanoTime();
		
		for(DrugName drugName : drugNameList){
			
			for(RawAbstract abst: abstractList){
				List<DrugMatchDao> matchList = this.matchAllOccurrences(abst, drugName);
				if ((matchList != null) && matchList.size() > 0){
					drugMatchList.addAll(matchList);
				}
			}
		}
		
		long endTime = System.nanoTime();
		double duration = (endTime - startTime) / 1000000000;
		duration = duration/60;
		System.out.println("Total Time taken to find names in abstracts (in minutes): " + duration);
		
		return drugMatchList;
	}
	
	public List<DrugMatchDao> matchAllOccurrences(RawAbstract abst, DrugName drugName) {
		String text = abst.getAbstractText();
		String name = drugName.getName();
		List<DrugMatchDao> drugMatchList = new ArrayList<DrugMatchDao>();

		//// For Testing Purposes
		// String text = "Paracetamol (acetaminophen) is a pain reliever and a fever
		// reducer. The exact mechanism of action of is not known.\n" +
		// "\n" +
		// "Paracetamol is used to treat many conditions such as headache, muscle aches,
		// arthritis, backache, toothaches, colds, and fevers. It relieves pain in mild
		// arthritis but has no effect on the underlying inflammation and swelling of
		// the joint.\n" +
		// "\n" +
		// "Paracetamol may also be used for other purposes not listed in this
		// medication guide.";
		//
		////
		String name_regex = name.replaceAll("\\(", "\\\\(");
		name_regex = name_regex.replaceAll("\\)", "\\\\)");
		name_regex = name_regex.replaceAll("\\+", "\\\\+");
		name_regex = name_regex.replaceAll("\\*", "\\\\*");
		name_regex = name_regex.replaceAll("\\.", "\\\\.");
		name_regex = name_regex.replaceAll("\\?", "\\\\?");
		name_regex = name_regex.replaceAll("\\]", "\\\\]");
		name_regex = name_regex.replaceAll("\\[", "\\\\[");
		name_regex = name_regex.replaceAll("\\}", "\\\\}");
		name_regex = name_regex.replaceAll("\\{", "\\\\{");
		name_regex = name_regex.replaceAll("\\$", "\\\\$");
		name_regex = name_regex.replaceAll("\\^", "\\\\^");
		System.out.println(name_regex);
		Pattern p = Pattern.compile("\\b" + name_regex + "\\b");
//		Pattern p = Pattern.compile("\\bmercaptopurine\\b");
		Matcher m = p.matcher(text.toLowerCase());

		while (m.find()) {
			int start = m.start();
			int end = start + name.length();
			if(start >0){
				if (text.charAt(start-1) == '-') continue;
			}
			if(end < text.length()){
				if (text.charAt(end) == '-') continue;
			}
			DrugMatchDao drugMatch = new DrugMatchDao(drugName.getRxcui(), drugName.getRxaui(), name,
					text.substring(start, end), abst.getPmid(), start, end);
			drugMatchList.add(drugMatch);
		}
		
		if (drugMatchList.size() >0) {
			System.out.println("Found " + drugMatchList.size() + " matches in pmid: " + abst.getPmid());
			return drugMatchList;
		}
		return null;
	}
}

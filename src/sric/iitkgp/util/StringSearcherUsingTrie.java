package sric.iitkgp.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import banner.eval.BANNER;
import banner.tokenization.Tokenizer;
import banner.types.Mention;
import banner.types.Sentence;
import sric.iitkgp.data.preparation.AbstractDBUtils;
import sric.iitkgp.data.preparation.DrugMatchDao;
import sric.iitkgp.data.preparation.DrugMatchDaoDBUtils;
import sric.iitkgp.data.preparation.RawAbstract;

public class StringSearcherUsingTrie {
	public static void main(String[] args) throws IOException, SQLException {

		Long startTime = System.nanoTime();

		HierarchicalConfiguration config;

		try {
			config = new XMLConfiguration(args[0]);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}

		Tokenizer tokenizer = BANNER.getTokenizer(config);

		HierarchicalConfiguration localConfig = config.configurationAt(BANNER.class.getPackage().getName());
		String dictionaryName = localConfig.getString("dictionaryTagger");
		if (dictionaryName == null)
			throw new IllegalArgumentException("Dictionary Tagger not Found !!");
		CustomDictionaryTagger dictionary = null;

		dictionary = new CustomDictionaryTagger();
		dictionary.configure(config, tokenizer);
		dictionary.load(config);

//		testImplementation(dictionary, tokenizer);

		DrugMatchDaoDBUtils drugNameDBUtils = new DrugMatchDaoDBUtils();
		AbstractDBUtils abstractDBUtils = new AbstractDBUtils();
		abstractDBUtils.getReady();
		
		RawAbstractAnnotator annotator = new RawAbstractAnnotator();
		
		boolean allAbstracts = false;
		List<RawAbstract> abstractList = null;
		int i = 0;
		int count = 2;
//		while (!allAbstracts && (i < count)) {
		while (!allAbstracts) {
			abstractList = abstractDBUtils.fetchNextBatch();
			if (abstractList == null || abstractList.size() == 0) {
				System.out.println("No Abstracts found or End reached. \n");
				allAbstracts = true;
				break;
			}

			System.out.println("Processing Abstracts...");
			List<DrugMatchDao> drugMatchList = annotator.annotateAbstracts(abstractList, dictionary, tokenizer);;
			if (drugMatchList == null || drugMatchList.size() == 0) {
				System.out.println("No Annotations found in this batch : ");
				continue;
			}
			drugNameDBUtils.persistDrugNameList(drugMatchList);
			System.out.println("Done Processing for " + i + " !! Now onto next set of abstracts !");
			i++;		
		}
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000000;
		System.out.println("Execution Finished !!");
		System.out.println("Total Time taken (in minutes): " + duration/60);
		

	}

	public static void testImplementation(CustomDictionaryTagger dictionary, Tokenizer tokenizer){
		RawAbstractAnnotator annotator = new RawAbstractAnnotator();
		
		
		List<RawAbstract> abstractList = new ArrayList<RawAbstract>();
		RawAbstract abst = new RawAbstract();
		abst.setPmid(7595741);
		abst.setAbstractText("To improve the prognosis of patients with poor-risk peripheral primitive neuroectodermal tumors (pPNETs; including peripheral neuroepithelioma and Ewing's sarcoma), while testing the feasibility of intensive use in adolescents and young adults of high-dose cyclophosphamide, doxorubicin, and vincristine (HD-CAV).    This report concerns previously untreated patients with newly diagnosed pPNET deemed poor-risk because of a tumor volume more than 100 cm3 or metastases to bone or bone marrow. The P6 protocol consists of seven courses of chemotherapy. Courses 1, 2, 3, and 6 include 6-hour infusions of cyclophosphamide on days 1 and 2 for a total of 4,200 mg/m2 per course (140 mg/kg per course for patients < 10 years old), plus 72-hour infusions of doxorubicin 75 mg/m2 and vincristine 2.0 mg/m2 beginning on day 1 (HD-CAV). Courses 4, 5, and 7 consist of 1-hour infusions of ifosfamide 1.8 g/m2/d and etoposide (VP-16) 100 mg/m2/d, for 5 days. Granulocyte colony-stimulating factor (G-CSF) and mesna are used. Courses start after neutrophil counts reach 500/microL and platelet counts reach 100,000/uL. Surgical resection follows course 3 and radiotherapy follows completion of all chemotherapy.    Among the first 36 consecutive assessable patients (median age, 17 years), HD-CAV achieved excellent histopathologic or clinical responses in 34 patients and partial responses (PRs) in two patients. For 24 patients with locoregional disease, the 2-year event-free survival rate was 77%; adverse events were two locoregional relapses, one distant relapse, and one secondary leukemia. All six patients with metastatic disease limited to lungs achieved a complete response (CR) and did not relapse; one is in remission 36+ months from diagnosis, but the other patients are not assessable in terms of long-term efficacy of the P6 protocol because of short follow-up time (n = 3), additional systemic therapy (bone marrow transplantation), or septic death (autopsy showed no residual pPNET). All six patients with widespread metastases had major responses, including eradication of extensive bone marrow involvement, but distant relapses ensued. Myelosuppression was severe, but most patients received the first three courses of HD-CAV within 6 to 7 weeks. Major nonhematologic toxicities were mucositis and peripheral neuropathy.    Excellent antitumor efficacy and manageable toxicity support the dose-intensive use of HD-CAV for pPNET in children, as well as in young adults. Consolidation of remissions of pPNET metastatic to bone and bone marrow remains a therapeutic challenge.");
//		abst.setAbstractText("To evaluate responses to medical therapy in ulcerative colitis, rectal biopsies of patients with active untreated disease, individuals with positive and negative sigmoidoscopic findings treated with salicylazosulfapyridine, (+-)-isomer of equol and 6-mercaptopurine, alone and in combinations and noncolitis controls were compared histologically. Predominant histological observations were analyzed statistically. There were fewer crypt abscesses but more mucosal edema after all forms of therapy. Quantitative histopathological analysis failed to demonstrate that the response to one drug was significantly different from another.");
		abstractList.add(abst);
		
		annotator.annotateAbstracts(abstractList, dictionary, tokenizer);
		
	}
	
	public static void testForDictionaryTagger(CustomDictionaryTagger dictionary, Tokenizer tokenizer) {

		String sentenceText = "MESNA has been successfully used to ease abdominal myomectomies and excision of "
				+ "endometrial cysts; in ENT surgery, topical MESNA could be widely used, "
				+ "from ear and skull base to head and neck diseases, in both outpatient and operating-room settings.";

		 sentenceText = "To evaluate responses to medical therapy in" + 
		" ulcerative colitis, rectal biopsies of patients with active untreated" + 
		" disease, individuals with positive and negative sigmoidoscopic" + 
		" findings treated with salicylazosulfapyridine, (+-)-isomer of equol" + 
		" and 6-mercaptopurine, alone and in combinations and noncolitis" + 
		" controls were compared histologically. Predominant histological" + 
		" observations were analyzed statistically. There were fewer crypt" + 
		" abscesses but more mucosal edema after all forms of therapy." + 
		" Quantitative histopathological analysis failed to demonstrate that" + 
		" the response to one drug was significantly different from another.";

		Sentence sentence = new Sentence("12", "23", sentenceText.toLowerCase());

		tokenizer.tokenize(sentence);
		dictionary.tag(sentence);
		System.out.println(sentence.getText());
		System.out.println(sentence.getMentions().size());
		for (Mention mention : sentence.getMentions()) {
			System.out.println(mention.getText() + "\t" + mention.getConceptId());
			System.out.println(sentenceText.substring(mention.getStartChar(), mention.getEndChar()));
		}
	}

}

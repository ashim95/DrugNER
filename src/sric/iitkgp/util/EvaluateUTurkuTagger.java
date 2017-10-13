package sric.iitkgp.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EvaluateUTurkuTagger {
	public static final String PATH_TO_DDI_CORPUS = "data/final_test_set/";
	public static final String PATH_TO_TAGGED_FILE = "data/DDI13-test-result-pred.xml";

	public static HashMap<String, Element> sentenceElements;

	public static HashMap<String, Element> gt_sentence_elements = new HashMap<String, Element>();

	public static float tp = 0;

	public static float fp = 0;

	public static float fn = 0;

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		File folder = new File(PATH_TO_DDI_CORPUS);
		File[] listOfFiles = folder.listFiles();

		File taggedFile = new File(PATH_TO_TAGGED_FILE);
		// evaluateFile(listOfFiles[0], taggedFile);
		for (File f : listOfFiles) {
			evaluateFile(f);
		}

		loadAllSentences(taggedFile);
		System.out.println(sentenceElements.size());
		System.out.println(gt_sentence_elements.size());
		calculateMetric();
		System.out.println(tp + "\t" + fp + "\t" + fn);
		System.out.println((tp / (tp + fp)));
		System.out.println((tp / (tp + fn)));
	}

	public static void evaluateFile(File groundTruth) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(groundTruth);
		doc.getDocumentElement().normalize();

		Element root = doc.getDocumentElement();
		NodeList sentences = root.getElementsByTagName("sentence");

		for (int i = 0; i < sentences.getLength(); i++) {
			Node sent = sentences.item(i);
			if (sent.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) sent;

				String sent_id = element.getAttribute("id");

				gt_sentence_elements.put(sent_id, element);

			}
		}

	}

	public static void calculateMetric() {
		int count = 0;
		Set<String> sent_ids = gt_sentence_elements.keySet();
		HashMap<String, Boolean> tagged_bol = new HashMap<String, Boolean>();
		HashMap<String, Boolean> gt_bol = new HashMap<String, Boolean>();

		for (String id : sent_ids) {
			Element sent_gt = gt_sentence_elements.get(id);
			Element sent_ta = sentenceElements.get(id);
			if (sent_ta == null) {
				gt_bol.put(id, true);
				continue;
			}
			tagged_bol.put(id, true);

			HashSet<String> gt = new HashSet<String>();
			HashSet<String> ta = new HashSet<String>();

			if (sent_gt.getElementsByTagName("entity") != null) {
				NodeList entities_gt = sent_gt.getElementsByTagName("entity");
				for (int j = 0; j < entities_gt.getLength(); j++) {

					Element ent = (Element) entities_gt.item(j);
					if ("drug".equals(ent.getAttribute("type"))) {
//					if (("drug".equals(ent.getAttribute("type")) || ("brand".equals(ent.getAttribute("type"))))) {
						try {
							String[] off_all = ent.getAttribute("charOffset").split(";");
							for (String str : off_all) {
								String[] off = str.split("-");
								gt.add(off[0] + "-" + (Integer.parseInt(off[1]) + 1));

							}
						} catch (Exception e) {
							System.out.println(ent.getAttribute("charOffset"));
							throw e;
							// TODO: handle exception
						}
					}
				}
			}

			if (sent_ta.getElementsByTagName("entity") != null) {
				NodeList entities_ta = sent_ta.getElementsByTagName("entity");
				count += entities_ta.getLength();
				for (int j = 0; j < entities_ta.getLength(); j++) {
					Element ent = (Element) entities_ta.item(j);
					if ("drug".equals(ent.getAttribute("type"))) {
//					if (("drug".equals(ent.getAttribute("type")) || ("brand".equals(ent.getAttribute("type"))))) {
						String[] off_all = ent.getAttribute("charOffset").split(";");
						for (String str : off_all) {
							ta.add(str);

						}
					}
				}
			}
			Object[] gt_array = gt.toArray();
			// String[] ta_array = (String[]) gt.toArray();
			for (int i = 0; i < gt_array.length; i++) {
				String curr = (String) gt_array[i];
				if (ta.contains(curr)) {
					// System.out.println(curr);
					tp++;
					ta.remove(curr);
					gt.remove(curr);
				}
			}
			if (!gt.isEmpty())
				fn = fn + gt.size();
			if (!ta.isEmpty())
				fp += ta.size();

		}

		for (String id : gt_bol.keySet()) {
			Element sent_gt = gt_sentence_elements.get(id);
			if (sent_gt.getElementsByTagName("entity") != null) {
				NodeList entities_gt = sent_gt.getElementsByTagName("entity");
				for (int j = 0; j < entities_gt.getLength(); j++) {

					Element ent = (Element) entities_gt.item(j);
					if ("drug".equals(ent.getAttribute("type"))) {
						String[] off_all = ent.getAttribute("charOffset").split(";");
						fn += off_all.length;
					}
				}

			}
		}
		for (String id : sentenceElements.keySet()) {
			if (tagged_bol.containsKey(id))
				continue;
			Element sent_gt = sentenceElements.get(id);
			if (sent_gt.getElementsByTagName("entity") != null) {
				NodeList entities_gt = sent_gt.getElementsByTagName("entity");
				count += entities_gt.getLength();
				for (int j = 0; j < entities_gt.getLength(); j++) {
					Element ent = (Element) entities_gt.item(j);
					if ("drug".equals(ent.getAttribute("type"))) {
						String[] off_all = ent.getAttribute("charOffset").split(";");
						fp += off_all.length;
					}
				}
			}
		}
		System.out.println(count);
	}

	public static void loadAllSentences(File taggedFile)
			throws SAXException, IOException, ParserConfigurationException {

		sentenceElements = new HashMap<String, Element>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(taggedFile);

		doc.getDocumentElement().normalize();

		Element root = doc.getDocumentElement();

		NodeList documents = root.getElementsByTagName("document");

		for (int i = 0; i < documents.getLength(); i++) {
			Node d = documents.item(i);

			if (d.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) d;

				String d_id = element.getAttribute("id");

				if (d_id.contains("DrugBank")) {
					// if (d_id.matches("^[^(DrugBank)]+$")) {
					// System.out.println(d_id.matches("^[^(DrugBank)]+$"));
					continue;
				}
				NodeList sentences = element.getElementsByTagName("sentence");
				for (int j = 0; j < sentences.getLength(); j++) {
					Node sent = sentences.item(j);
					if (sent.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) sent;

						String sent_id = e.getAttribute("id");
						sentenceElements.put(sent_id, e);
					}
				}

			}
		}
	}

}

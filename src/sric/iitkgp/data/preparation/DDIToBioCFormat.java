package sric.iitkgp.data.preparation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCPassage;
import bioc.io.BioCCollectionWriter;
import bioc.io.standard.BioCFactoryImpl;

public class DDIToBioCFormat {

	public static final String PATH_TO_DDI_CORPUS = "data/test_set/";
	private static final String XML_FILE_NAME = "resources/ddiDataInBioC_test2.xml";

	public static void main(String[] args)
			throws ParserConfigurationException, SAXException, IOException, XMLStreamException {
		File folder = new File(PATH_TO_DDI_CORPUS);
		File[] listOfFiles = folder.listFiles();

		List<BioCDocument> bioCDocList = new ArrayList<BioCDocument>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getName();
				System.out.println("File : " + fileName);
				BioCDocument doc = getDataAsBioCDocuments(listOfFiles[i]);
				// break;
				if (doc != null) {
					bioCDocList.add(doc);
				}
			}
		}
		System.out.println("Total Docs : " + bioCDocList.size());
		writeToXMLFile(bioCDocList);

	}

	public static BioCDocument getDataAsBioCDocuments(File xmlFile)
			throws ParserConfigurationException, SAXException, IOException {

		// File xmlFile = new File(PATH_TO_DDI_CORPUS + fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(xmlFile);

		doc.getDocumentElement().normalize();

		String docId = doc.getDocumentElement().getAttribute("id");
		String text = "";
		int offset = 0;

		BioCDocument bioCdoc = new BioCDocument();
		bioCdoc.setID(docId);

		BioCPassage passage = new BioCPassage();
		passage.putInfon("type", "abstract");
		passage.setOffset(0);

		Element root = doc.getDocumentElement();
		NodeList sentences = root.getElementsByTagName("sentence");

		int len = sentences.getLength();
		for (int i = 0; i < sentences.getLength(); i++) {
			Node sent = sentences.item(i);
			if (sent.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) sent;
				text += element.getAttribute("text");

				if (element.getElementsByTagName("entity") != null) {
					NodeList entities = element.getElementsByTagName("entity");
					for (int j = 0; j < entities.getLength(); j++) {
						Element ent = (Element) entities.item(j);
						try {
							// if("drug".equals(ent.getAttribute("type"))) {
							if (("drug".equals(ent.getAttribute("type"))
									|| ("brand".equals(ent.getAttribute("type"))))) {
								BioCAnnotation anno = new BioCAnnotation();
								anno.setID(ent.getAttribute("id"));
								anno.setText(ent.getAttribute("text"));
								anno.putInfon("type", "drug");
								String[] off = ent.getAttribute("charOffset").split("-");
								anno.addLocation(new BioCLocation(Integer.parseInt(off[0]) + offset,
										Integer.parseInt(off[1]) - Integer.parseInt(off[0]) + 1));

								passage.addAnnotation(anno);
							}
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println(e);
							System.out.println("Continuing ...");
							continue;

						}
					}
				}
				offset += element.getAttribute("text").length();

			}

		}

		passage.setText(text);
		bioCdoc.addPassage(passage);
		// System.out.println(text);

		// System.out.println(passage.toString());

		// for (BioCAnnotation ann : bioCdoc.getPassage(0).getAnnotations()) {
		// System.out.println(ann.getText());
		// int offs = ann.getLocations().get(0).getOffset();
		// int leng = ann.getLocations().get(0).getLength();
		// System.out.println(passage.getText().substring(offs, offs + leng));
		// }

		return bioCdoc;
	}

	public static void writeToXMLFile(List<BioCDocument> documents) throws XMLStreamException, IOException {

		BioCCollection collection = new BioCCollection();
		collection.setDate("23/09/2017");
		collection.setSource("dataFromDDI");
		collection.setKey("2");
		collection.setDocuments(documents);

		BioCCollectionWriter bioCWriter = new BioCFactoryImpl()
				.createBioCCollectionWriter(new FileWriter(new File(XML_FILE_NAME)));
		System.out.println("Writing documents to xml file ...");
		bioCWriter.writeCollection(collection);
		bioCWriter.close();
		System.out.println("Writing Finished. Please check the file !!");
		return;
	}
}

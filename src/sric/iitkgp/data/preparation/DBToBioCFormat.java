package sric.iitkgp.data.preparation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCPassage;
import bioc.io.BioCCollectionWriter;
import bioc.io.standard.BioCFactoryImpl;

public class DBToBioCFormat {
	
	private Map<String, BioCDocument> bioCDocMap;
	
	
	public DBToBioCFormat() {
		this.bioCDocMap = new HashMap<String, BioCDocument>();
	}

	private static final String DATABASE_DRUGS_NAME = "drugs";
	private static final String XML_FILE_NAME = "resources/dataInBioC_ALL_4.xml";
	
	public static void main(String[] args) throws XMLStreamException, IOException {
		
		long startTime = System.nanoTime();
		
		Connection conn = MySqlConnect.makeConnection(DATABASE_DRUGS_NAME);
		
		DBToBioCFormat converter = new DBToBioCFormat();
		converter.getDataAsBioCDocuments(conn);
		List<BioCDocument> docs = new ArrayList<BioCDocument>(converter.bioCDocMap.values());
		
		System.out.println("Total Documents to write: " + docs.size());
		
		converter.writeToXMLFile(docs);
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000000;
		System.out.println("Execution of the program finished in : " + duration + " seconds");
		
	}
	
	public void getDataAsBioCDocuments(Connection conn){
		
		long startTime = System.nanoTime();
		
		Integer count = 0;
		
//		List<BioCDocument> bioCDocumentList = new ArrayList<BioCDocument>();
		System.out.println("Quering from the database ... ");
		try {
			Statement stmt = conn.createStatement();
			
			String query = "select a.pmid, a.value, f.id, f.rxcui, f.rxaui, f.name, f.original_text, f.start, f.end"
					+ " from medline.abstract a "
					+ "INNER JOIN (select * from drug_names b where pmid in  "
					+ "( select pmid from ( select * from (select pmid, count(*) as cnt "
					+ "from drug_names c group by pmid order by cnt desc) d "
					+ "where cnt > 4 ) e) ) f where a.pmid = f.pmid";
			
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				count++;
				
				if(count %1000 == 0) {
					System.out.println("Records Processed: " + count);
				}
				
				String pmid = String.valueOf(rs.getLong("pmid"));
				
				if(this.bioCDocMap.containsKey(pmid)) {
					BioCPassage passage = this.bioCDocMap.get(pmid).getPassage(0);
					BioCAnnotation annotation = new BioCAnnotation();
					annotation.setID(String.valueOf(rs.getLong("id")));
					annotation.setText(rs.getString("original_text"));
					annotation.putInfon("name", rs.getString("name"));
					annotation.putInfon("type", "drug");
					annotation.putInfon("rxcui", rs.getString("rxcui"));
					annotation.putInfon("rxaui", rs.getString("rxaui"));
					annotation.addLocation(
							new BioCLocation(rs.getInt("start"),rs.getInt("end") -rs.getInt("start")));
					
					passage.addAnnotation(annotation);
					continue;
				}
				
				BioCDocument bioCDocument = new BioCDocument();
				bioCDocument.setID(pmid);
				
				BioCPassage passage = new BioCPassage();
				
				passage.setText(rs.getString("value"));
				passage.putInfon("type", "abstract");
				passage.setOffset(0);
				
				BioCAnnotation annotation = new BioCAnnotation();
				annotation.setID(String.valueOf(rs.getLong("id")));
				annotation.setText(rs.getString("original_text"));
				annotation.putInfon("name", rs.getString("name"));
				annotation.putInfon("type", "drug");
				annotation.putInfon("rxcui", rs.getString("rxcui"));
				annotation.putInfon("rxaui", rs.getString("rxaui"));
				annotation.addLocation(
						new BioCLocation(rs.getInt("start"),rs.getInt("end") -rs.getInt("start")));
				
				passage.addAnnotation(annotation); 
				
				bioCDocument.addPassage(passage);
				this.bioCDocMap.put(pmid,  bioCDocument);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000000;
		System.out.println("Time taken to fetch records : " + duration + " seconds");
		
		return ;
		
	}
	
	public void writeToXMLFile(List<BioCDocument> documents) throws XMLStreamException, IOException {
		
		BioCCollection collection = new BioCCollection();
		collection.setDate("12/09/2017");
		collection.setSource("dataFromDB");
		collection.setKey("1");
		collection.setDocuments(documents);
		
		BioCCollectionWriter bioCWriter = new BioCFactoryImpl().createBioCCollectionWriter(new FileWriter(new File(XML_FILE_NAME)));
		System.out.println("Writing documents to xml file ...");
		bioCWriter.writeCollection(collection);
		bioCWriter.close();
		System.out.println("Writing Finished. Please check the file !!");
		return;
	}
}

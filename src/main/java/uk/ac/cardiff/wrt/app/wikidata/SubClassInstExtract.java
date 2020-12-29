/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app.wikidata;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class SubClassInstExtract implements EntityDocumentProcessor {
    
    private final Logger LOG = LoggerFactory.getLogger(SubClassInstExtract.class);
    
    static final String SUBCLASSE = "P279"; // "subclass of"
    
    static final String INSTANCE = "P31"; // "instance of"
    
    int itemsInstCount = 0;
    
    int allItemCount = 0;
      
    final Map<Value,String> entityLabels  = new HashMap<>(); 
    
    final Map<Value, List<String>> result = new HashMap<>(); 
    
    public SubClassInstExtract(Map<String,String> entities) {
         entities.entrySet().forEach(entry->{
            Value value = Datamodel.makeWikidataItemIdValue(entry.getKey());
            result.put(value, new ArrayList<>());
            entityLabels.put(value,entry.getValue());
         });
    }

    @Override
    public void processItemDocument(ItemDocument itemDocument) {
	this.allItemCount++;
        if(itemDocument.getSiteLinks().containsKey("enwiki")){
            itemDocument.getStatementGroups().forEach((sg) -> {
                    boolean isInstanceOf = "P31".equals(sg.getProperty().getId());
                    if (isInstanceOf) {
                        for (Statement s : sg) {
                            if (result.containsKey(s.getValue())){
                                            itemsInstCount++;
                                            result.get(s.getValue()).add(itemDocument.getEntityId().getId());
                                        }
                                    }
                                }
                           });
        }
        if (this.allItemCount % 100000== 0) {
			 LOG.info(printLog());
	}
    }

	/**
	 * Escapes a string for use in CSV. In particular, the string is quoted and
	 * quotation marks are escaped.
	 *
	 * @param string
	 *            the string to escape
	 * @return the escaped string
	 */
	private String csvEscape(String string) {
		if (string == null) {
			return "\"\"";
		} else {
			return "\"" + string.replace("\"", "\"\"") + "\"";
		}
	}
    
        public String printLog() {
                  String s = "Found " + this.itemsInstCount
                                    + " matching instances"
                                    + " after scanning " + this.allItemCount
                                    + " items.";
                  return s;
                        
        }
    
        public void printStatus() {
                    System.out.println("Found " + this.itemsInstCount
                                    + " matching instances"
                                    + " after scanning " + this.allItemCount
                                    + " items.");
        }
    
    	public void close() {
		printStatus();
		
	}
        
        
        public void writeRecordedData(String resultPath) {
		try (PrintStream out = new PrintStream(
		     MyHelpers.openExampleFileOuputStream(resultPath ,"subClassInst.csv"))) {
		     out.println("SubclassID" + ", SubclassLabel"+ " ,NumberOfInstance"  + ",InstancesID" );
                     
                     result.entrySet().forEach(p->{
                         if(p.getValue().size()>0){
                         out.print(p.getKey()); 
                         out.print(",");
                         out.print(csvEscape(entityLabels.get(p.getKey())));    
                         out.print(",");
                         out.print(csvEscape(String.valueOf(p.getValue().size())));
                         out.print(",");
                         String s = p.getValue().stream().collect(Collectors.joining("@"));
                         out.print(csvEscape(s));
                         out.println();
                     
                         }
                     });
                     out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
                
	}
        
        
        
        
}

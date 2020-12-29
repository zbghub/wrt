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
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class SubClassInstExtract1 implements EntityDocumentProcessor {
    
    static final String SUBCLASSE = "P279"; // "subclass of"
    
    static final String INSTANCE = "P31"; // "instance of"
    
    final Value entityValue; 

    int itemsInstCount = 0;
    
    int itemsClsCount = 0;
    
    int allItemCount = 0;
      
    final List<ItemDocument> recordInstance = new ArrayList<>(); 
    
    final Map<ItemIdValue, ItemDocument> recordSubclass = new HashMap<>(); 
    

    public SubClassInstExtract1(String entityValueName) {
        this.entityValue =  Datamodel.makeWikidataItemIdValue(entityValueName); 
    }
    
    
    @Override
    public void processItemDocument(ItemDocument itemDocument) {
	this.allItemCount++;
        
	// Check if the item matches our filter conditions:
	if (itemDocument.hasStatementValue(INSTANCE, entityValue)) {
                 recordInstance.add(itemDocument);
        
                 
                 
        
        if (itemDocument.hasStatementValue(SUBCLASSE, entityValue) ) {
           
               recordSubclass.put(itemDocument.getEntityId(), itemDocument);
            
        }
        }
//        // Print progress every 100,000 items:
        if (this.allItemCount % 100000== 0) {
			printStatus();
	}
    }

    
    private Map<ItemDocument, List<ItemDocument>>  preapareResult(){
         Map<ItemDocument, List<ItemDocument>> res = new HashMap<>();
         recordSubclass.entrySet().forEach(cls->{
            res.put(cls.getValue(), new ArrayList<>());
         });
         recordInstance.forEach(instDocument->{
                 if(instDocument.getSiteLinks().containsKey("enwiki")){
                     if (!instDocument.getStatementGroups().isEmpty()) {
                          instDocument.getStatementGroups().forEach((sg) -> {
                               boolean isInstanceOf = "P31".equals(sg.getProperty().getId());
                               if (isInstanceOf) {
                                   for (Statement s : sg) {
                                          if (recordSubclass.keySet().contains((ItemIdValue) s.getValue())){
                                                ItemDocument clsDocument = recordSubclass.get((ItemIdValue) s.getValue()) ;
                                                res.get(clsDocument).add(instDocument);
                                          }
                                    }
                                }
                           });
                     }
                 }
         });
      return res;
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
    
    
            public void printStatus() {
                    System.out.println("Found " + this.recordInstance.size()
                                    + " matching instances"
                                    + " and " + this.recordSubclass.size()
                                    + " matching subclasses"
                                    + " after scanning " + this.allItemCount
                                    + " items.");
            }
    
    	public void close() {
		printStatus();
		
	}
        
        
        public void writeRecordedData(String resultPath) {
                Map<ItemDocument, List<ItemDocument>> res = preapareResult();
		try (PrintStream out = new PrintStream(
		     MyHelpers.openExampleFileOuputStream(resultPath ,"subClassInst.csv"))) {
		     out.println("Subclass ID" + ", Subclass Label"+ " ,occurance"  + ",Instances ID" );
                     res.entrySet().forEach(p->{
                         out.print(p.getKey().getEntityId().getId()); 
                         out.print(",");
                         out.print(csvEscape(p.getKey().findLabel("en")));    
                         out.print(",");
                         out.print(csvEscape(String.valueOf(p.getValue().size())));
                         out.print(",");
                         String s = p.getValue().stream().map(v -> v.getEntityId().getId()).collect(Collectors.joining("@"));
                         out.print(csvEscape(s));
                         out.println();
                     });
                     out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
                
	}
        
        
        
        
}

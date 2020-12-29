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
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import uk.ac.cardiff.wrt.util.MyHelpers;

public class TripleExtract implements EntityDocumentProcessor {
    
    static final String INSTANCE_ID = "P31"; // "instance of"

    final Value entityValue; 
    
    PrintStream out;

    int itemsCount = 0;
    
    int allItemCount = 0;
      
    final HashMap<EntityIdValue, ItemDocument> itemDoc = new HashMap<>();
    
    final HashMap<PropertyIdValue, PropertyDocument> propertyDoc = new HashMap<>();
    

    public TripleExtract(String resultPath, String entityValueName) throws IOException {
        this.entityValue =  Datamodel.makeWikidataItemIdValue(entityValueName); 
        out = new PrintStream(MyHelpers.openExampleFileOuputStream(resultPath, "triples.csv"));
        out.println("Head, Relation, Tail"); 
    }
    
    
    @Override
    public void processItemDocument(ItemDocument itemDocument) {
	this.allItemCount++;
	// Check if the item matches our filter conditions:
	if (!itemDocument.hasStatementValue(INSTANCE_ID,  entityValue)) {
	    return;
	}
        
        itemDocument.getStatementGroups().forEach((sg) -> {
            boolean isInstanceOf = "P31".equals(sg.getProperty().getId());
            if (! isInstanceOf) {
                PropertyIdValue prop = sg.getProperty();
                sg.forEach((s) -> {
                    EntityIdValue subject = s.getSubject();
                    if (s.getValue() instanceof EntityIdValue) {
                      
                        this.itemsCount++;
                        out.print(subject.getId());
                        out.print(",");
                        out.print(csvEscape(prop.getId()));
                        out.print(",");
                        out.print(csvEscape( ((EntityIdValue) s.getValue()).getId() ));
                        out.println();   
                    }
                });
            }
        });
 
//        // Print progress every 100,000 items:
//	    if (this.allItemCount % 100000 == 0) {
//			printStatus();
//	    }
	}
    
        @Override
        public void processPropertyDocument(PropertyDocument propertyDocument) {
		propertyDoc.put(propertyDocument.getEntityId(), propertyDocument);
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
                    System.out.println("Found " + this.itemsCount
                                    + " matching items after scanning " + this.allItemCount
                                    + " items.");
            }
    
    	public void close() {
		printStatus();
		this.out.close();
	}
}

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
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

import org.wikidata.wdtk.datamodel.interfaces.Value;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class InstanceExtract implements EntityDocumentProcessor {
    
    static final String INSTANCE = "P301"; // "instance of"
    
    final Value entityValue; 
    
    PrintStream out;

    int itemsCount = 0;
    
    int allItemCount = 0;
      
    final HashMap<EntityIdValue, List<EntityIdValue>> recordInstance = new HashMap<>(); 
    
    final List<EntityIdValue> subclasses = new ArrayList<>();
    
    public InstanceExtract(String resultPath, String entityValueName) throws IOException {
        this.entityValue =  Datamodel.makeWikidataItemIdValue(entityValueName); 
        out = new PrintStream(MyHelpers.openExampleFileOuputStream(resultPath, "instances.csv"));
        out.println("ID, Label, PageTitle"); 
    }
    
    
    @Override
    public void processItemDocument(ItemDocument itemDocument) {
	this.allItemCount++;
	// Check if the item matches our filter conditions:
	if (itemDocument.hasStatementValue(INSTANCE, entityValue) ) {
            
        
           if(itemDocument.getSiteLinks().containsKey("enwiki")){
              System.out.println("yep...");
            this.itemsCount++;
            SiteLink enwiki = itemDocument.getSiteLinks().get("enwiki");
            out.print(itemDocument.getEntityId().getId());
	    out.print(",");
	    out.print(csvEscape(itemDocument.findLabel("en")));
	    out.print(",");
	    out.print(csvEscape(enwiki.getPageTitle()));
			out.println();
            
            }  
        }
        // Print progress every 100,000 items:
	if (this.allItemCount % 100000== 0) {
			printStatus();
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app.wikidata2;

import java.io.IOException;
import java.io.PrintStream;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;



public class InstanceExtraction implements EntityDocumentProcessor {
    
    static final String extractPropertyId = "P227"; // "GND identifier"
    
    static final Value filterValue = Datamodel.makeWikidataItemIdValue("Q5"); // "human"
    
    static final String filterPropertyId = "P31"; // "instance of"
    
    
    PrintStream out;

    int itemsCount = 0;
    
    int allItemCount = 0;
        
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws IOException {
        MyExampleHelpers.configureLogging();
        InstanceExtraction processor = new InstanceExtraction();
        MyExampleHelpers.processEntitiesFromWikidataDump(processor);
        processor.close();
    }
    
    public InstanceExtraction() throws IOException {
	// open file for writing results: 
        out = new PrintStream(MyExampleHelpers.openExampleFileOuputStream("extracted-data.csv"));
	// write CSV header:
	out.println("ID, Label, Wikipedia"); 
    }
    
    @Override
    public void processItemDocument(ItemDocument itemDocument) {
	this.allItemCount++;

	// Check if the item matches our filter conditions:
	if (!itemDocument.hasStatementValue(filterPropertyId, filterValue)) {
	    return;
	}
        
        // Find the first value for this property, if any:
        StringValue stringValue = itemDocument
				.findStatementStringValue(extractPropertyId);
        
	// Find the english link, if any
	SiteLink siteLink = itemDocument
				.getSiteLinks().get("enwiki");

        if(siteLink != null && stringValue != null){       
                this.itemsCount++;
                out.print(itemDocument.getEntityId().getId());
                out.print(",");
                out.print(csvEscape(itemDocument.findLabel("en")));
                out.print(",");
                out.print(csvEscape(siteLink.getPageTitle()));
                out.println();
        }



        // Print progress every 100,000 items:
	    if (this.allItemCount % 100000 == 0) {
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

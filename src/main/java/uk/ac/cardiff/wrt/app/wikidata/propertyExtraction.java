/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app.wikidata;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class propertyExtraction implements EntityDocumentProcessor {
         
    static final String INSTANCE_ID = "P31"; // "instance of"
    
    static final String SUBCLASS_ID = "P279"; // "subclass of"
    
    final Value entityValue; 
    
    int countItems = 0;
   
    int countProperties = 0;
  
    
    final Map<PropertyIdValue, List<EntityIdValue>> propertyCoCounts = new HashMap<>();
    
    final HashMap<EntityIdValue, ItemDocument> itemDoc = new HashMap<>();
    
    final HashMap<PropertyIdValue, PropertyDocument> propertyDoc = new HashMap<>();
    
   
    public propertyExtraction(String entityValueName) {
        this.entityValue =  Datamodel.makeWikidataItemIdValue(entityValueName); 
    }

    @Override
    public void processItemDocument(ItemDocument itemDocument) {
	this.countItems++;
        if (!itemDocument.hasStatementValue(INSTANCE_ID,  entityValue)) {
	       return;
        }

        if (!itemDocument.getStatementGroups().isEmpty()) {
            itemDocument.getStatementGroups().forEach((sg) -> {
                PropertyIdValue property = sg.getProperty();
                for (Statement s : sg) {
                    Value value = s.getValue();
                     if (value instanceof EntityIdValue) {
                         itemDoc.put((EntityIdValue) value, itemDocument);
                         countCooccurringProperties(property, (EntityIdValue) value);
                     }
                }
            });
        }
        
        if (this.countItems % 1000 == 0) {
                  printStatus();
                  writePropertyData("/Users/ziedbouraoui/Experiments/data4rana");
        }
    }
    
    @Override
    public void processPropertyDocument(PropertyDocument propertyDocument) {
		this.countProperties++;
		propertyDoc.put(propertyDocument.getEntityId(), propertyDocument);
    }
    
    
    
    private void countCooccurringProperties(PropertyIdValue property, EntityIdValue value) {
            if(propertyCoCounts.containsKey(property)){
                propertyCoCounts.get(property).add(value);
            }else{
              List<EntityIdValue> l = new ArrayList<>();
              l.add(value);
              propertyCoCounts.put(property, l);
            }
    }
    
    public void printStatus() {
        System.out.println("Found " + this.propertyCoCounts.size()
                                    + " matching items after scanning " + this.countItems
                                    + " items.");
    }
    
    public void writePropertyData(String resultPath) {
		try (PrintStream out = new PrintStream(
		     MyHelpers.openExampleFileOuputStream(resultPath ,"properties.csv"))) {
		     out.println("Relation" + ",tail" + ",occurance" + ", Instances");
                     propertyCoCounts.entrySet().forEach(p->{
                            p.getValue().stream().distinct().forEach(v->{
                                    int o =  Collections.frequency(p.getValue(), v);
                                    PropertyIdValue prop = p.getKey();
                                    if(propertyDoc.containsKey(prop)){
                                         out.print(propertyDoc.get(prop).findLabel("en"));
                                    }
                                    out.print(",");
                                    out.print(csvEscape(itemDoc.get(v).findLabel("en")));
                                    out.print(",");
                                    out.print(csvEscape(String.valueOf(o)));
                                    out.print(",");
//                                    String s = p.getValue().stream().map(v -> v.getEntityId().getId()).collect(Collectors.joining("@"));
                                    
                                    
                                    
                                    out.println();

//                                  out.print(p.getKey().getIri() + "," + v + "," + o );
//                                    out.println();
                        });
                    });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    
    	private String csvEscape(String string) {
		if (string == null) {
			return "\"\"";
		} else {
			return "\"" + string.replace("\"", "\"\"") + "\"";
		}
	}

}

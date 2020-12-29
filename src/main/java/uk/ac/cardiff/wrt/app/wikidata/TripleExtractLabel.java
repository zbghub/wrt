/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app.wikidata;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class TripleExtractLabel implements EntityDocumentProcessor {
    
    static final String INSTANCE_ID = "P31"; // "instance of"

    final Value entityValue; 
    
    final Map<EntityIdValue, ItemDocument> itemDoc = new HashMap<>();
    
    final Map<PropertyIdValue, PropertyDocument> propertyDoc = new HashMap<>();
    
    final Set<String> wikiId = new HashSet<>();
    
    final List<String> records = new ArrayList<>();
    
    public TripleExtractLabel(String idListPath, String entityValueName) throws IOException {
        this.entityValue =  Datamodel.makeWikidataItemIdValue(entityValueName); 
        initSet( idListPath);
    }
    
    
    @Override
    public void processItemDocument(ItemDocument itemDocument) {
        if(wikiId.contains(itemDocument.getEntityId().getId())){
             records.add(itemDocument.getEntityId().getId() +","+ itemDocument.findLabel("en"));
        }    
    }
    
    @Override
    public void processPropertyDocument(PropertyDocument propertyDocument) {
         if(wikiId.contains(propertyDocument.getEntityId().getId())){
             records.add(propertyDocument.getEntityId().getId() +","+ propertyDocument.findLabel("en"));
         }
    }
    
    
    

    private void initSet(String idListPath) throws IOException {
       try (Stream<String> lines = Files.lines(Paths.get( idListPath ))) {
              lines
                   .forEach(line->{
                       if(!line.isEmpty()){
                       String[] split = line.trim().split(",");      
                       wikiId.addAll(Arrays.asList(split));
                       }
              });
       }
    }
    
    public void writeResult(String resultPath) {
        try (PrintStream out = new PrintStream(
            MyHelpers.openExampleFileOuputStream(resultPath,"label.csv"))) {
            out.println("WikiId,Label");
            records.forEach(r->{
               out.println(r);
            });
        } catch (IOException e) {
	    e.printStackTrace();
           }
	}
}

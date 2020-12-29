/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app.defaultsources;

import com.beust.jcommander.Parameter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import uk.ac.cardiff.wrt.app.wikidata.SubClassInstExtract;
import uk.ac.cardiff.wrt.util.MyHelpers;



public class TestSubClasseInstExtract {

    public static String INPUT_WIKIDATA_OPTION = "/Users/ziedbouraoui/Flexilog/wikidata/wikidata-20180820-all.json.gz";
    
    public static String INPUT_ENTITY_OPTION ="/Users/ziedbouraoui/Experiments/data4rana/query.csv"; 
    
    public static String OUTPUT_RESULT_OPTION ="/Users/ziedbouraoui/Experiments/data4rana/Q41176";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        final String logPath = "file:/Users/ziedbouraoui/Experiments/wikidata/file.log";      
        System.setProperty("log4j", logPath);
        MyHelpers.configureLogging();
         
        // read cvs
        
        Map<String,String> entities = new HashMap<>();
               

        String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_ENTITY_OPTION))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] record = line.split(cvsSplitBy);
                entities.put(record[0], record[1]);
            }} catch (IOException e) {
            e.printStackTrace();
            }
      
        
        
        
        
        SubClassInstExtract processor = new   SubClassInstExtract(entities);
        MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
        processor.writeRecordedData(OUTPUT_RESULT_OPTION);

         
         
         
         
//            TripleExtract processor = new TripleExtract(OUTPUT_RESULT_OPTION, INPUT_ENTITY_OPTION);
//            MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
//            processor.close();

//
//            SubClassesExtract processor = new SubClassesExtract(OUTPUT_RESULT_OPTION, INPUT_ENTITY_OPTION);
//            MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
//            processor.close();

    }
    
    
    
    
}

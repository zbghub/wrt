/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app.defaultsources;

import com.beust.jcommander.Parameter;
import java.io.IOException;
import uk.ac.cardiff.wrt.app.wikidata.InstanceExtract;
import uk.ac.cardiff.wrt.app.wikidata.SubClassesExtract;
import uk.ac.cardiff.wrt.app.wikidata.TripleExtract;
import uk.ac.cardiff.wrt.app.wikidata.propertyExtraction;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class TestSubClasseExtract {

    public static String INPUT_WIKIDATA_OPTION = "/Users/ziedbouraoui/Flexilog/wikidata/wikidata-20180820-all.json.gz";
    
    public static String INPUT_ENTITY_OPTION ="Q5";
    
    public static String OUTPUT_RESULT_OPTION ="/Users/ziedbouraoui/Experiments/data4rana/Q5";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
         final String logPath = "file:/Users/ziedbouraoui/Experiments/wikidata/file.log";      
         System.setProperty("log4j", logPath);
         MyHelpers.configureLogging();
//         propertyExtraction processor = new  propertyExtraction(INPUT_ENTITY_OPTION);
//         MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
//         processor.writePropertyData(OUTPUT_RESULT_OPTION);

//            TripleExtract processor = new TripleExtract(OUTPUT_RESULT_OPTION, INPUT_ENTITY_OPTION);
//            MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
//            processor.close();


            SubClassesExtract processor = new SubClassesExtract(OUTPUT_RESULT_OPTION, INPUT_ENTITY_OPTION);
            MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
            processor.close();



    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app.defaultsources;

import com.beust.jcommander.Parameter;
import java.io.IOException;

import uk.ac.cardiff.wrt.app.wikidata.TripleExtract;
import uk.ac.cardiff.wrt.app.wikidata.TripleExtractLabel;
import uk.ac.cardiff.wrt.app.wikidata.propertyExtraction;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class TestTripleExtractLabel1 {

    public static String INPUT_WIKIDATA_OPTION = "/Users/ziedbouraoui/Flexilog/wikidata/wikidata-20180604-all.json.gz";
    
    public static String INPUT_ENTITY_OPTION ="Q215380";
    
    public static String OUTPUT_RESULT_OPTION ="/Users/ziedbouraoui/GoogleDrive/data_rana/Q43229_organisation";
    
    public static String INPUT_WIKIID_OPTION ="/Users/ziedbouraoui/GoogleDrive/data_rana/Q43229_organisation/triples.csv";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
         final String logPath = "file:/Users/ziedbouraoui/GoogleDrive/data_rana/Q215380_band/labelfile.log";      
         System.setProperty("log4j", logPath);
         MyHelpers.configureLogging();
//         propertyExtraction processor = new  propertyExtraction(INPUT_ENTITY_OPTION);
//         MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
//         processor.writePropertyData(OUTPUT_RESULT_OPTION);

            TripleExtractLabel processor = new TripleExtractLabel(INPUT_WIKIID_OPTION, INPUT_ENTITY_OPTION);
            MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
            processor.writeResult(OUTPUT_RESULT_OPTION);


//            SubClassInstanceExtract processor = new SubClassInstanceExtract(OUTPUT_RESULT_OPTION, INPUT_ENTITY_OPTION);
//            MyHelpers.processEntitiesFromWikidataDump(INPUT_WIKIDATA_OPTION, processor);
//            processor.close();



    }
    
}

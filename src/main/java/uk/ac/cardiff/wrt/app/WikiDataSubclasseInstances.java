/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.ac.cardiff.wrt.app.wikidata.SubClassInstExtract;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class WikiDataSubclasseInstances {

    
        @Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode.")
	boolean verbose = false;
            
        @Parameter(names = { "-h", "--help" },description = "Print this message.")
	boolean HELP_OPTION = false;
    
        @Parameter(names = { "-log", "--output-log" }, description = "Path where to output the log file, helpful when you work on ARCCA.", required = false)
	String OUTPUT_LOG_OPTION = null;
        
        @Parameter(names = { "-r", "--output-result" }, description = "Path where to output result, i.e the bag-of-words. ", required = false)
	String OUTPUT_RESULT_OPTION ="-";
        
        @Parameter(names = { "-w", "--input-wikipedia-path" },description = "Path to the wikepedia dump .xml (Bz2 file contains some errors)." , required =false)
	String INPUT_WIKIDATA_OPTION ="-";
        
        @Parameter(names = { "-e", "--input-wikipedia-entities-path" },description = "Path to the wikepedia entities to fetch)." , required =false)
	String INPUT_ENTITY_OPTION ="-";
 
          
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      WikiDataSubclasseInstances options = new WikiDataSubclasseInstances();
        
      final JCommander commander
      = JCommander.newBuilder()
      .programName("WikiPedia Page Extractor")
      .addObject(options)
      .build();
      
      commander.parse(args);
      
      if (options.HELP_OPTION) {
                commander.usage();
	        System.exit(0); 
       }
      
        String logPath = options.OUTPUT_LOG_OPTION;      
       System.setProperty("log4j", logPath);
       final Logger LOG = Logger.getLogger( WikiPediaPageExtractor.class );  
       LOG.info("Running " +WikiPediaPageExtractor.class.getCanonicalName() + " with args " + Arrays.toString(args));
       
       
        // read cvs
        Map<String,String> entities = new HashMap<>();
        String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(options.INPUT_ENTITY_OPTION))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] record = line.split(cvsSplitBy);
                entities.put(record[0], record[1]);
            }} catch (IOException e) {
            e.printStackTrace();
        }
      
       
        SubClassInstExtract processor = new   SubClassInstExtract(entities);
        MyHelpers.processEntitiesFromWikidataDump(options.INPUT_WIKIDATA_OPTION, processor);
        processor.writeRecordedData(options.OUTPUT_RESULT_OPTION);

       
       
       
    }
    
}

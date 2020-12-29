/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.IOException;
import org.apache.log4j.Logger;
import uk.ac.cardiff.wrt.app.wikidata.InstanceExtract;
import uk.ac.cardiff.wrt.util.MyHelpers;


public class WikiPropertyInstanceExtract {
    
        @Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode.")
	boolean verbose = false;
            
	@Parameter(names = { "-h", "--help" },description = "Print this message.")
	boolean HELP_OPTION = false;
    
        @Parameter(names = { "-log", "--output-log" }, description = "Path where to output the log file, helpful when you work on ARCCA.", required = false)
	String OUTPUT_LOG_OPTION = null;
        
        @Parameter(names = { "-w", "--input-wikidata-path" },description = "Path to the wikepdata multistream.xml (Bz2 not et)." , required =false)
	String INPUT_WIKIDATA_OPTION ="-";
                     
        @Parameter(names = { "-e", "--input-entity-name" },description = "the name of the entity" , required =false)
	String INPUT_ENTITY_OPTION ="-";
        
        @Parameter(names = { "-r", "--output-result" }, description = "Path where to output result, i.e the cvs file. ", required = false)
	String OUTPUT_RESULT_OPTION ="-";
        
//        @Parameter(names = { "-p", "--path-properties-dir" },description = "Path to properties directory ", required = false)
//	String INPUT_PROP_OPTION ="-";
    
        
        static final String filterPropertyId = "P31"; // "instance of"

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
      WikiPropertyInstanceExtract options = new WikiPropertyInstanceExtract();
      
      final JCommander commander
      = JCommander.newBuilder()
      .programName("WikiData Instance Extractor")
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
       
       MyHelpers.configureLogging();
      
       InstanceExtract processor = new InstanceExtract(options.OUTPUT_RESULT_OPTION, options.INPUT_ENTITY_OPTION);
       
       MyHelpers.processEntitiesFromWikidataDump(options.INPUT_WIKIDATA_OPTION, processor);
       
       processor.close();


    }
    
    
    
    
    


    
    
    
}

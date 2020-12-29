/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app;



import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import org.apache.log4j.Logger;
import uk.ac.cardiff.wrt.wikipedia.BagOfWords;
import uk.ac.cardiff.wrt.wikipedia.WikipediaParser;

public class WikiPediaPageExtractor {  
    
        @Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode.")
	boolean verbose = false;
            
	@Parameter(names = { "-h", "--help" },description = "Print this message.")
	boolean HELP_OPTION = false;
    
        @Parameter(names = { "-log", "--output-log" }, description = "Path where to output the log file, helpful when you work on ARCCA.", required = false)
	String OUTPUT_LOG_OPTION = null;
        
        @Parameter(names = { "-r", "--output-result" }, description = "Path where to output result, i.e the bag-of-words. ", required = false)
	String OUTPUT_RESULT_OPTION ="-";
        
        @Parameter(names = { "-w", "--input-wikipedia-path" },description = "Path to the wikepedia dump .xml (Bz2 file contains some errors)." , required =false)
	String INPUT_WIKIMEDIA_OPTION ="-";
                     
        //@Parameter(names = { "-l", "--input-pageslist-path" },description = "Input the list of pages that you want to extract, a cvs file." , required = false)
	//String INPUT_LIST_PAGE_OPTION ="-";
        
        @Parameter(names = { "-l", "--input-pageslist-path" },description = "Input the list of pages that you want to extract, a path with cvs file." , required = false)
	String INPUT_LIST_PAGE_OPTION ="-";
        
        @Parameter(names = { "-i", "--position" },description = "the position of page titles on the cvs file, by default 1", required = false)
	String INPUT_CVS_POSITION_OPTION ="1";
        
        @Parameter(names = { "-m", "--input-extract-mode" },description = "The extraction mode: default or 1 for raw text; 2 for cleaned text and 3 for Bag-of-word", required = false)
	String INPUT_MODE_OPTION ="1";
      
        @Parameter(names = { "-p", "--path-properties-dir" },description = "Path to properties directory ", required = false)
	String INPUT_PROP_OPTION ="-";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException  {
      WikiPediaPageExtractor options = new WikiPediaPageExtractor();
        
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
       //LOG.info("Running with mode = " + options.INPUT_MODE_OPTION + " and cvs position " + options.INPUT_CVS_POSITION_OPTION);
       if(options.INPUT_PROP_OPTION.equals("-")){
            LOG.info("Run with default properties : Text Length = " +  WikipediaParser.TEXT_LENGTH + " and nbr of occurrance = " + BagOfWords.NUMBER_OF_OCCURRENCE);
       }else{
           Properties prop = new Properties();
           String propFileName = "/param.properties"; 
           InputStream inputStream  = new FileInputStream(options.INPUT_PROP_OPTION+propFileName);
           prop.load(inputStream);
           String textLenght = prop.getProperty("textlength");
           WikipediaParser.setTextLenght(Integer.parseInt(textLenght));
           String occurrance = prop.getProperty("occurrance");
           BagOfWords.setNumberOfOccurrence(Integer.parseInt(occurrance));
           String word = prop.getProperty("word");
           BagOfWords.setNumberOfWords(Integer.parseInt(word));
           
           LOG.info("Running with the following Properies: Text Length = " +  WikipediaParser.TEXT_LENGTH + ",  nbr of occurrance = " + BagOfWords.NUMBER_OF_OCCURRENCE + ", and nbr of words = " + BagOfWords.NUMBER_OF_WORDS);
       }

       LOG.info(" Reading wikimedia xml from: " + options.INPUT_WIKIMEDIA_OPTION);
       LOG.info(" Reading list of pages to extract  from: " + options.INPUT_LIST_PAGE_OPTION);             
       LOG.info(" Preaparing output to: " + options.OUTPUT_RESULT_OPTION);     
       LOG.info(" run program ....");   
     
       WikipediaParser wp = new  WikipediaParser(options.INPUT_WIKIMEDIA_OPTION, options.INPUT_LIST_PAGE_OPTION, options.OUTPUT_RESULT_OPTION);
       wp.parse(Integer.parseInt( options.INPUT_MODE_OPTION), Integer.parseInt(options.INPUT_CVS_POSITION_OPTION) );

    }
    
}

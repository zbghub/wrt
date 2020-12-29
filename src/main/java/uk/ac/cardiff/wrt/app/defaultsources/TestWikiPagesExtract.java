/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.app.defaultsources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.xml.stream.XMLStreamException;
import uk.ac.cardiff.wrt.wikipedia.BagOfWords;
import uk.ac.cardiff.wrt.wikipedia.WikipediaParser;


public class TestWikiPagesExtract {

    /**
     * @param args the command line arguments
     */
    ///Users/ziedbouraoui
    public static String INPUT_WIKIMEDIA_OPTION = "/Users/ziedbouraoui/Flexilog/wikidata/enwiki-20180401-pages-articles-multistream.xml";
    public static String INPUT_LIST_PAGE_OPTION ="/Users/ziedbouraoui/Experiments/wikidata/Q41176-building/direct-instances.csv";
    public static  String OUTPUT_RESULT_OPTION ="/Users/ziedbouraoui/Experiments/wikidata/Q41176-building/direct-instances-pages/";
    public static  String INPUT_MODE_OPTION = "3";
    public static  String INPUT_CVS_POSITION_OPTION ="1";
 
    
    
    public static void main(String[] args) throws IOException, XMLStreamException {
        
        final String logPath = "file:/Users/ziedbouraoui/Experiments/wikidata/file.log";      
        System.setProperty("log4j", logPath);
        
        Properties prop = new Properties();
        String propFileName = "/param.properties"; 
        InputStream inputStream  = TestWikiPagesExtract.class.getResourceAsStream(propFileName);
        prop.load(inputStream);
        String textLenght = prop.getProperty("textlength");
        WikipediaParser.setTextLenght(Integer.parseInt(textLenght));
        
                String occurrance = prop.getProperty("occurance");
        BagOfWords.setNumberOfOccurrence(Integer.parseInt(occurrance));
       
        
        WikipediaParser wp = new  WikipediaParser(INPUT_WIKIMEDIA_OPTION, INPUT_LIST_PAGE_OPTION, OUTPUT_RESULT_OPTION);
       
       
//       wp.parse(Integer.parseInt(INPUT_MODE_OPTION), Integer.parseInt(INPUT_CVS_POSITION_OPTION ));
    }
    
}

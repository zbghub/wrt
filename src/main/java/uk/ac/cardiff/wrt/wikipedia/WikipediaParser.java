/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.wikipedia;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.log4j.Logger;
import uk.ac.cardiff.wrt.util.CodeUtil;


public class WikipediaParser {
 
    final Logger LOG = Logger.getLogger( WikipediaParser.class );
    
    private static final String ELEMENT_PAGE = "page";
   
    private static final String ELEMENT_TITLE = "title";
    
    private static final String ELEMENT_TXT = "text";
    
   public static int TEXT_LENGTH = 1000;


    private final String wikiMediaFile;
    private final String pagesList;
    
    private final String outputPath;
    
    PrintStream out;
    
    int count = 0;
    int pageCount = 0;
    
    public WikipediaParser(final String wikiMediaFile, final String pagesList, final  String outputPath) {
        this.wikiMediaFile = wikiMediaFile;
        this.pagesList = pagesList;
        this.outputPath =  outputPath; 
    }
        
    public void parse(int mode, int position)  {
        final List<String> pages2extract;
        try {
            pages2extract = getPagesList(pagesList, position);
            LOG.info(pages2extract.size() + " pages to extract.");
//          pages2extract.forEach(System.out::println);
            if(!pages2extract.isEmpty()){
//                InputStream in = new BZip2CompressorInputStream(new FileInputStream(wikiMediaFile));
                XMLInputFactory factory = XMLInputFactory.newFactory();
                XMLStreamReader reader;    
                try {
                    reader = factory.createXMLStreamReader(new FileInputStream(wikiMediaFile));              
                    while (reader.hasNext()) {  
                       while(true) {
                          try{      
                             int event = reader.next();
                             if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals(ELEMENT_PAGE)) {
                                 this.count ++;
                                 parsePage(reader,pages2extract, mode);
                                 if (this.count % 100000 == 0) {
			             printStatus();
	                         }
                             }
                             break;
                           }catch(XMLStreamException e ){
                             reader.next();                         
//                             System.out.println("next1");
                           } 
                        }
                    
                    }
                } catch (XMLStreamException ex) {
                    java.util.logging.Logger.getLogger(WikipediaParser.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        } catch (IOException ex) {
             java.util.logging.Logger.getLogger(WikipediaParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        printStatus();
    }

    private void parsePage(final XMLStreamReader reader, final List<String> pages2extract, int mode) throws XMLStreamException, IOException {
        String title = null;
        String text = null ;
        while (reader.hasNext()) {
            final int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals(ELEMENT_PAGE)) {
                if(title !=null && text !=null){
//                  System.out.println(title);
//                  System.out.println(title);
                    // first filter of the text 
                    if(text.length() > TEXT_LENGTH){
                        if(pages2extract.contains(title)){ 
                          this.pageCount++;  
                          out = new PrintStream(CodeUtil.openFileOuputStream(outputPath, title));  
                          if(mode == 1 ){
                             out.print(text);
                          }else{
                               WikiPageClean c = new WikiPageClean(text);
                               String content = c.clean();
                               switch (mode) {
                                  case 2:
                                      out.print(content);
                                      break;
                                  case 3:
                                      BagOfWords  bw = new BagOfWords (content);
                                      out.print(bw.getBagOfWord());
                                      break;
                                  default:
                                      break;
                              }
                          }
                        }
                    }
                }
                return;
            }
            if (event == XMLStreamConstants.START_ELEMENT) {
                final String elementName = reader.getLocalName();
//              System.out.println(elementName);
                switch (elementName) {
                    case ELEMENT_TITLE:
                        title = reader.getElementText();
                        break;
                    case ELEMENT_TXT:
                        text = reader.getElementText();
                        break;
                }
            }
        }
    }

    private List<String> getPagesList(String pagesList, int position) throws IOException {
        List<String> pageTitles = new ArrayList<>(); 
        try (Stream<String> lines = Files.lines(Paths.get(pagesList) )   ) {
            lines
                .forEach(line->{
                    if(!line.isEmpty()){
                         String[] split = line.trim().split(",");
                         pageTitles.add(split[position]);
                    }
                });
        }
        return pageTitles;
    }


    public void close() {
           printStatus();
           this.out.close();
     }
    
    public void printStatus() {
		LOG.info("Found " + this.pageCount
				+ " matching items after scanning " + this.count
				+ " items."); 
    }
    
    public static void setTextLenght(int textLenght){
        TEXT_LENGTH = textLenght;
    }
}



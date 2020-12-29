/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;

/**
 *
 * @author Zied Bouraoui  <bouraoui@cril.univ-artois.fr>
 */
public class MyHelpers {

        public static final boolean OFFLINE_MODE = true;


	public enum DumpProcessingMode {
		JSON
	}
        
        public static final DumpProcessingMode DUMP_FILE_MODE = DumpProcessingMode.JSON;

        public static final int TIMEOUT_SEC = 0;

        private static String lastDumpFileName = "";
        
        /**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 */
        
	public static void configureLogging() {
		// Create the appender that will write log messages to the console.
		ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.INFO);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}
        
        
        
	/**
	 * Processes all entities in a Wikidata dump using the given entity
	 * processor. By default, the most recent JSON dump will be used. In offline
	 * mode, only the most recent previously downloaded file is considered.
	 *
	 * @param entityDocumentProcessor
	 *            the object to use for processing entities in this dump
	 */
        
	public static void processEntitiesFromWikidataDump(String DumpFile,
                EntityDocumentProcessor entityDocumentProcessor) {

		// Controller object for processing dumps:
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"enwiki");
		dumpProcessingController.setOfflineMode(OFFLINE_MODE);

		// Subscribe to the most recent entity documents of type wikibase item:
		dumpProcessingController.registerEntityDocumentProcessor(
				entityDocumentProcessor, null, true);

		// Also add a timer that reports some basic progress information:
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(
				TIMEOUT_SEC);
		dumpProcessingController.registerEntityDocumentProcessor(
				entityTimerProcessor, null, true);

		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DumpFile);
                dumpProcessingController.processDump(mwDumpFile);

		// Print final timer results:
		entityTimerProcessor.close();
	}
        
        
        	/**
	 * Opens a new FileOutputStream for a file of the given name in the example
	 * output directory ({@link ExampleHelpers#EXAMPLE_OUTPUT_DIRECTORY}). Any
	 * file of this name that exists already will be replaced. The caller is
	 * responsible for eventually closing the stream.
	 *
	 * @param filename
	 *            the name of the file to write to
	 * @return FileOutputStream for the file
	 * @throws IOException
	 *             if the file or example output directory could not be created
	 */
        
	public static FileOutputStream openExampleFileOuputStream(String outputDir, String filename)
			throws IOException {
		Path directoryPath;
		if ("".equals(lastDumpFileName)) {
			directoryPath = Paths.get(outputDir);
		} else {
			directoryPath = Paths.get(outputDir);
			createDirectory(directoryPath);
			directoryPath = directoryPath.resolve(
					lastDumpFileName);
		}

		createDirectory(directoryPath);
		Path filePath = directoryPath.resolve(filename);
		return new FileOutputStream(filePath.toFile());
	}
        
            
	private static void createDirectory(Path path) throws IOException {
		try {
			Files.createDirectory(path);
		} catch (FileAlreadyExistsException e) {
			if (!Files.isDirectory(path)) {
				throw e;
			}
		}
	}
        
        	/**
	 * Returns the name of the dump file that was last processed. This can be
	 * used to name files generated from this dump. The result might be the
	 * empty string if no file has been processed yet.
	 */
	public static String getLastDumpFileName() {
		return lastDumpFileName;
	}
        
        
        public static String csvEscape(String string) {
		if (string == null) {
			return "\"\"";
		} else {
			return "\"" + string.replace("\"", "\"\"") + "\"";
		}
	}
    
    

    

}

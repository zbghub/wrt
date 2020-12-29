/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Zied Bouraoui <bouraouiz@cardiff.ac.uk>
 */
public class CodeUtil {
    
    
	private static void createDirectory(Path path) throws IOException {
		try {
			Files.createDirectory(path);
		} catch (FileAlreadyExistsException e) {
			if (!Files.isDirectory(path)) {
				throw e;
			}
		}
	}
        
	public static FileOutputStream openFileOuputStream(String dirPath, String filename)
		      throws IOException {
		Path directoryPath = Paths.get(dirPath);
		createDirectory(directoryPath);
		Path filePath = directoryPath.resolve(filename);
		return new FileOutputStream(filePath.toFile());
	}
        
        
            
        public static <K,V extends Comparable<? super V>>
            SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
            SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1;
                    }
                }
            );
            sortedEntries.addAll(map.entrySet());
            return sortedEntries;
        }
    
    
}

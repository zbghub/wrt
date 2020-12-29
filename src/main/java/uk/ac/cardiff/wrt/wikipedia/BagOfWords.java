/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.wikipedia;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.stream.Collectors;
import uk.ac.cardiff.wrt.util.CodeUtil;


public class BagOfWords {
    
    public static int NUMBER_OF_OCCURRENCE = 10; 
    
    public static int NUMBER_OF_WORDS = 10; 
    
    public String content; 

    public BagOfWords(String content) {
        this.content = content;
    }
    
    public String getBagOfWord(){
          Map<String, Integer> bagOfWordMatrix = getBagOfWordMatrix(Arrays.asList(content.trim().split(" ")));
          String bagOfWord = getBagOfWordAsString(bagOfWordMatrix);
          return bagOfWord; 
    }

    private Map<String, Integer> getBagOfWordMatrix(List<String> wordList) {
        Map<String, Integer> bagOfWordMatrix = new TreeMap<>();
        wordList.forEach(w->{
            w = w.toLowerCase();
            if(!bagOfWordMatrix.containsKey(w)){
                bagOfWordMatrix.put(w, 1);
            }else{
                int i = bagOfWordMatrix.get(w) + 1;
                bagOfWordMatrix.replace(w, i);
               }
        });
        return bagOfWordMatrix;
    }

    private String getBagOfWordAsString(Map<String, Integer> bagOfWordMatrix) {
         SortedSet<Map.Entry<String, Integer>> sortedMap  = CodeUtil.entriesSortedByValues( bagOfWordMatrix );
         final List<String> lines = sortedMap.stream().filter(e -> e.getValue()> NUMBER_OF_OCCURRENCE).map(e->e.getKey()+":"+e.getValue())
                 .collect(Collectors.toList());
        return String.join("\n", lines);
    }


    public static void setNumberOfOccurrence (int value){
       NUMBER_OF_OCCURRENCE = value;
    } 
    
    public static void setNumberOfWords (int value){
       NUMBER_OF_WORDS= value;
    }   

}

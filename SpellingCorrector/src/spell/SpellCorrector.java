package spell;

import java.io.IOException;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.HashSet;
import java.util.Scanner; // Import the Scanner class to read text files

public class SpellCorrector implements ISpellCorrector {
  public Trie trie;

  public SpellCorrector() {
    this.trie = new Trie();
  }

  @Override
  public void useDictionary(String dictionaryFileName) throws IOException {
    // Read and begin scanning file
    File inputFile = new File(dictionaryFileName);
    Scanner in = new Scanner(inputFile);

    // Build Trie
    while (in.hasNext()) {
      String word = in.next();
      trie.add(word);
    }
    in.close();
  }

  @Override
  public String suggestSimilarWord(String inputWord) {
    String word = inputWord.toLowerCase();    // Find similar word independent of case
    String bestWord = "";

    // If word is in the trie, it is spelled correctly
    INode wordInTrie = trie.find(word);
    if (wordInTrie != null) {
      return word;
    }

    // Find distance 1 words
    HashSet<String> setDist1 = new HashSet<String>();
    permutateWord(word, setDist1);
    bestWord = findBestWord(word, setDist1);

    // If no permutation of distance 1 were found, compute distance 2 words
    if (bestWord.equals("")) {
      // Find distance 2 words for each word in setDist1
      HashSet<String> setDist2 = new HashSet<String>();
      for (String permutation : setDist1) {
        permutateWord(permutation, setDist2);
      }
      bestWord = findBestWord(word, setDist2);
    }

    // Check if a best word was found
    if (bestWord.equals("")) {
      return null;
    }
    return bestWord;
  }

  public String findBestWord(String word, HashSet<String> set) {
    int bestFreq = 0;
    String bestWord = "";

    for (String permutation : set) {
      // Check if permutation is in trie
      INode node = trie.find(permutation);
      if (node != null) {

        // If frequency is greater, update bestFreq and bestWord
        if (node.getValue() > bestFreq) {
          bestFreq = node.getValue();
          bestWord = permutation;
        }

        // If same frequency, check alphabetical order
        if (node.getValue() == bestFreq) {
          // Check if permutation comes first in the alphabet
          if (permutation.compareTo(bestWord) < 0) {
            bestFreq = node.getValue();
            bestWord = permutation;
          }
        }
      }
    }
    return bestWord;
  }

  public void permutateWord(String word, HashSet<String> set) {
    // Add permutations of word to set
    deletionDistance(set, word);
    transpositionDistance(set, word);
    alterationDistance(set, word);
    insertionDistance(set, word);
  }

  public void deletionDistance(HashSet<String> set, String word) {
    for (int i = 0; i < word.length(); i++) {
      StringBuilder editWord = new StringBuilder(word);
      editWord.deleteCharAt(i);
      set.add(editWord.toString());
    }
  }
  public void transpositionDistance(HashSet<String> set, String word) {
    for (int i = 0; i < word.length() - 1; i++) {
      StringBuilder editWord = new StringBuilder(word);

      char char1 = editWord.charAt(i);
      char char2 = editWord.charAt(i + 1);
      editWord.setCharAt(i, char2);
      editWord.setCharAt(i + 1, char1);
      set.add(editWord.toString());
    }
  }
  public void alterationDistance(HashSet<String> set, String word) {
    for (int i = 0; i < word.length(); i++) {
      for (int j = 0; j < 26; j++) {
        StringBuilder editWord = new StringBuilder(word);
        char c = (char) ('a' + j);

        if (editWord.charAt(i) != c) {
          editWord.setCharAt(i, c);
          set.add(editWord.toString());
        }
      }
    }
  }
  public void insertionDistance(HashSet<String> set, String word) {
    for (int i = 0; i <= word.length(); i++) {
      for (int j = 0; j < 26; j++) {
        StringBuilder editWord = new StringBuilder(word);
        char c = (char) ('a' + j);
        editWord.insert(i, c);
        set.add(editWord.toString());
      }
    }
  }
}

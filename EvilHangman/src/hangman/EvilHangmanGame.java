package hangman;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;
import java.util.Scanner;

@SuppressWarnings("CheckStyle")
public class EvilHangmanGame implements IEvilHangmanGame {

  public Set<String> words = null;
  public String curAnswer = null;
  public SortedSet<Character> guessedLetters = null;

  public EvilHangmanGame() { }

  public void initCurAnswer(int wordLength) {
    for (int i = 0; i < wordLength; i++) {
      curAnswer += "-";
    }
  }

  public void PrintWords() {
    // For debugging purposes
    System.out.println("size of words: " + words.size());
    for (String word : words) {
      System.out.println(word);
    }
  }

  @Override
  public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
    words = new HashSet<String>();
    curAnswer = "";
    guessedLetters = new TreeSet<Character>();

    // Create scanner to read dictionary
    Scanner in = new Scanner(dictionary);

    // Set curAnswer to '-'s to start
    initCurAnswer(wordLength);

    // Read dictionary
    while (in.hasNext()) {
      String word = in.next();  // Get next word

      // Only add the word to HashSet if its length is wordLength
      if (word.length() == wordLength) {
        words.add(word);
      }
    }
    in.close();

    // Check if dictionary is empty
    if (words.size() == 0) {
      throw new EmptyDictionaryException();
    }

  }

  public int guessLetter(char guess) throws GuessAlreadyMadeException {
    // Partition the dictionary based upon the guessed character
    makeGuess(guess);

    // Count the number of times the guessed character is found in curAnswer
    int numOccurrences = 0;
    for (int i = 0; i < curAnswer.length(); i++) {
      if (curAnswer.charAt(i) == guess) {
        numOccurrences++;
      }
    }
    return numOccurrences;
  }

  @Override
  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
    guess = Character.toLowerCase(guess);

    // Check if given guess has already been guessed
    if (guessedLetters.contains(guess)) {
      throw new GuessAlreadyMadeException();
    }
    guessedLetters.add(guess);

    // Initialize a map that maps partition pattern strings to sets of words matching that partition pattern
    HashMap<String, HashSet<String>> allPartitions = new HashMap<String, HashSet<String>>();

    // Iterate over guessedLetters TreeSet to build partitions
    for (String word : words) {
      String partition = "";

      // Track where the guess character appears in word
      for (int i = 0; i < word.length(); i++) {
        if (guess == word.charAt(i)) {
          partition += guess;
        }
        else {
          partition += "-";
        }
      }

      HashSet<String> partitionSet = null;

      // Check if partition is in HashMap
      if (allPartitions.containsKey(partition)) {
        partitionSet = allPartitions.get(partition);   // Get the partition that the word belongs to
      }
      else {
        partitionSet = new HashSet<String>();   // Initialize new partition if not in HashMap
      }

      partitionSet.add(word);   // Add new word to partition set
      allPartitions.put(partition, partitionSet);    // Remap the partition to the updated partition set
    }
    words = FindBiggestPartition(allPartitions, guess);
    return words;
  }

  public HashSet<String> FindBiggestPartition(HashMap<String, HashSet<String>> allPartitions, char guess) {

    String biggestPattern = "";
    int maxSize = 0;
    int maxPartitionCount = 0;

    for (String pattern : allPartitions.keySet()) {
      HashSet<String> partition = allPartitions.get(pattern);   // Get current partition

      // Determine biggest partition
      if (maxSize < partition.size()) {
        biggestPattern = pattern;
        maxSize = partition.size();
        maxPartitionCount = CountLetters(pattern, guess);
      }

      // If size is equal, store to compare latter
      else if (maxSize == partition.size()) {
        int curPartitionCount = CountLetters(pattern, guess);

        // Check if current partition has few guess characters, it is better
        if (curPartitionCount < maxPartitionCount) {
          biggestPattern = pattern;
          maxSize = partition.size();
          maxPartitionCount = curPartitionCount;
        }
        // If guess character count is equal, compare the placing of guess characters
        else if (curPartitionCount == maxPartitionCount) {
          biggestPattern = RightMostChars(pattern, biggestPattern, guess);
          maxSize = allPartitions.get(biggestPattern).size();
          maxPartitionCount = CountLetters(biggestPattern, guess);
        }
      }
    }
    updateCurAnswer(biggestPattern);  // Merge biggestPattern with curAnswer
    return allPartitions.get(biggestPattern);
  }

  public String RightMostChars(String partition1, String partition2, char guess) {
    for (int i = partition1.length() - 1; i >= 0; i--) {
      // Get the chars at position i for both partitions
      char letter1 = partition1.charAt(i);
      char letter2 = partition2.charAt(i);

      // If one char matches guess while the other does not, return the corresponding partition
      if (letter1 == guess && letter2 != guess) {
        return partition1;
      }
      if (letter2 == guess && letter1 != guess) {
        return partition2;
      }
    }
    return null;
  }

  public int CountLetters(String pattern, char guess) {
    // Count how many guess characters appear in the pattern
    int count = 0;
    for (int i = 0; i < pattern.length(); i++) {
      if (guess == pattern.charAt(i)) {
        count++;
      }
    }
    return count;
  }

  public void updateCurAnswer(String biggestPattern) {
    // Merge the curAnswer and the newly found pattern together
    StringBuilder answerBuilder = new StringBuilder(curAnswer);
    for (int i = 0; i < biggestPattern.length(); i++) {
      if (biggestPattern.charAt(i) != '-') {
        answerBuilder.setCharAt(i, biggestPattern.charAt(i));
      }
    }
    curAnswer = answerBuilder.toString();
  }

  @Override
  public SortedSet<Character> getGuessedLetters() {
    return guessedLetters;
  }

  public Set<String> getWords() {
    return words;
  }

  public Boolean checkIfWon() {
    // If all spaces of curAnswer have been filled and no '-'s remain, they win
    return curAnswer.indexOf('-') < 0;
  }

  public String getLosingAnswer() {
    // Return any word from the word set
    for (String word : words) {
      return word;
    }
    return null;
  }
}

package collections;

import collections.exceptions.InvalidWordException;

import java.util.List;
import java.util.stream.Collectors;

public interface CompactWordsSet {

  static void checkIfWordIsValid(String word) throws InvalidWordException {
    if (word == null || word == "" || containsNonLowercaseAlphabetic(word)) {
      throw new InvalidWordException("The word is invalid!");
    }
  }

  private static boolean containsNonLowercaseAlphabetic(String word) {
    for (int i = 0; i < word.length(); i++) {
      if (!Character.isAlphabetic(word.charAt(i)) || !Character.isLowerCase(word.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  boolean add(String word) throws InvalidWordException;

  boolean remove(String word) throws InvalidWordException;

  boolean contains(String word) throws InvalidWordException;

  int size();

  List<String> uniqueWordsInAlphabeticOrder();

}

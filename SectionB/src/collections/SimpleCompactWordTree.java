package collections;

import collections.exceptions.InvalidWordException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCompactWordTree implements CompactWordsSet {
  private WordTreeNode root;
  private int size;
  public static final int NUMBER_OF_LETTERS_IN_ALPHABET = 26;

  public SimpleCompactWordTree() {
    root = new WordTreeNode('\0');
    size = 0;
  }

  @Override
  public synchronized boolean add(String word) throws InvalidWordException {
    CompactWordsSet.checkIfWordIsValid(word);

    WordTreeNode current = root;
    int wordPointer = 0;
    while (wordPointer < word.length() - 1) {
      char currentLetter = word.charAt(wordPointer);
      if (!current.hasChild(currentLetter)) {
        current.addChild(currentLetter);
      }
      current = current.getChild(currentLetter);
      wordPointer++;
    }

    char lastLetter = word.charAt(wordPointer);
    if (current.hasChild(lastLetter) && current.getChild(lastLetter).isWord) {
      return false;
    } else {
      current.addChild(lastLetter);
      current.getChild(lastLetter).isWord = true;
      size++;
      return true;
    }
  }

  @Override
  public synchronized boolean remove(String word) throws InvalidWordException {
    CompactWordsSet.checkIfWordIsValid(word);
    WordTreeNode current = root;
    int wordPointer = 0;
    while (wordPointer < word.length() - 1) {
      char currentLetter = word.charAt(wordPointer);
      if (!current.hasChild(currentLetter)) {
        return false;
      }
      current = current.getChild(currentLetter);
      wordPointer++;
    }

    char lastLetter = word.charAt(wordPointer);
    if (current.hasChild(lastLetter) && current.getChild(lastLetter).isWord) {
      current.getChild(lastLetter).isWord = false;
      size--;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public synchronized boolean contains(String word) throws InvalidWordException {
    CompactWordsSet.checkIfWordIsValid(word);

    WordTreeNode current = root;
    int wordPointer = 0;
    while (wordPointer < word.length() - 1) {
      char currentLetter = word.charAt(wordPointer);
      if (!current.hasChild(currentLetter)) {
        return false;
      }
      current = current.getChild(currentLetter);
      wordPointer++;
    }

    char lastLetter = word.charAt(wordPointer);
    return current.hasChild(lastLetter) && current.getChild(lastLetter).isWord;

  }

  @Override
  public synchronized int size() {
    return size;
  }

  @Override
  public List<String> uniqueWordsInAlphabeticOrder() {
    WordTreeNode current = root;
    List<String> uniqueWords = new ArrayList<>();
    populateUniqueWords(current, "", uniqueWords);
    return uniqueWords;
  }

  private void populateUniqueWords(WordTreeNode current,
                                   String pendingWord,
                                   List<String> wordsFoundSoFar
  ) {
    if (current.isWord) {
      wordsFoundSoFar.add(pendingWord);
    }
    for (WordTreeNode child : current.getPresentChildren()) {
      if (child != null) {
        populateUniqueWords(child, pendingWord + child.getLetter(), wordsFoundSoFar);
      }
    }
  }

  private static class WordTreeNode {
    private final char letter;
    private boolean isWord;
    private WordTreeNode[] children;

    public WordTreeNode(char letter) {
      this.letter = letter;
      children = new WordTreeNode[NUMBER_OF_LETTERS_IN_ALPHABET];
      isWord = false;
    }

    public char getLetter() {
      return letter;
    }

    private static int charToIndex(char letter) {
      return Character.getNumericValue(letter) - Character.getNumericValue('a');
    }

    private static int indexToChar(int index) {
      return (char) index + Character.getNumericValue('a');
    }

    public void addChild(char letter) {
      if (hasChild(letter)) {
        getChild(letter).isWord = true;
      } else {
        children[charToIndex(letter)] = new WordTreeNode(letter);
      }
    }

    public WordTreeNode getChild(char letter) {
      assert hasChild(letter);
      return children[charToIndex(letter)];
    }

    public List<WordTreeNode> getPresentChildren() {
      List<WordTreeNode> presentChildren = new ArrayList<>();
      for (WordTreeNode child : children) {
        if (child != null) {
          presentChildren.add(child);
        }
      }
      return presentChildren;
    }

    public boolean hasChild(char letter) {
      return children[charToIndex(letter)] != null;
    }
  }
}

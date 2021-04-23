package collections;

import collections.exceptions.InvalidWordException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedCompactWordTree implements CompactWordsSet {
  private LockableWordTreeNode root;
  private AtomicInteger size;
  public static final int NUMBER_OF_LETTERS_IN_ALPHABET = 26;

  public FineGrainedCompactWordTree() {
    root = new LockableWordTreeNode('\0');
    size = new AtomicInteger(0);
  }

  @Override
  public boolean add(String word) throws InvalidWordException {
    CompactWordsSet.checkIfWordIsValid(word);
    LockableWordTreeNode current = root;
    LockableWordTreeNode successor;
    try {
      current.lock();
      int wordPointer = 0;

      while (wordPointer < word.length() - 1) {
        char currentLetter = word.charAt(wordPointer);
        if (!current.hasChild(currentLetter)) {
          current.addChild(currentLetter);
        }
        successor = current.getChild(currentLetter);
        successor.lock();
        current.unlock();
        current = successor;
        wordPointer++;
      }

      char lastLetter = word.charAt(wordPointer);
      if (current.hasChild(lastLetter) && current.getChild(lastLetter).isWord) {
        return false;
      } else {
        current.addChild(lastLetter);
        current.getChild(lastLetter).isWord = true;
        size.incrementAndGet();
        return true;
      }
    } finally {
      current.unlock();
    }

  }


  @Override
  public boolean remove(String word) throws InvalidWordException {
    CompactWordsSet.checkIfWordIsValid(word);
    LockableWordTreeNode current = root;
    LockableWordTreeNode successor;
    try {
      current.lock();
      int wordPointer = 0;
      while (wordPointer < word.length() - 1) {
        char currentLetter = word.charAt(wordPointer);
        if (!current.hasChild(currentLetter)) {
          return false;
        }
        successor = current.getChild(currentLetter);
        successor.lock();
        current.unlock();
        current = successor;
        wordPointer++;
      }

      char lastLetter = word.charAt(wordPointer);
      if (current.hasChild(lastLetter) && current.getChild(lastLetter).isWord) {
        current.getChild(lastLetter).isWord = false;
        size.decrementAndGet();
        return true;
      } else {
        return false;
      }
    } finally {
      current.unlock();
    }
  }

  @Override
  public boolean contains(String word) throws InvalidWordException {
    CompactWordsSet.checkIfWordIsValid(word);

    LockableWordTreeNode current = root;
    LockableWordTreeNode successor;
    try {
      current.lock();
      int wordPointer = 0;
      while (wordPointer < word.length() - 1) {
        char currentLetter = word.charAt(wordPointer);
        if (!current.hasChild(currentLetter)) {
          return false;
        }
        successor = current.getChild(currentLetter);
        successor.lock();
        current.unlock();
        current = successor;
        wordPointer++;
      }

      char lastLetter = word.charAt(wordPointer);
      return current.hasChild(lastLetter) && current.getChild(lastLetter).isWord;
    } finally {
      current.unlock();
    }
  }

  @Override
  public int size() {
    return size.get();
  }

  @Override
  public List<String> uniqueWordsInAlphabeticOrder() {
    LockableWordTreeNode current = root;
    List<String> uniqueWords = new ArrayList<>();
    populateUniqueWords(current, "", uniqueWords);
    return uniqueWords;
  }

  private void populateUniqueWords(LockableWordTreeNode current,
                                   String pendingWord,
                                   List<String> wordsFoundSoFar
  ) {
    if (current.isWord) {
      wordsFoundSoFar.add(pendingWord);
    }
    for (LockableWordTreeNode child : current.getPresentChildren()) {
      if (child != null) {
        populateUniqueWords(child, pendingWord + child.getLetter(), wordsFoundSoFar);
      }
    }
  }

  private static class LockableWordTreeNode {
    private final char letter;
    private boolean isWord;
    private LockableWordTreeNode[] children;
    private Lock lock = new ReentrantLock();

    public LockableWordTreeNode(char letter) {
      this.letter = letter;
      children = new LockableWordTreeNode[NUMBER_OF_LETTERS_IN_ALPHABET];
      isWord = false;
    }

    public void lock() {
      lock.lock();
    }

    public void unlock() {
      lock.unlock();
    }

    public char getLetter() {
      return letter;
    }

    private static int charToIndex(char letter) {
      return Character.getNumericValue(letter) - Character.getNumericValue('a');
    }

    public boolean hasChild(char letter) {
      return children[charToIndex(letter)] != null;
    }

    public void addChild(char letter) {
      if (hasChild(letter)) {
        getChild(letter).isWord = true;
      } else {
        children[charToIndex(letter)] = new LockableWordTreeNode(letter);
      }
    }

    public LockableWordTreeNode getChild(char letter) {
      assert hasChild(letter);
      return children[charToIndex(letter)];
    }

    public List<LockableWordTreeNode> getPresentChildren() {
      List<LockableWordTreeNode> presentChildren = new ArrayList<>();
      for (LockableWordTreeNode child : children) {
        if (child != null) {
          presentChildren.add(child);
        }
      }
      return presentChildren;
    }
  }
}

package spell;

public class Trie implements ITrie {
  private int wordCount;
  private int nodeCount;
  private Node root;

  public Trie() {
    this.wordCount = 0;
    this.nodeCount = 1;
    this.root = new Node();
  }

  @Override
  public void add(String word) {
    Node node = this.root;
    for (int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      int index = c - 'a';

      // Check if child exists for letter c
      if (node.children[index] == null) {
        node.children[index] = new Node();
        this.nodeCount++;
      }
      node = node.children[index];    // Move to next node for c
    }

    // Check if word is already in trie
    if (node.freqCount == 0) {
      this.wordCount++;
    }
    node.freqCount++;
  }

  @Override
  public INode find(String word) {
    Node node = this.root;

    for (int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      int index = c - 'a';

      // Check if child exists for letter c
      if (node.children[index] != null) {
        node = node.children[index];
      }
      else {
        return null;
      }
    }

    if (node.freqCount > 0) {
      return node;
    }
    return null;
  }

  @Override
  public int getWordCount() {
    return this.wordCount;
  }

  @Override
  public int getNodeCount() {
    return this.nodeCount;
  }

  public void toString_Helper(Node node, StringBuilder curWord, StringBuilder output) {
    if (node == null) {
      return;
    }

    // Check if this is a word
    if (node.getValue() > 0) {
      output.append(curWord.toString() + '\n');
    }
    Node[] children = node.children;    // Get the node's children
    if (children != null) {

      // Iterate over children, recursively building words for each child
      for (int i = 0; i < 26; i++) {
        Node child = children[i];
        if (child != null) {
          char c = (char) ('a' + i);
          curWord.append(c);
          toString_Helper(child, curWord, output);    // Recursively call child
          curWord.deleteCharAt(curWord.length() - 1);   // Remove letter to back up to the parent word
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder curWord = new StringBuilder();
    StringBuilder output = new StringBuilder();
    toString_Helper(this.root, curWord, output);
    return output.toString();
  }

  @Override
  public int hashCode() {
    int a = 1 + this.wordCount;
    int b = 1 + this.nodeCount;
    int c = 4;
    int d = 3;

    if (this.root.children != null) {
      for (int i = 0; i < 26; i++) {
        if (this.root.children[i] != null) {
          c += i;
          d += this.root.children[i].freqCount;
        }
      }
    }

    return (a + b) * c + d;
  }

  public boolean equals_Helper(Node node1, Node node2) {
    // Check if they have the same value
    if (node1.getValue() != node2.getValue()) {
      return false;
    }

    // Iterate over children, recursively comparing each child
    boolean equalChildren = true;
    for (int i = 0; i < 26; i++) {
      Node child1 = node1.children[i];
      Node child2 = node2.children[i];
      if (child1 != null && child2 != null) {
        equalChildren = equals_Helper(child1, child2);    // Recursively check child
      }
      else if (child1 == null && child2 != null) {
        return false;
      }
      else if (child2 == null && child1 != null) {
        return false;
      }
      if (!equalChildren) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (o.getClass() != this.getClass()) {
      return false;
    }
    Trie trie = (Trie) o;
    if (!(trie.wordCount == this.wordCount) || !(trie.nodeCount == this.nodeCount)) {
      return false;
    }
    return equals_Helper(this.root, trie.root);
  }
}

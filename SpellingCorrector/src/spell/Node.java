package spell;

public class Node implements INode {
  public int freqCount;
  public Node[] children;

  public Node() {
    this.freqCount = 0;
    this.children = new Node[26];
  }

  @Override
  public int getValue() {
    return this.freqCount;
  }
}

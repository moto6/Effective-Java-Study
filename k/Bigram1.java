package k;

import java.util.HashSet;
import java.util.Set;

public class Bigram1 {
  private final char first;
  private final char second;

  public Bigram1(char first, char second) {
    this.first = first;
    this.second = second;
  }

  public boolean equals(Bigram1 b) {
    return b.first == first && b.second == second;
  }

  public int hashCode() {
    return 31 * first + second;
  }

  public static void main(String[] args) {
    Set<Bigram1> s = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      for (char ch = 'a'; ch <= 'z'; ch++) {
        s.add(new Bigram1(ch, ch));
      }
    }
    System.out.println(s.size());
  }
}

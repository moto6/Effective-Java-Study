

### 아이템5: 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라.



다음과 같이 정적 유틸 클래스를 구현한 경우와 싱글턴을 구현한 경우는 사전이 언어별로 따로 있다는 것을 고려해봤을 때, 하나의 사전에 너무 의존적이다. 

**정적 유틸 클래스 방식** 

```java
public class SpellChecker {

    private static final Lexicon dictionary = new KoreanDictionary();

    private SpellChecker() {
    }

    public static boolean isValid(String word) {
      	...
        return true;
    }

    public static List<String> suggestions(String type) {
      	...
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        SpellChecker.isValid("hello");
    }
}

interface Lexicon {
}

class KoreanDictionary implements Lexicon {

}
```



**싱글턴 방식**

```java
public class SpellChecker {

    private final Lexicon dictionary = new KoreanDictionary();

    private SpellChecker() {
    }

    public static final SpellChecker INSTANCE = new SpellChecker();

    public boolean isValid(String word) {
        return true;
    }

    public List<String> suggestions(String type) {
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        SpellChecker.INSTANCE.isValid("hello");
    }
}

interface Lexicon {
}

class KoreanDictionary implements Lexicon {

}

```

dictionary와 같이 사용하는 자원에 따라 동작이 달라지는 클래스는 위의 두 방식이 적합하지 않다.

-> 유연하지 않다.



따라서, 인스턴스를 생성할 때 **생성자로 필요한 자원을 주입하는 방식이 적절**하다. -> 의존 객체 주입 

```java
public class SpellChecker {

    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isValid(String word) {
        return true;
    }

    public List<String> suggestions(String type) {
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Lexicon lexicon = new KoreanDictionary();

        SpellChecker spellChecker = new SpellChecker(lexicon);
        spellChecker.isValid("hello");
    }
}

interface Lexicon {
}

class KoreanDictionary implements Lexicon {
}

class TestDictionary implements Lexicon {
}
```

TestDictionary라는 클래스를 만들어서 순수하게 SpellChecker만을 테스트하는 단위 테스트가 가능해진다. 

스프링과 같은 프레임워크가 이와 같은 의존 객체 주입 방식을 알맞게 사용하고 있다.



의존 객체 주입 방식의 변형으로 생성자에 자원 팩토리를 넘겨주는 방식도 유용하다.

- 팩토리? 
  - 호출할 때마다 특정 타입의 인스턴스를 만들어 주는 객체 

Supplier<T> 인터페이스가 하나의 예시이다.

```java
Mosaic create(Supplier<? extends Tile> tileFactory) {
	...
}
```

위와 같이, Supplier<T>를 입력으로 받는 create 메서드는 Tile의 하위 클래스로 매개변수의 타입을 제한한다.

이 방식대로 하면 명시한 타입의 하위 타입을 생성하는 팩토리를 넘길 수 있다. 



### 결론 

클래스가 내부적으로 특정 자원에 의존하고 그 자원이 동작에 영향을 준다면, 

싱글턴과 정적 유틸 클래스보다는 필요한 자원 혹은 그 자원을 만들어주는 팩토리를 생성자로 주입 받는 것이 좋다.

클래스의 유연성, 재사용, 테스트 용이성이 높아진다. 

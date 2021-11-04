# Item 36: 비트 필드 대신 EnumSet 을 사용하라

제목이 곧 내용이다.

## 비트 필드 예시

매우 끔찍하게도 비트 마스킹을 해야한다. <br>
IoT 회사에 가면 경험해볼 수 있다. <br>
그리고 다시는 하고 싶지 않다고 느낄 것이다. <br>
`chmod 777` 이나, IP 주소가 비트 필드이다.

```java
class Item36 {
    static final int STYLE_BOLD = 0x1 << 0; // 1
    static final int STYLE_ITALIC = 0x1 << 1; // 2
    static final int STYLE_UNDERLINE = 0x1 << 2; // 4
    static final int STYLE_STRIKETHROUGH = 0x1 << 3; // 8

    static void applyStyles(int styles) {
        StringBuilder sb = new StringBuilder();
        if ((STYLE_BOLD & styles) != 0) {
            sb.append("BOLD ");
        }
        if ((STYLE_ITALIC & styles) != 0) {
            sb.append("ITALIC ");
        }
        if ((STYLE_UNDERLINE & styles) != 0) {
            sb.append("UNDERLINE ");
        }
        if ((STYLE_STRIKETHROUGH & styles) != 0) {
            sb.append("STRIKETHROUGH ");
        }
        System.out.println(sb);
    }

    static String toBinaryString(int field) {
        return String.format("%32s", Integer.toBinaryString(field))
                .replaceAll(" ", "0");
    }

    public static void main(String[] args) {
        applyStyles(STYLE_BOLD | STYLE_ITALIC | STYLE_STRIKETHROUGH);
        System.out.println();
        System.out.println(toBinaryString(0) + " : ZERO");
        System.out.println(toBinaryString(-1) + " : FULL_BIT");
        System.out.println(toBinaryString(STYLE_BOLD) + " : STYLE_BOLD");
        System.out.println(toBinaryString(STYLE_ITALIC) + " : STYLE_ITALIC");
        System.out.println(toBinaryString(STYLE_UNDERLINE) + " : STYLE_UNDERLINE");
        System.out.println(toBinaryString(STYLE_STRIKETHROUGH) + " : STYLE_STRIKETHROUGH");
    }
}
```

```txt
BOLD ITALIC STRIKETHROUGH 

00000000000000000000000000000000 : ZERO
11111111111111111111111111111111 : FULL_BIT
00000000000000000000000000000001 : STYLE_BOLD
00000000000000000000000000000010 : STYLE_ITALIC
00000000000000000000000000000100 : STYLE_UNDERLINE
00000000000000000000000000001000 : STYLE_STRIKETHROUGH
```

## EnumSet 예시

비트 마스킹을 하다가, EnumSet 을 보면 심신이 정화된다.

```java
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Item36 {
    enum Style {BOLD, ITALIC, UNDERLINE, STRIKETHROUGH}

    static void applyStyles(Set<Style> styles) {
        StringBuilder sb = new StringBuilder();
        if (styles.contains(Style.BOLD)) {
            sb.append("BOLD ");
        }
        if (styles.contains(Style.ITALIC)) {
            sb.append("ITALIC ");
        }
        if (styles.contains(Style.UNDERLINE)) {
            sb.append("UNDERLINE ");
        }
        if (styles.contains(Style.STRIKETHROUGH)) {
            sb.append("STRIKETHROUGH ");
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {
        applyStyles(new HashSet<>(Arrays.asList(Style.BOLD, Style.ITALIC, Style.STRIKETHROUGH)));
    }
}
```

```txt
BOLD ITALIC STRIKETHROUGH 
```

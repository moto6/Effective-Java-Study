# 아이템7 : 다 쓴 객체는 참조를 해제하라
- Java에서는 메모리를 언어가 관리해주고, C/C++는 프로그래머가 관리해야한다
- 메모리 관리를 개발자가 안하니까 편하다만, 신경을 안써도 잘 굴러갈꺼라는 오해를 하면 안됨

## 메모리 누수가 나는 상황 

### 1) 시나리오
![1](https://user-images.githubusercontent.com/31065684/126774784-4c24b360-0c29-4079-b0d0-5eb73f6d98df.jpeg)
### 2) 4번 상황을 그림으로
![2](https://user-images.githubusercontent.com/31065684/126774794-d609572a-3415-4681-8f8f-a6a62405d98d.jpeg)
### 3) 5번 상황을 그림으로
![3](https://user-images.githubusercontent.com/31065684/126774806-7a343ab5-c839-4f82-8c45-5c76205f8a76.jpeg)
### 4) 메모리 누수 해결방법

- 다 쓴 Object 참조를 해제한다, 다시말해서 레퍼런스 변수에 null대입

```java
public Object pop() {
    if (size == 0) {
        throw new EmptyStackException();
    }
    Object result = elements[size];
    elements[size] = null; // 다 쓴 참조 해제
    size--;
    return result;
}
```

- 다 쓴 참조를 null 대입으로 처리할때의 이로운 점
  - null처리된 부분을 실수로 다시 사용하는 에러를 예방
  - NPE발생 >> 스텍자료구조 구현이 잘못된 하는 상황이라면..

## GC에 의해 메모리자원 반납이 되기 위해서

### null 처리(직접할당 해제)
- 예시
```java
elements[size] = null; // 다 쓴 참조 해제

Object object = new Object(...);
// 사용
object = null;
```
- null 처리가 일반적으로 좋은건 아님
  - 이 아이템을 배웠다고 해서 모든 객체를 다 쓰자마자 일일이 null 처리해야겠다는 생각은 하지 말자! 
  - 그럴 필요도 없고 바람직하지도 않다. 프로그램을 지저분하게 만들 뿐이다. 
  - 객체 참조를 null 처리하는 일은 예외적인 경우여야 한다.
  - 다 쓴 참조를 해제하는 가장 우아한 방법은 아래 Scope밖으로 밀어내기 방식

- 그럼에도 불구하고 null 처리를 해야할때
  - (모듈/라이브러리) 가 메모리를 직접 관리할 때 null처리를 하는게 의미가 있다
- 위에서 제시한 stack 상황에서는 null 사용이 옳다!
  - elements 배열로 저장소 풀을 만들어 원
소들을 관리하는데
  - 배열의 활성 영역에 속한 원소들이 사용되고 비활성 영역은 쓰이지 않는다
  - 문제는 가비지 컬렉터 입장에서는 레퍼런스가 있으니 비활성 영역을 처리해주지 않는다(메모리 누수), 똑같이 유효한 객체로 인식함
  - 비활성 영역의 객체가 더 이상 쓸모없다는 건 stack모듈을 개발한 프로그래머만 알고 있다. 이런 stack을 가져다 쓰는 사람은.... 문제가 생겨 뜯어볼때까지 알수 없다
  - 이 상황에서는 비활성 영역이 되는 순간 null 처리해서 해당 객체를 더는 쓰지 않을 것임을 가비지 컬렉터에 알려줘야 메모리 누수가 생기지 않는다

### 참조를 담은 변수를 Scope밖으로 밀어내는 자동할당 해제
- 변수가 선언된 scope가 종료되는 순간 reference가 해제되어 GC의 대상이 됨

```java
static String[] member; // static의 생명주기는 클래스가 로딩될때부터 내려갈때까지(클래스가 메모리에서 내려가는 일이 있나..? 그냥 프로그램이 종료될때 까지인가 모르겠슴당)

int[] age; // 클래스 인스턴스가 생성되서 해제될 때 까지


// ... 생략


public List<?> memberSortByAge(String[] member, int[] age) {

    if(age.size == 0) {
        return new ArrayList<>();
    }
    else {
        List<Memeber> memberList = new ArrayList<>();    
        
        // ... 생략


        for(Member tempMember : memberList) {
            // ... 생략
        
        }//Member tempMember 참조가 해제됨


        return memberList;
    }// List<Memeber> memberList 참조 해제
}
```

### (내생각) 자동 리소스 닫기(try-catch-resources)
- 예외 발생 여부와 상관없이 finally에서 항상 처리해줘야했던 리소스 객체(입출력 스트림, 소켓 등)의 close() 메소드를 호출해서 안전하게 리소스를 닫아준다.
  - 시스템자원도 일종의 메모리라고 생각하면 될까...? 
- 위처럼 가벼운 객체는 프로그램 생명주기 끝날때까지 모를수 있는데, 이런 무거운 객체 혹은 리소스들은 잘 관리해야할꺼같다!

- 자바6 이전까지 사용했던 코드
    ```java
    FileInputStream fis = null;
    try{
        fis = new FileInputStream("file.txt");
        // ... 

    }catch(IOException e){
        // ...
    }finally{
        if(fis != null){
            try{
                fis.close();
            }catch(IOException e) {

            }
        }
    }
    ```
- 자바7 부터 사용가능한 코드. 명시적으로 close()를 호출하지 않아도 자동으로 호출해준다.
    ```java
    try(
        FileInputStream fis = new FileInputStream("file.txt")
        FileOutputStream fos = new FileOutputStream("file2.txt")
    ){
        // ...
    }catch(IOException e){
        // ...
    }
    ````
- try-catch-resources를 사용하기 위해서는 해당 리소스 객체가 java.lang.AutoCloseable 인터페이스를 구현하고 있어야 한다. 
- 아래와 같이 API 도큐먼트에서 AutoCloseable 인터페이스 찾아보도록


## 메모리 누수를 일으키는 주범
- 첫번째는 위에서 설명한 class내에서 instance에 대한 참조(reference)를 관리하는 객체이다.
- 두번째는 Map과 같은 캐시
- 세번째는 리스너(Listener) 혹은 콜백(Callback)
  - Map과 같은 캐시에 객체참조를 넣어두고 사용이 끝났는데도 초기화를 안시켜주는 경우 메모리 누수가 발생한다.
  - 엔트리가 살아있는 동안만 캐시를 사용하려면 WeakHashMap을 사용하자.
  - WeakHashMap을 이해하려면 Java의 Reference를 좀 알아야 한다.

## Java Reference
Java에는 4가지의 Reference가 있다.

Strong Reference

우리가 흔히 사용하는 reference
String str = new String("abc"); 와 같은 형태
Strong Reference는 GC의 대상이 되지 않는다.
Strong Reference관계의 객체가 GC가 되기 위해선 null로 초기화해
객체에 대한 Reachability상태를 UnReachable 상태로 만들어 줘야 한다.
Soft Reference

객체의 Reachability가 Strongly Reachable 객체가 아닌 객체 중 Soft Reference만 있는 상태
SoftReference<Class> ref = new SoftReference<>(new String("abc"));와 같은 형태로 사용
Soft Reference는 대게 GC대상이 아니다가 out of memory에러가 나기 직전까지 가면
Soft Reference 관계에 있는 객체들은 GC대상이 된다.
Weak Reference

객체의 Reachability가 Strongly Reachable 객체가 아닌 객체 중 Soft Reference가 없고 Weak Reference만 있는 상태
WeakReference<Class> ref = new WeakReference<Class>(new String("abc")); 와 같은 형태로 사용
WeakReference는 GC가 발생 할 때마다 대상이 된다.
Phantomly Reference

객체의 Reachability가 Strongly Reachable 객체가 아닌 객체 중 Soft Reference와 Weak Referencerk 모두가 해당되지 않는 객체
finalize 되었지만 메모리가 아직 회수 되지 않은 객체
아직 잘 이해가... 안됨

## 위크해시맵 : https://jaehun2841.github.io/2019/01/07/effective-java-item7/#weakhashmap

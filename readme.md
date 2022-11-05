# Proxy란?
<br>
Client(요청자) 와 Server(제공자) 사이에 Proxy(대리인)을 의미한다.<br>
이 Client와 Server는 굉장히 넓은 의미가 될 수 있는데,<br>
여기서는 웹 브라우저 - 웹 서버의 개념이 아닌, 객체간의 호출자와 호출 당한 객체를 의미한다.<br>

---

## Proxy 도입 장점

<br>
우선, 직접 호출을 하지 않고 대리자를 하나 세웠다는 것 만으로 많은 것을 할 수 있다.<br>
(이미 Spring에서 Proxy를 많이 사용한다.)<br>
<br>
<br>

* 대상에게 접근을 제어하거나, 캐싱을 할 수 있다.
  * 권한에 대한 접근 차단
  * 캐싱
  * 지연로딩 (client가 프록시를 가지고 놀다가 실제 요청이 들어오면 실 객체를 제공한다)
* 대상에게 추가적인 기능을 제공할 수 있다.
  * 원래 서버가 제공하는 기능 + 알파
  * ex) 요청값이나 응답 값을 중간에 변형
  * ex) 실행 시간을 측정해 로그 남기기
* 대상에게 접근할 때, 대리자는 접근을 위임하여 2차 대리인을 세울 수 있다. (3,4,5차는 자유. 프록시 체인이라고 한다)

<br>
<br>

## Proxy 조건

<br>
객체를 Proxy로 사용하기 위해서는 조건이 필요하다.<br>
<br>
<br>

* Interface를 같이 구현하고 있어야 한다. (대체 가능)<br>
    Client는 서버에 Interface만 의존하고, 구현체와 Proxy는 그 Interface를 사용하고 있다.<br>
    따라서 DI를 통해 대체 주입이 가능하다.<br>

<br>


ex) Client - Interface - Server(impl Interface)<br>
    Client - Interface - Proxy(impl Interface) - Server(impl Interface)<br>
<br>
<br>
Spring에서는 같은 구현체면 대체 DI가 가능하다.<br>
그리고 대체된 기능을 그대로 수행할 수 있어야 한다. `클라이언트의 코드 수정 없이`<br><br>



## Proxy를 사용한 디자인 패턴 2가지

<br>

프록시를 사용한 패턴은 크게 두 가지가 있다.
<br>

1. Proxy Pattern
2. Decorator Pattern

### intent(의도)

두 디자인 패턴은 의도가 다르다.<br>
`Proxy Pattern`은 대리자를 통해 객체에 `접근을 제어` 하는 것이 목적이다.<br>
`Decorator Pattern`은 대리자들을 프록시체인으로 묶어 객체에 `동적으로 기능을 추가`하는 것이 목적이다.<br>



### Proxy Pattern

접근을 제어하는 방식 (Cache로 동일한 데이터 return)
<br>

data를 가져올 때, 실 객체에서 계속 같은 요청을 하면 Resource가 소모되므로,<br>
앞에 CacheProxy를 둬서 Cache에 있는지 확인하고, 없으면 실 객체에 요청을 보내고, Cache에 저장한다.<br>
데이터 요청시 1초가 걸리는데, Cache에 있으면 0초가 걸린다.<br>
3번 요청이 들어오면 2번은 Cache에서 가져오고, 1번은 실 객체에서 가져온다.<br>
Proxy 도입으로 2초 절약이 된다.
<br>
<br>


#### CacheProxy 예제

> 예제 구조
<img width="584" alt="스크린샷 2022-11-05 오후 9 51 20" src="https://user-images.githubusercontent.com/37995817/200124066-26706494-b94a-45a4-8157-6f4be18cd26e.png">

<br>



* RealSubject.java(실제 작동 객체)

```java
   @Slf4j
public class RealSubject implements Subject {
    //가상 데이터 조회 상황 (1초나 걸리는 과부화 상황)
    @Override
    public String operation() {
        log.info("실제 객체 호출");
        sleep(1000);
        return "data";
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
```

<br>

* Subject.java(인터페이스)

```java
public interface Subject {
    String operation();
}
```

* CacheProxy.java(Proxy 객체)

```java
@Slf4j
public class CacheProxy implements Subject {

    private Subject target;
    private String data;

    public CacheProxy(Subject subject) {
        this.target = subject;
    }

    @Override
    public String operation() {
        log.info("프록시 호출");
        if (data == null) {
            data = target.operation();
        }
        return data;
    }
}
```


* Client.java(클라이언트)

실 데이터를 요청하는 클라이언트

```java
public class ProxyPatternClient {

    private Subject subject;

    public ProxyPatternClient(Subject subject) {
        this.subject = subject;
    }

    public void execute() {
        subject.operation();
    }
}
```



#### CacheProxy 실행

```java
@Slf4j
public class ProxyPatternTest {


    /**
     * 실제 객체에서 변하지 않는 데이터를 계속 받는 상황.
     * 프록시 패턴으로 Cache를 사용하여 접근을 제어해서, 이미 받은 데이터는 캐싱을 하자.
     * 실제 객체의 데이터를 받는 시간이 1초라고 가정하자.
     */
    @Test
    void noProxyTest() {
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        client.execute();
        client.execute();
        client.execute();
    }


    /**
     * 프록시 패턴을 사용해서, 이미 받은 데이터는 캐싱을 하자.
     * 처음에는 CacheProxy가 RealSubject를 호출하고,
     * 그 다음부터는 CacheProxy에 저장된 data를 호출한다.
     * 그래서 1초만에 끝난다.(3초가 아닌)
     */
    @Test
    void cacheProxyTest() {
        RealSubject realSubject = new RealSubject();
        CacheProxy cacheProxy = new CacheProxy(realSubject);
        ProxyPatternClient proxyPatternClient = new ProxyPatternClient(cacheProxy);

        proxyPatternClient.execute();
        proxyPatternClient.execute();
        proxyPatternClient.execute();
    }

}

```


### Decorator Pattern

프록시로 부가기능을 추가하는 방식.
RealComponent를 실행시키는게 목표고, 그 앞에 Proxy체인으로 부가기능을 추가할 것이다.

<br>

#### Decorator Pattern 예제


> 예제 구조
<img width="546" alt="스크린샷 2022-11-05 오후 10 41 46" src="https://user-images.githubusercontent.com/37995817/200124076-c96b99ee-ac70-4562-965f-078243a65fe2.png">


* RealComponent.java(실제 작동 객체)

```java
@Slf4j
public class RealComponent implements Component {

    @Override
    public String operation() {
        log.info("Real Component 실행");
        return "data";
    }
}

```

---

Decorator들은 추상 클래스로 구현해줘서, 반복되는 코드를 묶어줬다.

* Decorator.java(Proxy 객체)

```java
public abstract class Decorator implements Component {

    private Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    @Override
    public String operation() {
        return component.operation();
    }
}

```

* MessageDecorator.java

메세지 앞, 뒤로 *****를 붙여주는 기능

```java
@Slf4j
public class MessageDecorator extends Decorator {

    public MessageDecorator(Component component) {
        super(component);
    }

    @Override
    public String operation() {
        log.info("MessageDecorator 실행");

        //data -> ****** data ******로 추가
        String resultData = super.operation();
        String decoResult = "****** " + resultData + " ******";
        log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={})",resultData, decoResult);

        return  decoResult;
    }

}

```


* TimeDecorator.java

수행 시간을 측정하는 기능

```java
@Slf4j
public class TimeDecorator extends Decorator {

    public TimeDecorator(Component component) {
        super(component);
    }

        @Override
        public String operation() {
            log.info("TimeDecorator 실행");
            long startTime = System.currentTimeMillis();
            String result = super.operation();
            long endTime = System.currentTimeMillis();
            log.info("실행 시간 : " + (endTime - startTime));
            return result;
        }
}

```

---

* DecoratorPatternClient.java

실제 객체를 호출하려는 client

```java

@Slf4j
public class DecoratorPatternClient {

    private Component component;

    public DecoratorPatternClient(Component component) {
        this.component = component;
    }

    public void execute() {
        String result = component.operation();
        log.info("result={}", result);
    }
}

```


* DecoratorPatternTest.java

```java

@Slf4j
public class DecoratorPatternTest {

    /**
     * 데코레이션 패턴 사용 X
     */
    @Test
    void noDecorator() {
        Component realComponent = new RealComponent();
        DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
        client.execute();

    }


    /**
     * MessageDecorator만 붙인 상태
     */
    @Test
    void decorator1() {
        Component realComponent = new RealComponent();
        Component messageDecorator = new MessageDecorator(realComponent);
        DecoratorPatternClient client = new DecoratorPatternClient(messageDecorator);
        client.execute();
    }


    /**
     * Proxy 체인으로 MessageDecorator, LogDecorator를 붙인 상태
     */
    @Test
    void decorator2() {
        Component realComponent = new RealComponent();
        Component messageDecorator = new MessageDecorator(realComponent);
        Component TimeDecorator = new TimeDecorator(messageDecorator);
        DecoratorPatternClient client = new DecoratorPatternClient(TimeDecorator);
        client.execute();

    }
}


```



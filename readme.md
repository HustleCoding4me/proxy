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
1

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


---


# 동적 프록시

프록시를 동작을 원하는 개수만큼 생성해놓는게 아니라,<br>
동적으로 객체를 만들 수 있다.<br>
프록시를 적용할 코드를 하나 만들고, 동적 프록시 기술로 원하는 만큼 찍어내면 된다.<br>
<br>

그러기 위해서는 기본적으로 자바가 어떤 클래스, 어떤 메서드던지 획득, 작동을 시킬 수 있어야 한다.<br>
그래서 자바에서는 Reflection API를 제공한다.<br>

---

## Reflection API

<br>
대표적으로 package명으로 Class를 가져오고,<br>
이름으로 해당 Class의 Method명을 가져오면 된다.<br>
<br>
개인적으로는 해당 Class의 Method를 전부 가져와서<br>
Enum으로 메서드명을 value로 가져와서 일치하면 실행시키는 식으로 개발했었다.<br>
<br>

### 예시

```java
@Slf4j
public class ReflectionTest {

    /**
     * Reflection이 필요한 상황
     */
    @Test
    void reflection0() {
        Hello target = new Hello();

        /**
         * 호출하는 메서드만 다르고 나머지는 동일한 동작.
         * 공통 로직 1과 2를 하나의 메서드로 뽑아서 합칠 수 있을까?
         */
        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA();//호출하는 메서드가 다름
        log.info("result={}", result1);
        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB();//호출하는 메서드가 다름
        log.info("result={}", result2);
        //공통 로직2 종료

    }

    /**
     * Reflection을 사용.
     * 클래스 정보를 획득하여 사용 예제
     */

    @Test
    void reflection1() throws Exception {
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        //callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        Object result1 = methodCallA.invoke(target);
        log.info("result1={}", result1);

        //callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target);
        log.info("rsult2={}", result2);
    }

    /**
     * Reflection 을 사용해 더 동적으로 만들어보기
     */
    @Test
    void reflection2() throws Exception {
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        //callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);
        //callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
        }

    private void dynamicCall(Method method, Object target) throws Exception{
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}", result);

    }

    /**
     * Runtime에 동작하기 때문에 컴파일 시점에 오류를 잡기 힘들다.
     * 그래서 일반적으로 사용하지 않으면 좋다.
     * 너무 일반적인 공통 처리가 필요할 때 부분적으로 주의해서 사용해야 한다.
     */
    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }

        public String callB() {
            log.info("callB");
            return "B";
        }
    }
}

```

#### Reflection 주의사항


* Runtime에 동작하기 때문에 컴파일 시점에 오류를 잡기 힘들다.
* 그래서 일반적으로 사용하지 않으면 좋다.
* 너무 일반적인 공통 처리가 필요할 때 부분적으로 주의해서 사용해야 한다.




## JDK Dynamic Proxy

### JDK Dynamic Proxy란?

* JDK에서 제공하는 Dynamic Proxy 기능을 사용하면 인터페이스 기반의 프록시 객체를 쉽게 생성할 수 있다.
* JDK Dynamic Proxy는 인터페이스를 구현한 클래스의 객체를 생성할 때 사용한다.(인터페이스가 필수)
* JDK Dynamic Proxy는 `InvocationHandler` 인터페이스를 구현한 클래스의 객체를 생성자 파라미터로 넘겨주면 된다.
* JDK Dynamic Proxy는 인터페이스의 모든 메서드를 구현한 프록시 객체를 생성한다.

### JDK Dynamic Proxy 예제

> 공통 기능을 가진 Proxy를 구현

```java
@Slf4j
public class TimeInvocationHandler implements InvocationHandler {

    private final Object target;

    public TimeInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("TimeProxy 실행");
        long start = System.currentTimeMillis();

        Object result = method.invoke(target, args);

        long end = System.currentTimeMillis();
        log.info("TimeProxy 종료");
        log.info("TimeProxy 실행 시간: {}ms", end - start);
        return result;
    }
}


```
* InvocationHandler을 구현하여 공통 기능을 구현한다. (invoke 메서드)

<br>

> JDK Dynamic Proxy를 사용해 구현

```java

@Slf4j
public class JdkDynamicProxyTest {

    /**
     * 핵심은 target용 Proxy 객체를 각각 생성해준 것이 아닌,
     * TimeInvocationHandler에 공통 기능을 담고, 공통기능을 A구현체, B구현체에 적용시킨 것이다.
     */
    @Test
    void dynamicA() {
        
        //A 객체 앞에 TimeInvocationHanlder 적용
        AInterface target = new AImpl();

        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class},
                handler);

        proxy.call();
        log.info("tagetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        
        //B 객체 앞에 TimeInvocationHanlder 적용
        BInterface targetB = new BImpl();
        TimeInvocationHandler handlerB = new TimeInvocationHandler(targetB);

        BInterface proxyB = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(),
                new Class[]{BInterface.class},
                handler);

        proxyB.call();
        log.info("tagetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

    }
    
}

```


* ` public static Object newProxyInstance(ClassLoader loader,
  Class<?>[] interfaces,
  InvocationHandler h)` : Proxy 객체를 생성하는 메서드이다.<br> 해당 메서드로 Proxy 객체를 받아 원하는 Interface로 형변환 하면 프록시 객체가 생성되는데,<br>
    이 때, InvocationHandler의 invoke 메서드가 호출된다.(구현한)



### JDK Dynamic Proxy 예제2

Method Naming 패턴에 따라 다른 로직을 수행하는 Proxy를 구현

> 공통 적용소스인 Handler를 제작한다. pattern에 따라 다른 로직을 수행한다. (pattern은 외부주입)

```java
public class LogTraceFilterHandler implements InvocationHandler {

    private final Object target;
    private final LogTrace logTrace;
    private final String[] patterns;

    public LogTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns) {
        this.target = target;
        this.logTrace = logTrace;
        this.patterns = patterns;
    }

    /**
     *
     * method 이름 패턴에 따라 작동시킬지 결정
     */

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //메서드 이름 필터
        String methodName = method.getName();
        /**
         * 특정 메서드명 패턴에 매치되지 않으면 Log를 찍지 않고( 별다른 공통 소스 없이)
         * 원본 메서드를 작동시킨다.
         */
        //save, request, reque*, *est
        if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            return method.invoke(target, args);
        }
        TraceStatus status = logTrace.begin(method.getDeclaringClass() + "." + methodName + "()");
        Object result = method.invoke(target, args);
        logTrace.end(status);

        return result;
    }
}

```


<br>

> Proxy를 사용하는 소스 Bean 등록 (Interface Impl, Handler, Proxy)

```java

@Configuration
public class DynamicProxyFilterConfig {

    /**
     *메서드 명 패턴이 아래와 같아야 handler 실행
     */

    /**
     * [dc4b7a40] interface hello.proxy.app.v1.OrderControllerV1.request()
     * [dc4b7a40] |-->interface hello.proxy.app.v1.OrderServiceV1.orderItem()
     * [dc4b7a40] |   |-->interface hello.proxy.app.v1.OrderRepositoryV1.save()
     * [dc4b7a40] |   |<--interface hello.proxy.app.v1.OrderRepositoryV1.save() time=1007ms
     * [dc4b7a40] |<--interface hello.proxy.app.v1.OrderServiceV1.orderItem() time=1016ms
     * [dc4b7a40] interface hello.proxy.app.v1.OrderControllerV1.request() time=1018ms
     */
    private static final String[] PATTERNS = {"request", "order*", "save*"};

    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = orderRepositoryV1(logTrace);

        return (OrderControllerV1) Proxy.newProxyInstance(OrderControllerV1.class.getClassLoader(),
                new Class[]{OrderControllerV1.class},
                new LogTraceFilterHandler(new OrderControllerV1Impl(orderServiceV1(logTrace)), logTrace, PATTERNS));

    }

    @Bean
    OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));

        OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(OrderServiceV1.class.getClassLoader(),
                new Class[]{OrderServiceV1.class},
                new LogTraceFilterHandler(orderService, logTrace, PATTERNS));

        return proxy;
    }
    
    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();

        OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(OrderRepositoryV1.class.getClassLoader(),
                new Class[]{OrderRepositoryV1.class},
                new LogTraceFilterHandler(orderRepository, logTrace, PATTERNS));

        return proxy;
    }
}

```




#### JDK 동적 프록시를 적용한 뒤, 얻는 이점

* 공통 기능을 가진 Proxy 객체를 한번만 구현한 뒤, 원하는 모든 객체에 적용 가능하다.(필요할 때 생성해서)





<br>

## CGLIB

Interface가 아닌 Class에도 Proxy를 적용할 수 있다.<br>
바이트코드로 조작하는 방식이다. (JDK는 인터페이스 기반)<br>

<br>

### JDK Proxy vs CGLIB Proxy

* JDK Proxy는 Interface 기반으로 동작한다. (Interface가 없으면 동작하지 않는다.)<br>
* CGLIB는 Interface가 없어도 동작한다. (Class 기반으로 동작한다.)<br>
* CGLIB는 JDK Proxy보다 느리다. (JDK Proxy는 Interface 기반으로 동작하기 때문에, Interface가 있으면 바이트코드를 조작하지 않고 바로 호출한다.)<br>
* CGLIB는 JDK Proxy보다 많은 메모리를 사용한다. (JDK Proxy는 Interface 기반으로 동작하기 때문에, Interface가 있으면 바이트코드를 조작하지 않고 바로 호출한다.)<br>
* 네이밍 규칙 :  `proxyClass=class com.sun.proxy.$Proxy0` vs `대상클래스$$EnhancerByCGLIB$$임의코드`<br>

<br>

<br>

CGLIB는 보통 직접 구현해서 사용하는 경우는 없다. `ProxyFactoryBean`을 사용한다.<br>

---








## ProxyFactory, Advice, Pointcut

개발자는 ProxyFactory로 Interface인지, Class인지 구분하지 않고, `Advice`를 등록하면 된다.<br>
ProxyFactory는 Interface가 있으면 JDK Proxy를 생성하고, 없으면 CGLIB를 생성해서 Client에게 제공한다.<br>
ProxyFactory로 생성된 Proxy는 `Advice`를 호출한다.<br>
그래서 사용자는 Proxy의 종류에 구애받지 않고 `Advice`로 공통기능을 작성하면 된다.
특정 메서드 패턴만 작동한다던지 동작에 조건을 걸때는 `PointCut`을 사용하면 된다.


`PointCut` : 어디에 부가기능을 적용할지, 어디에 부가기능을 적용하지 않을지 판단하는 필터링 로직이다.
주로 클래스와 메서드 이름으로 필터링 한다. 이름 그대로 어떤 포인트(Point)에 기능을 적용할지 안할지 잘라서 구분 (cut)
`Advice` : 프록시가 호출하는 부가기능. 공통 로직
`Advisor` : 하나의 포인트컷 + 어드바이스를 가지고 있는 객체. 어드바이스를 어디에 적용할지 결정하는 역할


공통기능인 Advice를 제작하고, 어디에 적용할지 결정하는 PointCut을 만들어서 Advisor에 넣어서 ProxyFactory에 등록한다.<br>

>구분한 이유 ? -> 역할과 책임 분배

* 포인트컷 -> 부가 기능 적용 여부 결정하는 필터 기능만 담당
* 어드바이스 -> 부가 기능만 담당
* 둘을 한 세트로 합쳐 어드바이저로 뭉쳐 사용.


<br>
> Advice : 프록시 기능<br>
> Pointcut : 특정 조건을 달성할 때만 프록시 기능이 적용되는 것<br>
> JoinPoint : Advice가 적용되는 지점<br>
> Target : Advice가 적용되는 대상<br>
> Weaving : Advice를 Target에 적용하는 것<br>
> AOP : Aspect Oriented Programming<br>


### ProxyFactory 사용법

> Advice를 정의한다.

```java

@Slf4j
public class TimeAdvice implements MethodInterceptor {
    /**
     *
     * @param invocation the method invocation joinpoint
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");
        long start = System.currentTimeMillis();

        /**
         더이상 target을 넣지 않고, invocation에 이미 담겨져있다.
         target을 찾아서 원본을 실행해준다,
         */
        // Object result = methodProxy.invoke(target, args);
        Object result = invocation.proceed();

        long end = System.currentTimeMillis();
        log.info("TimeProxy 종료");
        log.info("TimeProxy 실행 시간: {}ms", end - start);
        return result;
    }
}

```

* `import org.aopalliance.intercept.MethodInterceptor` : Advice를 구현하는 인터페이스<br>
* ProxyFactory를 생성할 때 이미 target이 있기 때문에, Advice에서 target을 찾아서 실행할 필요가 없다.<br>


> ProxyFactory를 생성한다.

```java

@Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        /**
         * 실제 target으로 ProxyFactory 생성,
         * Advice를 추가하여 Proxy 기능을 담는다.
         */
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        //proxy 작동
        proxy.save();

        /**
         * `AopUtils`
         * ProxyFactory로 Proxy를 생성했을때 사용 가능하다.
         */

        //타겟이 Proxy객체인지 확인
        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        //타겟이 Proxy객체이면서 인터페이스를 구현한 Proxy인지 확인
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        //타겟이 Proxy객체이면서 클래스를 상속받은 Proxy인지 확인
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isFalse();

    }

    
```
* `ProxyFactory proxyFactory = new ProxyFactory(target);` : target을 넣어서 최초 생성한다.
* `proxyFactory.addAdvice(new TimeAdvice());` : Advice를 추가한다.
* ` ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();` : Proxy를 생성한다.
* `AopUtils` : ProxyFactory로 생성된 Proxy를 확인할 수 있다.

* Interface가 없는 경우에도 그냥 비슷하게 구현해주면 된다.


### 결론

ProxyFactory로 추상화 했기 때문에, Proxy 기술에 의존하지 않고 사용할 수 있다.<br>
부가 기능 로직도 `Advice`로 분리해서 사용할 수 있다.<br>
InvocationHandler와 MethodInterceptor가 Advice를 호출하도록 되어있기 때문이다.<br>
위의 예제는 Advice 와 PointCut이 함께 구현되어있기 때문에, 둘을 구분하여야 `단일책임원칙`을<br>
지켜 따로 생성해야한다.<br>


---

## 단일 책임 원칙을 지킨 ProxyFactory


<img width="768" alt="스크린샷 2022-11-20 오후 3 49 10" src="https://user-images.githubusercontent.com/37995817/202889812-12c47673-c2ac-467c-a2fc-768628f794ed.png">
<img width="705" alt="스크린샷 2022-11-20 오후 3 49 19" src="https://user-images.githubusercontent.com/37995817/202889815-1c3a4187-4aad-43f5-bf29-ea7b80e93eba.png">


### 작동 순서

(1)client -> Proxy 객체 호출<br>
(2)-> Proxy 객체(ProxyFactory로 생성한) 는 Advisor 호출<br>
(3)-> Advisor는 PointCut 필터를 호출<br>
(4)-> PointCut 필터에 통과되어 적용 가능하면 Advice를 호출<br>
(5)-> Advice는 InvocationHandler 또는 MethodInterceptor를 호출<br>
(6)-> InvocationHandler 또는 MethodInterceptor는 타겟을 호출<br>
(7)-> 타겟은 실제 로직(Method)을 수행<br>
<br>

### 구현

> target

```java

    public interface ServiceInterface {
        void save();

        void find();
    }

    public class ServiceImpl implements ServiceInterface {
        @Override
        public void save() {
            System.out.println("save");
        }
        @Override
        public void find() {
            log.info("find 호출");
        }
    }

```

* save, find 메서드를 가진 구현체이다.

<br>

> ProxyFactory + Advisor


```java

@Test
@DisplayName("스프링이 제공하는 포인트컷")
    void advisorTest3() {
            ServiceInterface target = new ServiceImpl();
            ProxyFactory proxyFactory = new ProxyFactory(target);

            /**
             * 메소드 네임이 save인 경우에만 허용
             */
            NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
            nameMatchMethodPointcut.setMappedName("save");

            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(nameMatchMethodPointcut , new TimeAdvice());
            proxyFactory.addAdvisor(advisor);

            ServiceInterface proxy =    (ServiceInterface) proxyFactory.getProxy();

            proxy.save();
            proxy.find();

            }
```

* `new ProxyFactory(target)` : 타겟을 넣어 프록시 팩토리를 생성한다.
* `new TimeAdvice()` : `import org.aopalliance.intercept.MethodInterceptor` 의 invoke 메서드를 구현한 구현체이다. (Advice)
* `new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice())` : 기본 PointCut을 가진 Advisor를 생성한다.
* `proxyFactory.addAdvisor(advisor)` : Advisor를 추가한다.
* `ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();` : Proxy를 생성한다.


### PointCut 종류

* `NameMatchMethodPointcut` : 메서드 이름으로 허가
* `JdkRegexpMethodPointcut` : 정규식으로 허가
* `TruePointcut` : 모든 메서드 허가
* `AnnotationMatchingPointcut` : 어노테이션으로 허가
* `AspectJExpressionPointcut` : AspectJ 표현식으로 허가

실무에서는 `AspectJExpressionPointcut` : AspectJ 표현식으로 허가 를 많이 사용한다.





## MultiAdvisor

Target에 여러 부가기능이 달리게 한다.


> advisor1,2 제작

```java

    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("Advice1 실행");
            return invocation.proceed();
        }
    }

    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("Advice2 실행");
            return invocation.proceed();
        }
    }

```


> 적용

```java

    @Test
    @DisplayName("proxy -> multiAdvisor")
    void multiAdvisorTest2() {

        //advisor1,2 생성
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());

        ServiceInterface target = new ServiceImpl();

        ProxyFactory proxyFactory = new ProxyFactory(target);

        //등록 순서대로 먼저 실행된다 proxy2 -> proxy1
        proxyFactory.addAdvisor(advisor2);
        proxyFactory.addAdvisor(advisor1);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
    }
```


### 💡참고사항

Spring AOP에서도 이와 같이 객체당 Proxy 객체는 하나만 만들고,<br>
Advisor를 여러개 적용하여 넣는 방식으로 사용된다. (Proxy 객체가 여러개 생긴다고 생각하면 안됨)<br>


### 적용 예시

```java

private Advisor getAdvisor(LogTrace logTrace) {
        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");
        //advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);

        }
```


## 빈 후처리기 (Bean Post Processor)

요즘은 ComponentScan을 사용하여 어노테이션으로 다 Bean을 자동으로 등록하고,<br>
직접 설정을 하는 경우가 드물기 때문에, Proxy를 등록할 때 빈 후처리기를 사용한다.<br>

Bean을 등록할 때 BeanPostProcessor를 사용하여 Bean을 후처리한다.<br>



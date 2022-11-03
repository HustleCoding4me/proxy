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

두 디자인 패턴은 의도가 다르다.<br>
`Proxy Pattern`은 대리자를 통해 기능을 제공하는 것이 목적이고(접근 제어),<br>
`Decorator Pattern`은 기능을 추가하는 것이 목적이다.<br>




